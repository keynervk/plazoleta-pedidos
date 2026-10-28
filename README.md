# Microservicio Pedidos

Ciclo de vida de los pedidos de la plazoleta de comidas (HU 11 a 18). Puerto **8083**, base de datos **`pedidos_db`**.

Misma arquitectura hexagonal que `usuarios` y `plazoleta`: `domain` (Java puro, sin Spring), `application`
(DTOs, mappers, handlers transaccionales) e `infrastructure` (REST, JPA, clientes HTTP, seguridad).
Nunca accede a las bases de datos de los otros microservicios: todo lo consulta por HTTP.

## Cómo levantarlo

1. Crear la base de datos (Postgres no la crea sola): `CREATE DATABASE pedidos_db;`
2. Variables de entorno: `DB_PASSWORD` (obligatoria) y `JWT_SECRET` (opcional; debe ser **la misma** que en
   `usuarios` y `plazoleta`, el valor por defecto ya coincide).
3. Levantar `usuarios` (8081) y `plazoleta` (8082), y luego `./mvnw spring-boot:run`.
4. Swagger: <http://localhost:8083/swagger-ui.html>. Inicia sesión con `POST http://localhost:8081/usuarios/login`
   y pega el token en **Authorize**.

Pruebas unitarias: `./mvnw test`. Todos los casos de uso están cubiertos: camino feliz, cada regla de negocio y cada transición inválida.

## Endpoints

| HU | Método y ruta | Rol | Qué hace |
|----|---------------|-----|----------|
| 11 | `POST /pedidos` | CLIENTE | Crea el pedido en `PENDIENTE`. Body: `{ "idRestaurante": 1, "platos": [{ "idPlato": 3, "cantidad": 2 }] }` |
| 12 | `GET /pedidos?estado=PENDIENTE&pagina=0&tamanioPagina=10` | EMPLEADO | Pedidos del restaurante del empleado, del más antiguo al más reciente |
| 13 | `PATCH /pedidos/{id}/asignar` | EMPLEADO | `PENDIENTE` → `EN_PREPARACION` y guarda el empleado asignado |
| 14 | `PATCH /pedidos/{id}/listo` | EMPLEADO | `EN_PREPARACION` → `LISTO`, genera el PIN y "envía" el SMS |
| 15 | `PATCH /pedidos/{id}/entregar` | EMPLEADO | `LISTO` → `ENTREGADO` si el PIN coincide. Body: `{ "pin": "482913" }` |
| 16 | `PATCH /pedidos/{id}/cancelar` | CLIENTE | `PENDIENTE` → `CANCELADO`, solo si el pedido es del cliente |
| 17 | `GET /pedidos/{id}/trazabilidad` | CLIENTE | Historial de cambios de estado de un pedido propio |
| 18 | `GET /pedidos/eficiencia?idRestaurante=1` | PROPIETARIO | Tiempo de cada pedido entregado y tiempo medio por empleado |

Los errores siguen el formato común `{ "status": <código>, "mensaje": "..." }`:
400 (datos o PIN inválidos), 401 (sin token o token inválido), 403 (rol incorrecto o recurso ajeno),
404 (pedido o restaurante inexistente), 409 (transición de estado no permitida o pedido activo existente) y
503 (Usuarios o Plazoleta no responden).

## Decisiones de diseño

- **Relación empleado-restaurante**: se guarda en `usuarios` (`id_restaurante` del empleado, validado contra
  Plazoleta al crear el empleado). Pedidos la consulta con `GET /usuarios/{id}`, que también da el celular del
  cliente para el SMS. Ese endpoint exige JWT, así que Pedidos reenvía el token de la petición en curso
  (`RestTemplateConfiguration`).
- **Precio**: cada plato del pedido guarda el precio que tenía al momento de pedir (`pedido_platos.precio_unitario`).
- **PIN**: 6 dígitos con `SecureRandom`. **No se devuelve en ninguna respuesta** (ni siquiera al listar pedidos
  para el empleado); solo llega al cliente por SMS. Si lo viera el empleado, dejaría de servir como prueba de
  que el cliente está presente.
- **SMS simulado**: `SmsSimuladoAdapter` escribe en consola `[SMS SIMULADO] Para: ... | Mensaje: ...`. Para
  integrar Twilio o AWS SNS basta con otro adaptador de `INotificacionPort` (en el código está comentado dónde).
- **Trazabilidad**: toda transición pasa por `PedidoUseCase.cambiarEstado`, que inserta en `pedido_estado_log`
  dentro de la misma transacción que el cambio de estado. La creación queda registrada como `null → PENDIENTE`.
- **Eficiencia**: solo cuenta pedidos `ENTREGADO` con trazabilidad completa (`PENDIENTE` y `ENTREGADO`); los
  tiempos se reportan en minutos y ordenados del más rápido al más lento.
- **Estados finales**: `ENTREGADO` y `CANCELADO` no admiten más transiciones. Cancelar algo que no esté
  `PENDIENTE` responde siempre *"Lo sentimos, tu pedido ya está en preparación y no puede cancelarse"*.
- **Un pedido activo por cliente**: la validación se hace en el dominio. Dos peticiones simultáneas del mismo
  cliente podrían pasar ambas; si eso importa, se resolvería con un índice único parcial en Postgres.

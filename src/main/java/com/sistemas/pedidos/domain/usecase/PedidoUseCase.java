package com.sistemas.pedidos.domain.usecase;

import com.sistemas.pedidos.domain.api.IPedidoServicePort;
import com.sistemas.pedidos.domain.exception.EstadoPedidoInvalidoException;
import com.sistemas.pedidos.domain.exception.NoAutorizadoException;
import com.sistemas.pedidos.domain.exception.PedidoActivoExistenteException;
import com.sistemas.pedidos.domain.exception.PedidoNoEncontradoException;
import com.sistemas.pedidos.domain.exception.PinInvalidoException;
import com.sistemas.pedidos.domain.exception.ValidacionException;
import com.sistemas.pedidos.domain.model.EstadoPedido;
import com.sistemas.pedidos.domain.model.Pedido;
import com.sistemas.pedidos.domain.model.PedidoPlato;
import com.sistemas.pedidos.domain.model.Plato;
import com.sistemas.pedidos.domain.model.Restaurante;
import com.sistemas.pedidos.domain.spi.INotificacionPort;
import com.sistemas.pedidos.domain.spi.IPedidoPersistencePort;
import com.sistemas.pedidos.domain.spi.IPlazoletaExternoPort;
import com.sistemas.pedidos.domain.spi.IUsuarioExternoPort;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PedidoUseCase implements IPedidoServicePort {

    private static final List<EstadoPedido> ESTADOS_ACTIVOS =
            List.of(EstadoPedido.PENDIENTE, EstadoPedido.EN_PREPARACION, EstadoPedido.LISTO);
    private static final SecureRandom GENERADOR_PIN = new SecureRandom();

    private final IPedidoPersistencePort pedidoPersistencePort;
    private final IPlazoletaExternoPort plazoletaExternoPort;
    private final IUsuarioExternoPort usuarioExternoPort;
    private final INotificacionPort notificacionPort;

    public PedidoUseCase(IPedidoPersistencePort pedidoPersistencePort,
                         IPlazoletaExternoPort plazoletaExternoPort,
                         IUsuarioExternoPort usuarioExternoPort,
                         INotificacionPort notificacionPort) {
        this.pedidoPersistencePort = pedidoPersistencePort;
        this.plazoletaExternoPort = plazoletaExternoPort;
        this.usuarioExternoPort = usuarioExternoPort;
        this.notificacionPort = notificacionPort;
    }

    @Override
    public Pedido crearPedido(Pedido pedido, Long idCliente) {
        validarEstructuraDelPedido(pedido);

        if (pedidoPersistencePort.existePedidoDeClienteEnEstados(idCliente, ESTADOS_ACTIVOS)) {
            throw new PedidoActivoExistenteException(
                    "Ya tienes un pedido en proceso; podras hacer otro cuando sea entregado o lo canceles");
        }

        Long idRestaurante = pedido.getIdRestaurante();
        Restaurante restaurante = plazoletaExternoPort.buscarRestaurante(idRestaurante);
        if (restaurante == null) {
            throw new ValidacionException("El restaurante indicado no existe");
        }

        Map<Long, Plato> menu = new HashMap<>();
        for (Plato plato : plazoletaExternoPort.listarPlatosDeRestaurante(idRestaurante)) {
            menu.put(plato.getId(), plato);
        }

        for (PedidoPlato item : pedido.getPlatos()) {
            Plato plato = menu.get(item.getIdPlato());
            if (plato == null || !idRestaurante.equals(plato.getIdRestaurante())) {
                throw new ValidacionException("El plato con id " + item.getIdPlato()
                        + " no existe o no pertenece al restaurante indicado");
            }
            if (!plato.isActivo()) {
                throw new ValidacionException("El plato " + plato.getNombre() + " no esta disponible en este momento");
            }
            item.setPrecioUnitario(plato.getPrecio());
        }

        pedido.setIdCliente(idCliente);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setIdEmpleadoAsignado(null);
        pedido.setPin(null);
        pedido.setFechaCreacion(LocalDateTime.now());
        return pedidoPersistencePort.guardarPedido(pedido);
    }

    @Override
    public List<Pedido> listarPedidosPorEstado(EstadoPedido estado, int pagina, int tamanioPagina, Long idEmpleado) {
        if (estado == null) {
            throw new ValidacionException("El estado es obligatorio");
        }
        if (pagina < 0) {
            throw new ValidacionException("El numero de pagina no puede ser negativo");
        }
        if (tamanioPagina <= 0) {
            throw new ValidacionException("El tamanio de la pagina debe ser mayor a 0");
        }

        Long idRestaurante = obtenerRestauranteDelEmpleado(idEmpleado);
        return pedidoPersistencePort.listarPorRestauranteYEstado(idRestaurante, estado, pagina, tamanioPagina);
    }

    @Override
    public Pedido asignarPedido(Long idPedido, Long idEmpleado) {
        Pedido pedido = obtenerPedidoDelRestauranteDelEmpleado(idPedido, idEmpleado);
        validarEstadoActual(pedido, EstadoPedido.PENDIENTE, "Solo se pueden asignar pedidos en estado PENDIENTE");

        pedido.setEstado(EstadoPedido.EN_PREPARACION);
        pedido.setIdEmpleadoAsignado(idEmpleado);
        return pedidoPersistencePort.actualizarPedido(pedido);
    }

    @Override
    public Pedido marcarPedidoListo(Long idPedido, Long idEmpleado) {
        Pedido pedido = obtenerPedidoDelRestauranteDelEmpleado(idPedido, idEmpleado);
        validarEstadoActual(pedido, EstadoPedido.EN_PREPARACION,
                "Solo se pueden marcar como listos los pedidos en estado EN_PREPARACION");

        String pin = generarPin();
        pedido.setEstado(EstadoPedido.LISTO);
        pedido.setPin(pin);
        Pedido actualizado = pedidoPersistencePort.actualizarPedido(pedido);

        String celular = usuarioExternoPort.obtenerCelular(pedido.getIdCliente());
        notificacionPort.enviarSms(celular, "Tu pedido #" + pedido.getId()
                + " esta listo para recoger. Presenta este PIN de seguridad al reclamarlo: " + pin);
        return actualizado;
    }

    @Override
    public Pedido entregarPedido(Long idPedido, String pin, Long idEmpleado) {
        if (pin == null || pin.isBlank()) {
            throw new ValidacionException("El PIN de seguridad es obligatorio para entregar el pedido");
        }

        Pedido pedido = obtenerPedidoDelRestauranteDelEmpleado(idPedido, idEmpleado);
        validarEstadoActual(pedido, EstadoPedido.LISTO, "Solo se pueden entregar pedidos en estado LISTO");

        if (!pin.trim().equals(pedido.getPin())) {
            throw new PinInvalidoException("El PIN de seguridad no coincide con el del pedido");
        }

        pedido.setEstado(EstadoPedido.ENTREGADO);
        return pedidoPersistencePort.actualizarPedido(pedido);
    }

    private String generarPin() {
        return String.format("%06d", GENERADOR_PIN.nextInt(1_000_000));
    }

    private Pedido obtenerPedido(Long idPedido) {
        if (idPedido == null) {
            throw new ValidacionException("El id del pedido es obligatorio");
        }
        Pedido pedido = pedidoPersistencePort.buscarPorId(idPedido);
        if (pedido == null) {
            throw new PedidoNoEncontradoException("No existe un pedido con ese id");
        }
        return pedido;
    }

    private Pedido obtenerPedidoDelRestauranteDelEmpleado(Long idPedido, Long idEmpleado) {
        Pedido pedido = obtenerPedido(idPedido);
        Long idRestaurante = obtenerRestauranteDelEmpleado(idEmpleado);
        if (!idRestaurante.equals(pedido.getIdRestaurante())) {
            throw new NoAutorizadoException("Este pedido no pertenece al restaurante en el que trabajas");
        }
        return pedido;
    }

    private void validarEstadoActual(Pedido pedido, EstadoPedido estadoRequerido, String mensaje) {
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new EstadoPedidoInvalidoException("El pedido ya fue entregado y no puede cambiar de estado");
        }
        if (pedido.getEstado() != estadoRequerido) {
            throw new EstadoPedidoInvalidoException(mensaje);
        }
    }

    private Long obtenerRestauranteDelEmpleado(Long idEmpleado) {
        Long idRestaurante = usuarioExternoPort.obtenerIdRestauranteDeEmpleado(idEmpleado);
        if (idRestaurante == null) {
            throw new NoAutorizadoException("No tienes un restaurante asignado como empleado");
        }
        return idRestaurante;
    }

    private void validarEstructuraDelPedido(Pedido pedido) {
        if (pedido == null) {
            throw new ValidacionException("El pedido es obligatorio");
        }
        if (pedido.getIdRestaurante() == null) {
            throw new ValidacionException("El id del restaurante es obligatorio");
        }
        if (pedido.getPlatos() == null || pedido.getPlatos().isEmpty()) {
            throw new ValidacionException("El pedido debe tener al menos un plato");
        }

        Set<Long> idsPlatos = new HashSet<>();
        for (PedidoPlato item : pedido.getPlatos()) {
            if (item == null || item.getIdPlato() == null) {
                throw new ValidacionException("Cada plato del pedido debe indicar su id");
            }
            if (item.getCantidad() == null || item.getCantidad() <= 0) {
                throw new ValidacionException("La cantidad de cada plato debe ser un numero entero mayor a 0");
            }
            if (!idsPlatos.add(item.getIdPlato())) {
                throw new ValidacionException("El plato con id " + item.getIdPlato()
                        + " esta repetido; indica la cantidad total en un solo item");
            }
        }
    }
}
package com.sistemas.pedidos.infrastructure.input.rest;

import com.sistemas.pedidos.application.dto.EntregarPedidoRequestDto;
import com.sistemas.pedidos.application.dto.PedidoRequestDto;
import com.sistemas.pedidos.application.dto.PedidoResponseDto;
import com.sistemas.pedidos.application.handler.PedidoHandler;
import com.sistemas.pedidos.domain.model.EstadoPedido;
import com.sistemas.pedidos.infrastructure.configuration.UsuarioAutenticado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@Tag(name = "Pedidos", description = "Ciclo de vida de los pedidos de la plazoleta")
public class PedidoController {

    private final PedidoHandler pedidoHandler;

    public PedidoController(PedidoHandler pedidoHandler) {
        this.pedidoHandler = pedidoHandler;
    }

    private Long idUsuarioAutenticado() {
        return ((UsuarioAutenticado) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).id();
    }

    @Operation(summary = "HU 11: Realizar un pedido (requiere rol CLIENTE)")
    @ApiResponse(responseCode = "201", description = "Pedido creado en estado PENDIENTE")
    @ApiResponse(responseCode = "400", description = "Datos invalidos, restaurante inexistente o plato no disponible")
    @ApiResponse(responseCode = "409", description = "El cliente ya tiene un pedido en proceso")
    @PostMapping
    public ResponseEntity<PedidoResponseDto> crearPedido(@RequestBody PedidoRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoHandler.crearPedido(dto, idUsuarioAutenticado()));
    }

    @Operation(summary = "HU 12: Listar los pedidos del restaurante del empleado filtrando por estado, paginados "
            + "(requiere rol EMPLEADO)")
    @ApiResponse(responseCode = "200", description = "Pagina de pedidos, del mas antiguo al mas reciente")
    @ApiResponse(responseCode = "400", description = "Estado o parametros de paginacion invalidos")
    @ApiResponse(responseCode = "403", description = "El empleado no tiene un restaurante asignado")
    @GetMapping
    public ResponseEntity<List<PedidoResponseDto>> listarPorEstado(
            @Parameter(description = "Estado de los pedidos a listar") @RequestParam EstadoPedido estado,
            @Parameter(description = "Numero de pagina, empieza en 0") @RequestParam(defaultValue = "0") int pagina,
            @Parameter(description = "Cantidad de elementos por pagina") @RequestParam(defaultValue = "10") int tamanioPagina) {
        return ResponseEntity.ok(pedidoHandler.listarPorEstado(estado, pagina, tamanioPagina, idUsuarioAutenticado()));
    }

    @Operation(summary = "HU 13: Asignarse un pedido PENDIENTE, que pasa a EN_PREPARACION (requiere rol EMPLEADO)")
    @ApiResponse(responseCode = "200", description = "Pedido asignado al empleado autenticado")
    @ApiResponse(responseCode = "403", description = "El pedido no es del restaurante del empleado")
    @ApiResponse(responseCode = "404", description = "El pedido no existe")
    @ApiResponse(responseCode = "409", description = "El pedido no esta en estado PENDIENTE")
    @PatchMapping("/{id}/asignar")
    public ResponseEntity<PedidoResponseDto> asignarPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoHandler.asignarPedido(id, idUsuarioAutenticado()));
    }

    @Operation(summary = "HU 14: Marcar un pedido EN_PREPARACION como LISTO y notificar al cliente por SMS con su PIN "
            + "(requiere rol EMPLEADO)")
    @ApiResponse(responseCode = "200", description = "Pedido listo; el PIN se envia al cliente (SMS simulado en consola)")
    @ApiResponse(responseCode = "403", description = "El pedido no es del restaurante del empleado")
    @ApiResponse(responseCode = "404", description = "El pedido no existe")
    @ApiResponse(responseCode = "409", description = "El pedido no esta EN_PREPARACION")
    @PatchMapping("/{id}/listo")
    public ResponseEntity<PedidoResponseDto> marcarPedidoListo(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoHandler.marcarPedidoListo(id, idUsuarioAutenticado()));
    }

    @Operation(summary = "HU 15: Entregar un pedido LISTO validando el PIN del cliente (requiere rol EMPLEADO)")
    @ApiResponse(responseCode = "200", description = "Pedido entregado")
    @ApiResponse(responseCode = "400", description = "PIN ausente o incorrecto")
    @ApiResponse(responseCode = "403", description = "El pedido no es del restaurante del empleado")
    @ApiResponse(responseCode = "404", description = "El pedido no existe")
    @ApiResponse(responseCode = "409", description = "El pedido no esta LISTO o ya fue entregado")
    @PatchMapping("/{id}/entregar")
    public ResponseEntity<PedidoResponseDto> entregarPedido(@PathVariable Long id,
                                                            @RequestBody EntregarPedidoRequestDto dto) {
        return ResponseEntity.ok(pedidoHandler.entregarPedido(id, dto, idUsuarioAutenticado()));
    }
}

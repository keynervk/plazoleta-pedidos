package com.sistemas.pedidos.infrastructure.input.rest;

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
}
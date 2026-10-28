package com.sistemas.pedidos.infrastructure.input.rest;

import com.sistemas.pedidos.application.dto.PedidoEstadoLogResponseDto;
import com.sistemas.pedidos.application.handler.TrazabilidadHandler;
import com.sistemas.pedidos.infrastructure.configuration.UsuarioAutenticado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@Tag(name = "Trazabilidad", description = "Historial de cambios de estado de los pedidos")
public class TrazabilidadController {

    private final TrazabilidadHandler trazabilidadHandler;

    public TrazabilidadController(TrazabilidadHandler trazabilidadHandler) {
        this.trazabilidadHandler = trazabilidadHandler;
    }

    private Long idUsuarioAutenticado() {
        return ((UsuarioAutenticado) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).id();
    }

    @Operation(summary = "HU 17: Consultar la trazabilidad de un pedido propio (requiere rol CLIENTE)")
    @ApiResponse(responseCode = "200", description = "Cambios de estado del pedido, del mas antiguo al mas reciente")
    @ApiResponse(responseCode = "403", description = "El pedido no pertenece al cliente autenticado")
    @ApiResponse(responseCode = "404", description = "El pedido no existe")
    @GetMapping("/{id}/trazabilidad")
    public ResponseEntity<List<PedidoEstadoLogResponseDto>> consultarTrazabilidad(@PathVariable Long id) {
        return ResponseEntity.ok(trazabilidadHandler.consultarTrazabilidad(id, idUsuarioAutenticado()));
    }
}
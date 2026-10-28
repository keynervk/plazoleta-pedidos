package com.sistemas.pedidos.infrastructure.input.rest;

import com.sistemas.pedidos.application.dto.EficienciaResponseDto;
import com.sistemas.pedidos.application.handler.EficienciaHandler;
import com.sistemas.pedidos.infrastructure.configuration.UsuarioAutenticado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos/eficiencia")
@Tag(name = "Eficiencia", description = "Tiempos de atencion de los pedidos de un restaurante")
public class EficienciaController {

    private final EficienciaHandler eficienciaHandler;

    public EficienciaController(EficienciaHandler eficienciaHandler) {
        this.eficienciaHandler = eficienciaHandler;
    }

    private Long idUsuarioAutenticado() {
        return ((UsuarioAutenticado) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).id();
    }

    @Operation(summary = "HU 18: Tiempo de cada pedido entregado y tiempo medio por empleado, del mas rapido al "
            + "mas lento (requiere rol PROPIETARIO, dueno del restaurante)")
    @ApiResponse(responseCode = "200", description = "Reporte de eficiencia del restaurante")
    @ApiResponse(responseCode = "403", description = "El restaurante no pertenece al propietario autenticado")
    @ApiResponse(responseCode = "404", description = "El restaurante no existe")
    @GetMapping
    public ResponseEntity<EficienciaResponseDto> consultarEficiencia(
            @Parameter(description = "Id del restaurante") @RequestParam Long idRestaurante) {
        return ResponseEntity.ok(eficienciaHandler.consultarEficiencia(idRestaurante, idUsuarioAutenticado()));
    }
}
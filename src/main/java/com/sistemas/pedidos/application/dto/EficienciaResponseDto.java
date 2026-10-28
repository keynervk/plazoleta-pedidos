package com.sistemas.pedidos.application.dto;

import java.util.List;

/**
 * Pedidos y empleados ordenados del mas rapido al mas lento.
 */
public record EficienciaResponseDto(
        Long idRestaurante,
        List<TiempoPedidoResponseDto> pedidos,
        List<RankingEmpleadoResponseDto> empleados) {
}
package com.sistemas.pedidos.application.dto;

import java.time.LocalDateTime;

public record TiempoPedidoResponseDto(
        Long idPedido,
        Long idEmpleado,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        double tiempoMinutos) {
}
package com.sistemas.pedidos.application.dto;

public record RankingEmpleadoResponseDto(
        Long idEmpleado,
        int pedidosAtendidos,
        double tiempoPromedioMinutos) {
}
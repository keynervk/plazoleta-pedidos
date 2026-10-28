package com.sistemas.pedidos.application.dto;

import com.sistemas.pedidos.domain.model.EstadoPedido;

import java.time.LocalDateTime;

public record PedidoEstadoLogResponseDto(
        Long idPedido,
        EstadoPedido estadoAnterior,
        EstadoPedido estadoNuevo,
        LocalDateTime fechaCambio) {
}
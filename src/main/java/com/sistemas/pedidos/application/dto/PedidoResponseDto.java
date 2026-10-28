package com.sistemas.pedidos.application.dto;

import com.sistemas.pedidos.domain.model.EstadoPedido;

import java.time.LocalDateTime;
import java.util.List;

/**
 * El PIN de seguridad no se expone: solo lo recibe el cliente por SMS.
 */
public record PedidoResponseDto(
        Long id,
        Long idCliente,
        Long idRestaurante,
        EstadoPedido estado,
        Long idEmpleadoAsignado,
        LocalDateTime fechaCreacion,
        List<PedidoPlatoResponseDto> platos) {
}
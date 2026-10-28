package com.sistemas.pedidos.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PedidoRequestDto(
        @Schema(example = "1") Long idRestaurante,
        List<PedidoPlatoRequestDto> platos) {
}
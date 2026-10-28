package com.sistemas.pedidos.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PedidoPlatoRequestDto(
        @Schema(example = "3") Long idPlato,
        @Schema(example = "2") Integer cantidad) {
}
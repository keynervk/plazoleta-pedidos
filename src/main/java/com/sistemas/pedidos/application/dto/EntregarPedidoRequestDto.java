package com.sistemas.pedidos.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record EntregarPedidoRequestDto(
        @Schema(example = "482913", description = "PIN que el cliente recibio por SMS") String pin) {
}
package com.sistemas.pedidos.domain.model;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Tiempo que tardo un pedido desde que se creo (PENDIENTE) hasta que se entrego (ENTREGADO).
 */
public record TiempoPedido(Long idPedido, Long idEmpleado, LocalDateTime inicio, LocalDateTime fin,
                           Duration duracion) {
}
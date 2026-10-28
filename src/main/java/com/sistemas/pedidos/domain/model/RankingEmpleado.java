package com.sistemas.pedidos.domain.model;

import java.time.Duration;

/**
 * Tiempo medio de los pedidos que un empleado atendio, desde que se crearon hasta que se entregaron.
 */
public record RankingEmpleado(Long idEmpleado, int pedidosAtendidos, Duration tiempoPromedio) {
}
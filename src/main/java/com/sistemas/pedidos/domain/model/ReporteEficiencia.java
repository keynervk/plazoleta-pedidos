package com.sistemas.pedidos.domain.model;

import java.util.List;

public record ReporteEficiencia(Long idRestaurante, List<TiempoPedido> pedidos, List<RankingEmpleado> empleados) {
}
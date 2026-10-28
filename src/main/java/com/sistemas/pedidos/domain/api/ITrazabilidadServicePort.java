package com.sistemas.pedidos.domain.api;

import com.sistemas.pedidos.domain.model.PedidoEstadoLog;

import java.util.List;

public interface ITrazabilidadServicePort {

    List<PedidoEstadoLog> consultarTrazabilidad(Long idPedido, Long idCliente);
}
package com.sistemas.pedidos.domain.spi;

import com.sistemas.pedidos.domain.model.PedidoEstadoLog;

import java.util.List;

public interface IPedidoEstadoLogPersistencePort {

    void registrarCambio(PedidoEstadoLog cambio);

    List<PedidoEstadoLog> listarPorPedido(Long idPedido);
}
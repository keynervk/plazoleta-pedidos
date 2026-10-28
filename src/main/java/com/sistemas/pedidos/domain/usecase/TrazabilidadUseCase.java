package com.sistemas.pedidos.domain.usecase;

import com.sistemas.pedidos.domain.api.ITrazabilidadServicePort;
import com.sistemas.pedidos.domain.exception.NoAutorizadoException;
import com.sistemas.pedidos.domain.exception.PedidoNoEncontradoException;
import com.sistemas.pedidos.domain.exception.ValidacionException;
import com.sistemas.pedidos.domain.model.Pedido;
import com.sistemas.pedidos.domain.model.PedidoEstadoLog;
import com.sistemas.pedidos.domain.spi.IPedidoEstadoLogPersistencePort;
import com.sistemas.pedidos.domain.spi.IPedidoPersistencePort;

import java.util.Comparator;
import java.util.List;

public class TrazabilidadUseCase implements ITrazabilidadServicePort {

    private final IPedidoPersistencePort pedidoPersistencePort;
    private final IPedidoEstadoLogPersistencePort pedidoEstadoLogPersistencePort;

    public TrazabilidadUseCase(IPedidoPersistencePort pedidoPersistencePort,
                               IPedidoEstadoLogPersistencePort pedidoEstadoLogPersistencePort) {
        this.pedidoPersistencePort = pedidoPersistencePort;
        this.pedidoEstadoLogPersistencePort = pedidoEstadoLogPersistencePort;
    }

    @Override
    public List<PedidoEstadoLog> consultarTrazabilidad(Long idPedido, Long idCliente) {
        if (idPedido == null) {
            throw new ValidacionException("El id del pedido es obligatorio");
        }

        Pedido pedido = pedidoPersistencePort.buscarPorId(idPedido);
        if (pedido == null) {
            throw new PedidoNoEncontradoException("No existe un pedido con ese id");
        }
        if (!pedido.getIdCliente().equals(idCliente)) {
            throw new NoAutorizadoException("Solo puedes consultar la trazabilidad de tus propios pedidos");
        }

        return pedidoEstadoLogPersistencePort.listarPorPedido(idPedido).stream()
                .sorted(Comparator.comparing(PedidoEstadoLog::getFechaCambio))
                .toList();
    }
}
package com.sistemas.pedidos.infrastructure.output.jpa.mapper;

import com.sistemas.pedidos.domain.model.PedidoEstadoLog;
import com.sistemas.pedidos.infrastructure.output.jpa.entity.PedidoEstadoLogEntity;
import org.springframework.stereotype.Component;

@Component
public class PedidoEstadoLogEntityMapper {

    public PedidoEstadoLogEntity toEntity(PedidoEstadoLog log) {
        PedidoEstadoLogEntity entity = new PedidoEstadoLogEntity();
        entity.setId(log.getId());
        entity.setIdPedido(log.getIdPedido());
        entity.setEstadoAnterior(log.getEstadoAnterior());
        entity.setEstadoNuevo(log.getEstadoNuevo());
        entity.setFechaCambio(log.getFechaCambio());
        return entity;
    }

    public PedidoEstadoLog toModel(PedidoEstadoLogEntity entity) {
        return new PedidoEstadoLog(
                entity.getId(),
                entity.getIdPedido(),
                entity.getEstadoAnterior(),
                entity.getEstadoNuevo(),
                entity.getFechaCambio());
    }
}
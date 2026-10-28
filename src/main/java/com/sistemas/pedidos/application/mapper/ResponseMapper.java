package com.sistemas.pedidos.application.mapper;

import com.sistemas.pedidos.application.dto.PedidoEstadoLogResponseDto;
import com.sistemas.pedidos.application.dto.PedidoPlatoResponseDto;
import com.sistemas.pedidos.application.dto.PedidoResponseDto;
import com.sistemas.pedidos.domain.model.Pedido;
import com.sistemas.pedidos.domain.model.PedidoEstadoLog;
import org.springframework.stereotype.Component;

@Component
public class ResponseMapper {

    public PedidoResponseDto toDto(Pedido p) {
        var platos = p.getPlatos().stream()
                .map(item -> new PedidoPlatoResponseDto(item.getIdPlato(), item.getCantidad(), item.getPrecioUnitario()))
                .toList();
        return new PedidoResponseDto(p.getId(), p.getIdCliente(), p.getIdRestaurante(), p.getEstado(),
                p.getIdEmpleadoAsignado(), p.getFechaCreacion(), platos);
    }

    public PedidoEstadoLogResponseDto toDto(PedidoEstadoLog log) {
        return new PedidoEstadoLogResponseDto(log.getIdPedido(), log.getEstadoAnterior(), log.getEstadoNuevo(),
                log.getFechaCambio());
    }
}
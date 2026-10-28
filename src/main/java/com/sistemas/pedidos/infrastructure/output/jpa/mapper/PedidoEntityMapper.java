package com.sistemas.pedidos.infrastructure.output.jpa.mapper;

import com.sistemas.pedidos.domain.model.Pedido;
import com.sistemas.pedidos.domain.model.PedidoPlato;
import com.sistemas.pedidos.infrastructure.output.jpa.entity.PedidoEntity;
import com.sistemas.pedidos.infrastructure.output.jpa.entity.PedidoPlatoEmbeddable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class PedidoEntityMapper {

    public PedidoEntity toEntity(Pedido pedido) {
        PedidoEntity entity = new PedidoEntity();
        entity.setId(pedido.getId());
        entity.setIdCliente(pedido.getIdCliente());
        entity.setIdRestaurante(pedido.getIdRestaurante());
        entity.setEstado(pedido.getEstado());
        entity.setIdEmpleadoAsignado(pedido.getIdEmpleadoAsignado());
        entity.setPin(pedido.getPin());
        entity.setFechaCreacion(pedido.getFechaCreacion());
        entity.setPlatos(new ArrayList<>(pedido.getPlatos().stream()
                .map(p -> new PedidoPlatoEmbeddable(p.getIdPlato(), p.getCantidad(), p.getPrecioUnitario()))
                .toList()));
        return entity;
    }

    public Pedido toModel(PedidoEntity entity) {
        return new Pedido(
                entity.getId(),
                entity.getIdCliente(),
                entity.getIdRestaurante(),
                entity.getEstado(),
                entity.getIdEmpleadoAsignado(),
                entity.getPin(),
                entity.getFechaCreacion(),
                new ArrayList<>(entity.getPlatos().stream()
                        .map(p -> new PedidoPlato(p.getIdPlato(), p.getCantidad(), p.getPrecioUnitario()))
                        .toList()));
    }
}
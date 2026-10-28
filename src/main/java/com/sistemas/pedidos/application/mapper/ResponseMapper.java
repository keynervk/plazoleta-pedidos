package com.sistemas.pedidos.application.mapper;

import com.sistemas.pedidos.application.dto.PedidoPlatoResponseDto;
import com.sistemas.pedidos.application.dto.PedidoResponseDto;
import com.sistemas.pedidos.domain.model.Pedido;
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
}
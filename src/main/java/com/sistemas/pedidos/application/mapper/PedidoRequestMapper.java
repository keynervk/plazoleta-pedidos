package com.sistemas.pedidos.application.mapper;

import com.sistemas.pedidos.application.dto.PedidoRequestDto;
import com.sistemas.pedidos.domain.model.Pedido;
import com.sistemas.pedidos.domain.model.PedidoPlato;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PedidoRequestMapper {

    public Pedido toModel(PedidoRequestDto dto) {
        Pedido pedido = new Pedido();
        pedido.setIdRestaurante(dto.idRestaurante());

        List<PedidoPlato> platos = new ArrayList<>();
        if (dto.platos() != null) {
            dto.platos().forEach(item -> platos.add(
                    item == null ? null : new PedidoPlato(item.idPlato(), item.cantidad(), null)));
        }
        pedido.setPlatos(platos);
        return pedido;
    }
}
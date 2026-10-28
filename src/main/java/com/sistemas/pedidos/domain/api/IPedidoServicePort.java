package com.sistemas.pedidos.domain.api;

import com.sistemas.pedidos.domain.model.Pedido;

public interface IPedidoServicePort {

    Pedido crearPedido(Pedido pedido, Long idCliente);
}
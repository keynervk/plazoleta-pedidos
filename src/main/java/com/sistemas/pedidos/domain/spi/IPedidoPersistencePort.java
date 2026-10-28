package com.sistemas.pedidos.domain.spi;

import com.sistemas.pedidos.domain.model.EstadoPedido;
import com.sistemas.pedidos.domain.model.Pedido;

import java.util.List;

public interface IPedidoPersistencePort {

    Pedido guardarPedido(Pedido pedido);

    boolean existePedidoDeClienteEnEstados(Long idCliente, List<EstadoPedido> estados);
}
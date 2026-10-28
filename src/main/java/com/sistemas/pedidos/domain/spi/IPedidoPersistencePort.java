package com.sistemas.pedidos.domain.spi;

import com.sistemas.pedidos.domain.model.EstadoPedido;
import com.sistemas.pedidos.domain.model.Pedido;

import java.util.List;

public interface IPedidoPersistencePort {

    Pedido guardarPedido(Pedido pedido);

    Pedido buscarPorId(Long id);

    Pedido actualizarPedido(Pedido pedido);

    boolean existePedidoDeClienteEnEstados(Long idCliente, List<EstadoPedido> estados);

    List<Pedido> listarPorRestauranteYEstado(Long idRestaurante, EstadoPedido estado, int pagina, int tamanioPagina);

    List<Pedido> listarTodosPorRestauranteYEstado(Long idRestaurante, EstadoPedido estado);
}
package com.sistemas.pedidos.domain.api;

import com.sistemas.pedidos.domain.model.EstadoPedido;
import com.sistemas.pedidos.domain.model.Pedido;

import java.util.List;

public interface IPedidoServicePort {

    Pedido crearPedido(Pedido pedido, Long idCliente);

    List<Pedido> listarPedidosPorEstado(EstadoPedido estado, int pagina, int tamanioPagina, Long idEmpleado);

    Pedido asignarPedido(Long idPedido, Long idEmpleado);

    Pedido marcarPedidoListo(Long idPedido, Long idEmpleado);

    Pedido entregarPedido(Long idPedido, String pin, Long idEmpleado);

    Pedido cancelarPedido(Long idPedido, Long idCliente);
}
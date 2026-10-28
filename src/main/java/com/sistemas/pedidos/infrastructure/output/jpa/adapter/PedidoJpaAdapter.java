package com.sistemas.pedidos.infrastructure.output.jpa.adapter;

import com.sistemas.pedidos.domain.model.EstadoPedido;
import com.sistemas.pedidos.domain.model.Pedido;
import com.sistemas.pedidos.domain.spi.IPedidoPersistencePort;
import com.sistemas.pedidos.infrastructure.output.jpa.mapper.PedidoEntityMapper;
import com.sistemas.pedidos.infrastructure.output.jpa.repository.IPedidoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PedidoJpaAdapter implements IPedidoPersistencePort {

    private final IPedidoRepository pedidoRepository;
    private final PedidoEntityMapper mapper;

    public PedidoJpaAdapter(IPedidoRepository pedidoRepository, PedidoEntityMapper mapper) {
        this.pedidoRepository = pedidoRepository;
        this.mapper = mapper;
    }

    @Override
    public Pedido guardarPedido(Pedido pedido) {
        return mapper.toModel(pedidoRepository.save(mapper.toEntity(pedido)));
    }

    @Override
    public boolean existePedidoDeClienteEnEstados(Long idCliente, List<EstadoPedido> estados) {
        return pedidoRepository.existsByIdClienteAndEstadoIn(idCliente, estados);
    }
}
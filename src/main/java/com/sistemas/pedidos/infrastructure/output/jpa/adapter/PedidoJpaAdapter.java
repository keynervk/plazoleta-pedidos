package com.sistemas.pedidos.infrastructure.output.jpa.adapter;

import com.sistemas.pedidos.domain.model.EstadoPedido;
import com.sistemas.pedidos.domain.model.Pedido;
import com.sistemas.pedidos.domain.spi.IPedidoPersistencePort;
import com.sistemas.pedidos.infrastructure.output.jpa.entity.PedidoEntity;
import com.sistemas.pedidos.infrastructure.output.jpa.mapper.PedidoEntityMapper;
import com.sistemas.pedidos.infrastructure.output.jpa.repository.IPedidoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id).map(mapper::toModel).orElse(null);
    }

    @Override
    public Pedido actualizarPedido(Pedido pedido) {
        // Solo cambian los datos del flujo; los platos del pedido nunca se modifican despues de crearlo
        PedidoEntity entity = pedidoRepository.findById(pedido.getId())
                .orElseThrow(() -> new IllegalStateException("El pedido " + pedido.getId() + " no existe"));
        entity.setEstado(pedido.getEstado());
        entity.setIdEmpleadoAsignado(pedido.getIdEmpleadoAsignado());
        entity.setPin(pedido.getPin());
        return mapper.toModel(pedidoRepository.save(entity));
    }

    @Override
    public boolean existePedidoDeClienteEnEstados(Long idCliente, List<EstadoPedido> estados) {
        return pedidoRepository.existsByIdClienteAndEstadoIn(idCliente, estados);
    }

    @Override
    public List<Pedido> listarPorRestauranteYEstado(Long idRestaurante, EstadoPedido estado,
                                                   int pagina, int tamanioPagina) {
        var pageable = PageRequest.of(pagina, tamanioPagina, Sort.by(Sort.Direction.ASC, "fechaCreacion"));
        return pedidoRepository.findByIdRestauranteAndEstado(idRestaurante, estado, pageable)
                .stream().map(mapper::toModel).toList();
    }
}
package com.sistemas.pedidos.infrastructure.output.jpa.adapter;

import com.sistemas.pedidos.domain.model.PedidoEstadoLog;
import com.sistemas.pedidos.domain.spi.IPedidoEstadoLogPersistencePort;
import com.sistemas.pedidos.infrastructure.output.jpa.mapper.PedidoEstadoLogEntityMapper;
import com.sistemas.pedidos.infrastructure.output.jpa.repository.IPedidoEstadoLogRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PedidoEstadoLogJpaAdapter implements IPedidoEstadoLogPersistencePort {

    private final IPedidoEstadoLogRepository pedidoEstadoLogRepository;
    private final PedidoEstadoLogEntityMapper mapper;

    public PedidoEstadoLogJpaAdapter(IPedidoEstadoLogRepository pedidoEstadoLogRepository,
                                     PedidoEstadoLogEntityMapper mapper) {
        this.pedidoEstadoLogRepository = pedidoEstadoLogRepository;
        this.mapper = mapper;
    }

    @Override
    public void registrarCambio(PedidoEstadoLog cambio) {
        pedidoEstadoLogRepository.save(mapper.toEntity(cambio));
    }

    @Override
    public List<PedidoEstadoLog> listarPorPedido(Long idPedido) {
        return pedidoEstadoLogRepository.findByIdPedidoOrderByFechaCambioAsc(idPedido)
                .stream().map(mapper::toModel).toList();
    }
}
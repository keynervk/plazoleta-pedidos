package com.sistemas.pedidos.application.handler;

import com.sistemas.pedidos.application.dto.PedidoEstadoLogResponseDto;
import com.sistemas.pedidos.application.mapper.ResponseMapper;
import com.sistemas.pedidos.domain.api.ITrazabilidadServicePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TrazabilidadHandler {

    private final ITrazabilidadServicePort trazabilidadServicePort;
    private final ResponseMapper responseMapper;

    public TrazabilidadHandler(ITrazabilidadServicePort trazabilidadServicePort, ResponseMapper responseMapper) {
        this.trazabilidadServicePort = trazabilidadServicePort;
        this.responseMapper = responseMapper;
    }

    public List<PedidoEstadoLogResponseDto> consultarTrazabilidad(Long idPedido, Long idCliente) {
        return trazabilidadServicePort.consultarTrazabilidad(idPedido, idCliente)
                .stream().map(responseMapper::toDto).toList();
    }
}
package com.sistemas.pedidos.application.handler;

import com.sistemas.pedidos.application.dto.PedidoRequestDto;
import com.sistemas.pedidos.application.dto.PedidoResponseDto;
import com.sistemas.pedidos.application.mapper.PedidoRequestMapper;
import com.sistemas.pedidos.application.mapper.ResponseMapper;
import com.sistemas.pedidos.domain.api.IPedidoServicePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PedidoHandler {

    private final IPedidoServicePort pedidoServicePort;
    private final PedidoRequestMapper mapper;
    private final ResponseMapper responseMapper;

    public PedidoHandler(IPedidoServicePort pedidoServicePort, PedidoRequestMapper mapper,
                         ResponseMapper responseMapper) {
        this.pedidoServicePort = pedidoServicePort;
        this.mapper = mapper;
        this.responseMapper = responseMapper;
    }

    public PedidoResponseDto crearPedido(PedidoRequestDto dto, Long idCliente) {
        return responseMapper.toDto(pedidoServicePort.crearPedido(mapper.toModel(dto), idCliente));
    }
}
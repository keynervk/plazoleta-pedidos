package com.sistemas.pedidos.application.handler;

import com.sistemas.pedidos.application.dto.EntregarPedidoRequestDto;
import com.sistemas.pedidos.application.dto.PedidoRequestDto;
import com.sistemas.pedidos.application.dto.PedidoResponseDto;
import com.sistemas.pedidos.application.mapper.PedidoRequestMapper;
import com.sistemas.pedidos.application.mapper.ResponseMapper;
import com.sistemas.pedidos.domain.api.IPedidoServicePort;
import com.sistemas.pedidos.domain.model.EstadoPedido;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional(readOnly = true)
    public List<PedidoResponseDto> listarPorEstado(EstadoPedido estado, int pagina, int tamanioPagina,
                                                   Long idEmpleado) {
        return pedidoServicePort.listarPedidosPorEstado(estado, pagina, tamanioPagina, idEmpleado)
                .stream().map(responseMapper::toDto).toList();
    }

    public PedidoResponseDto asignarPedido(Long idPedido, Long idEmpleado) {
        return responseMapper.toDto(pedidoServicePort.asignarPedido(idPedido, idEmpleado));
    }

    public PedidoResponseDto marcarPedidoListo(Long idPedido, Long idEmpleado) {
        return responseMapper.toDto(pedidoServicePort.marcarPedidoListo(idPedido, idEmpleado));
    }

    public PedidoResponseDto entregarPedido(Long idPedido, EntregarPedidoRequestDto dto, Long idEmpleado) {
        return responseMapper.toDto(pedidoServicePort.entregarPedido(idPedido, dto.pin(), idEmpleado));
    }

    public PedidoResponseDto cancelarPedido(Long idPedido, Long idCliente) {
        return responseMapper.toDto(pedidoServicePort.cancelarPedido(idPedido, idCliente));
    }
}
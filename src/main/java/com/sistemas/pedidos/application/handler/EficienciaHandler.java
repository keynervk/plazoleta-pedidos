package com.sistemas.pedidos.application.handler;

import com.sistemas.pedidos.application.dto.EficienciaResponseDto;
import com.sistemas.pedidos.application.mapper.ResponseMapper;
import com.sistemas.pedidos.domain.api.IEficienciaServicePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EficienciaHandler {

    private final IEficienciaServicePort eficienciaServicePort;
    private final ResponseMapper responseMapper;

    public EficienciaHandler(IEficienciaServicePort eficienciaServicePort, ResponseMapper responseMapper) {
        this.eficienciaServicePort = eficienciaServicePort;
        this.responseMapper = responseMapper;
    }

    public EficienciaResponseDto consultarEficiencia(Long idRestaurante, Long idPropietario) {
        return responseMapper.toDto(eficienciaServicePort.consultarEficiencia(idRestaurante, idPropietario));
    }
}
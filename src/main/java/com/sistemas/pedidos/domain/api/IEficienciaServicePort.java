package com.sistemas.pedidos.domain.api;

import com.sistemas.pedidos.domain.model.ReporteEficiencia;

public interface IEficienciaServicePort {

    ReporteEficiencia consultarEficiencia(Long idRestaurante, Long idPropietario);
}
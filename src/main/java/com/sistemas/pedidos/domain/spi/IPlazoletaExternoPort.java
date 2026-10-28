package com.sistemas.pedidos.domain.spi;

import com.sistemas.pedidos.domain.model.Plato;
import com.sistemas.pedidos.domain.model.Restaurante;

import java.util.List;

public interface IPlazoletaExternoPort {

    /**
     * Devuelve el restaurante, o null si no existe en Plazoleta.
     */
    Restaurante buscarRestaurante(Long idRestaurante);

    List<Plato> listarPlatosDeRestaurante(Long idRestaurante);
}
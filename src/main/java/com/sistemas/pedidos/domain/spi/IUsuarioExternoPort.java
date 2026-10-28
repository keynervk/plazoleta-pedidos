package com.sistemas.pedidos.domain.spi;

public interface IUsuarioExternoPort {

    /**
     * Devuelve el id del restaurante al que pertenece el empleado, o null si no tiene uno asignado.
     */
    Long obtenerIdRestauranteDeEmpleado(Long idEmpleado);
}
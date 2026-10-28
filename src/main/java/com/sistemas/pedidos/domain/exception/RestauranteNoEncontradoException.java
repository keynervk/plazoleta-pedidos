package com.sistemas.pedidos.domain.exception;

public class RestauranteNoEncontradoException extends RuntimeException {
    public RestauranteNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
package com.sistemas.pedidos.domain.exception;

public class PinInvalidoException extends RuntimeException {
    public PinInvalidoException(String mensaje) {
        super(mensaje);
    }
}
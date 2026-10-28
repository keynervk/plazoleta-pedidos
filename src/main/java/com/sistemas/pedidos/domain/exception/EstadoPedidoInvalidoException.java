package com.sistemas.pedidos.domain.exception;

public class EstadoPedidoInvalidoException extends RuntimeException {
    public EstadoPedidoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
package com.sistemas.pedidos.domain.exception;

public class PedidoNoEncontradoException extends RuntimeException {
    public PedidoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
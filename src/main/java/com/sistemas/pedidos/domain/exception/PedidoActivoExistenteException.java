package com.sistemas.pedidos.domain.exception;

public class PedidoActivoExistenteException extends RuntimeException {
    public PedidoActivoExistenteException(String mensaje) {
        super(mensaje);
    }
}
package com.sistemas.pedidos.domain.spi;

public interface INotificacionPort {

    void enviarSms(String celular, String mensaje);
}
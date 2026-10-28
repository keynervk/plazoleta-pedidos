package com.sistemas.pedidos.infrastructure.output.notificacion;

import com.sistemas.pedidos.domain.spi.INotificacionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Simula el envio de SMS escribiendo el mensaje en la consola.
 */
@Component
public class SmsSimuladoAdapter implements INotificacionPort {

    private static final Logger log = LoggerFactory.getLogger(SmsSimuladoAdapter.class);

    @Override
    public void enviarSms(String celular, String mensaje) {
        // Integracion real: crear otro adaptador de INotificacionPort y registrarlo en lugar de este,
        // sin tocar el dominio. Por ejemplo:
        //  - Twilio: Message.creator(new PhoneNumber(celular), new PhoneNumber(numeroRemitente), mensaje).create();
        //  - AWS SNS: snsClient.publish(PublishRequest.builder().phoneNumber(celular).message(mensaje).build());
        log.info("[SMS SIMULADO] Para: {} | Mensaje: {}", celular, mensaje);
    }
}
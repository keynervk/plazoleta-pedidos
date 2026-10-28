package com.sistemas.pedidos.infrastructure.configuration;

import com.sistemas.pedidos.domain.api.IPedidoServicePort;
import com.sistemas.pedidos.domain.spi.INotificacionPort;
import com.sistemas.pedidos.domain.spi.IPedidoPersistencePort;
import com.sistemas.pedidos.domain.spi.IPlazoletaExternoPort;
import com.sistemas.pedidos.domain.spi.IUsuarioExternoPort;
import com.sistemas.pedidos.domain.usecase.PedidoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public IPedidoServicePort pedidoServicePort(IPedidoPersistencePort pedidoPersistencePort,
                                                IPlazoletaExternoPort plazoletaExternoPort,
                                                IUsuarioExternoPort usuarioExternoPort,
                                                INotificacionPort notificacionPort) {
        return new PedidoUseCase(pedidoPersistencePort, plazoletaExternoPort, usuarioExternoPort,
                notificacionPort);
    }
}
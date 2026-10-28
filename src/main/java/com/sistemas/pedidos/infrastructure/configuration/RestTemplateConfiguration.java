package com.sistemas.pedidos.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(propagarToken());
        return restTemplate;
    }

    // Reenvia el JWT de la peticion actual para que Usuarios y Plazoleta apliquen sus propias reglas de seguridad
    private ClientHttpRequestInterceptor propagarToken() {
        return (request, body, execution) -> {
            Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
            if (autenticacion != null && autenticacion.getCredentials() instanceof String token) {
                request.getHeaders().setBearerAuth(token);
            }
            return execution.execute(request, body);
        };
    }
}
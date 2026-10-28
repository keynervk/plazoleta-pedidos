package com.sistemas.pedidos.infrastructure.output.cliente;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sistemas.pedidos.domain.exception.ServicioExternoException;
import com.sistemas.pedidos.domain.spi.IUsuarioExternoPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class UsuarioExternoAdapter implements IUsuarioExternoPort {

    @JsonIgnoreProperties(ignoreUnknown = true)
    record UsuarioRespuesta(Long id, String celular, String rol, Long idRestaurante) {
    }

    private final RestTemplate restTemplate;
    private final String urlBaseUsuarios;

    public UsuarioExternoAdapter(RestTemplate restTemplate,
                                 @Value("${app.usuarios.url}") String urlBaseUsuarios) {
        this.restTemplate = restTemplate;
        this.urlBaseUsuarios = urlBaseUsuarios;
    }

    @Override
    public Long obtenerIdRestauranteDeEmpleado(Long idEmpleado) {
        UsuarioRespuesta usuario = buscarUsuario(idEmpleado);
        return usuario != null ? usuario.idRestaurante() : null;
    }

    @Override
    public String obtenerCelular(Long idUsuario) {
        UsuarioRespuesta usuario = buscarUsuario(idUsuario);
        return usuario != null ? usuario.celular() : null;
    }

    // GET /usuarios/{id} exige JWT: RestTemplateConfiguration reenvia el token de la peticion actual
    private UsuarioRespuesta buscarUsuario(Long idUsuario) {
        try {
            return restTemplate.getForObject(urlBaseUsuarios + "/usuarios/" + idUsuario, UsuarioRespuesta.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (RestClientException e) {
            throw new ServicioExternoException("No fue posible comunicarse con el microservicio Usuarios");
        }
    }
}
package com.sistemas.pedidos.infrastructure.output.cliente;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sistemas.pedidos.domain.exception.ServicioExternoException;
import com.sistemas.pedidos.domain.model.Plato;
import com.sistemas.pedidos.domain.model.Restaurante;
import com.sistemas.pedidos.domain.spi.IPlazoletaExternoPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Component
public class PlazoletaExternoAdapter implements IPlazoletaExternoPort {

    private static final String MENSAJE_NO_DISPONIBLE = "No fue posible comunicarse con el microservicio Plazoleta";

    @JsonIgnoreProperties(ignoreUnknown = true)
    record RestauranteRespuesta(Long id, String nombre, Long idPropietario) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record PlatoRespuesta(Long id, String nombre, Integer precio, boolean activo, Long idRestaurante) {
    }

    private final RestTemplate restTemplate;
    private final String urlBasePlazoleta;

    public PlazoletaExternoAdapter(RestTemplate restTemplate,
                                   @Value("${app.plazoleta.url}") String urlBasePlazoleta) {
        this.restTemplate = restTemplate;
        this.urlBasePlazoleta = urlBasePlazoleta;
    }

    @Override
    public Restaurante buscarRestaurante(Long idRestaurante) {
        try {
            String url = urlBasePlazoleta + "/restaurantes/" + idRestaurante;
            RestauranteRespuesta r = restTemplate.getForObject(url, RestauranteRespuesta.class);
            return r == null ? null : new Restaurante(r.id(), r.nombre(), r.idPropietario());
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (RestClientException e) {
            throw new ServicioExternoException(MENSAJE_NO_DISPONIBLE);
        }
    }

    @Override
    public List<Plato> listarPlatosDeRestaurante(Long idRestaurante) {
        try {
            String url = UriComponentsBuilder.fromUriString(urlBasePlazoleta + "/platos")
                    .queryParam("idRestaurante", idRestaurante)
                    .toUriString();
            PlatoRespuesta[] platos = restTemplate.getForObject(url, PlatoRespuesta[].class);
            if (platos == null) {
                return List.of();
            }
            return Arrays.stream(platos)
                    .map(p -> new Plato(p.id(), p.nombre(), p.precio(), p.activo(), p.idRestaurante()))
                    .toList();
        } catch (RestClientException e) {
            throw new ServicioExternoException(MENSAJE_NO_DISPONIBLE);
        }
    }
}
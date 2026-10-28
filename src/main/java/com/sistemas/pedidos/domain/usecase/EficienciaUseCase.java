package com.sistemas.pedidos.domain.usecase;

import com.sistemas.pedidos.domain.api.IEficienciaServicePort;
import com.sistemas.pedidos.domain.exception.NoAutorizadoException;
import com.sistemas.pedidos.domain.exception.RestauranteNoEncontradoException;
import com.sistemas.pedidos.domain.exception.ValidacionException;
import com.sistemas.pedidos.domain.model.EstadoPedido;
import com.sistemas.pedidos.domain.model.Pedido;
import com.sistemas.pedidos.domain.model.PedidoEstadoLog;
import com.sistemas.pedidos.domain.model.RankingEmpleado;
import com.sistemas.pedidos.domain.model.ReporteEficiencia;
import com.sistemas.pedidos.domain.model.Restaurante;
import com.sistemas.pedidos.domain.model.TiempoPedido;
import com.sistemas.pedidos.domain.spi.IPedidoEstadoLogPersistencePort;
import com.sistemas.pedidos.domain.spi.IPedidoPersistencePort;
import com.sistemas.pedidos.domain.spi.IPlazoletaExternoPort;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EficienciaUseCase implements IEficienciaServicePort {

    private final IPedidoPersistencePort pedidoPersistencePort;
    private final IPedidoEstadoLogPersistencePort pedidoEstadoLogPersistencePort;
    private final IPlazoletaExternoPort plazoletaExternoPort;

    public EficienciaUseCase(IPedidoPersistencePort pedidoPersistencePort,
                             IPedidoEstadoLogPersistencePort pedidoEstadoLogPersistencePort,
                             IPlazoletaExternoPort plazoletaExternoPort) {
        this.pedidoPersistencePort = pedidoPersistencePort;
        this.pedidoEstadoLogPersistencePort = pedidoEstadoLogPersistencePort;
        this.plazoletaExternoPort = plazoletaExternoPort;
    }

    @Override
    public ReporteEficiencia consultarEficiencia(Long idRestaurante, Long idPropietario) {
        if (idRestaurante == null) {
            throw new ValidacionException("El id del restaurante es obligatorio");
        }

        Restaurante restaurante = plazoletaExternoPort.buscarRestaurante(idRestaurante);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe un restaurante con ese id");
        }
        if (idPropietario == null || !idPropietario.equals(restaurante.getIdPropietario())) {
            throw new NoAutorizadoException("Este restaurante no te pertenece, no puedes consultar su eficiencia");
        }

        List<Pedido> entregados =
                pedidoPersistencePort.listarTodosPorRestauranteYEstado(idRestaurante, EstadoPedido.ENTREGADO);
        if (entregados.isEmpty()) {
            return new ReporteEficiencia(idRestaurante, List.of(), List.of());
        }

        List<Long> idsPedidos = entregados.stream().map(Pedido::getId).toList();
        Map<Long, List<PedidoEstadoLog>> cambiosPorPedido = pedidoEstadoLogPersistencePort.listarPorPedidos(idsPedidos)
                .stream().collect(Collectors.groupingBy(PedidoEstadoLog::getIdPedido));

        List<TiempoPedido> tiempos = new ArrayList<>();
        for (Pedido pedido : entregados) {
            List<PedidoEstadoLog> cambios = cambiosPorPedido.getOrDefault(pedido.getId(), List.of());
            Optional<LocalDateTime> inicio = fechaDelCambioA(cambios, EstadoPedido.PENDIENTE);
            Optional<LocalDateTime> fin = fechaDelCambioA(cambios, EstadoPedido.ENTREGADO);
            // Un pedido sin trazabilidad completa no se puede medir, asi que no entra en el reporte
            if (inicio.isPresent() && fin.isPresent()) {
                tiempos.add(new TiempoPedido(pedido.getId(), pedido.getIdEmpleadoAsignado(), inicio.get(), fin.get(),
                        Duration.between(inicio.get(), fin.get())));
            }
        }
        tiempos.sort(Comparator.comparing(TiempoPedido::duracion).thenComparing(TiempoPedido::idPedido));

        List<RankingEmpleado> ranking = tiempos.stream()
                .filter(tiempo -> tiempo.idEmpleado() != null)
                .collect(Collectors.groupingBy(TiempoPedido::idEmpleado))
                .entrySet().stream()
                .map(entrada -> new RankingEmpleado(entrada.getKey(), entrada.getValue().size(),
                        promedio(entrada.getValue())))
                .sorted(Comparator.comparing(RankingEmpleado::tiempoPromedio)
                        .thenComparing(RankingEmpleado::idEmpleado))
                .toList();

        return new ReporteEficiencia(idRestaurante, tiempos, ranking);
    }

    private Optional<LocalDateTime> fechaDelCambioA(List<PedidoEstadoLog> cambios, EstadoPedido estado) {
        return cambios.stream()
                .filter(cambio -> cambio.getEstadoNuevo() == estado)
                .map(PedidoEstadoLog::getFechaCambio)
                .min(Comparator.naturalOrder());
    }

    private Duration promedio(List<TiempoPedido> tiempos) {
        Duration total = tiempos.stream().map(TiempoPedido::duracion).reduce(Duration.ZERO, Duration::plus);
        return total.dividedBy(tiempos.size());
    }
}
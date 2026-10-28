package com.sistemas.pedidos.application.mapper;

import com.sistemas.pedidos.application.dto.EficienciaResponseDto;
import com.sistemas.pedidos.application.dto.PedidoEstadoLogResponseDto;
import com.sistemas.pedidos.application.dto.PedidoPlatoResponseDto;
import com.sistemas.pedidos.application.dto.PedidoResponseDto;
import com.sistemas.pedidos.application.dto.RankingEmpleadoResponseDto;
import com.sistemas.pedidos.application.dto.TiempoPedidoResponseDto;
import com.sistemas.pedidos.domain.model.Pedido;
import com.sistemas.pedidos.domain.model.PedidoEstadoLog;
import com.sistemas.pedidos.domain.model.ReporteEficiencia;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ResponseMapper {

    public PedidoResponseDto toDto(Pedido p) {
        var platos = p.getPlatos().stream()
                .map(item -> new PedidoPlatoResponseDto(item.getIdPlato(), item.getCantidad(), item.getPrecioUnitario()))
                .toList();
        return new PedidoResponseDto(p.getId(), p.getIdCliente(), p.getIdRestaurante(), p.getEstado(),
                p.getIdEmpleadoAsignado(), p.getFechaCreacion(), platos);
    }

    public PedidoEstadoLogResponseDto toDto(PedidoEstadoLog log) {
        return new PedidoEstadoLogResponseDto(log.getIdPedido(), log.getEstadoAnterior(), log.getEstadoNuevo(),
                log.getFechaCambio());
    }

    public EficienciaResponseDto toDto(ReporteEficiencia reporte) {
        var pedidos = reporte.pedidos().stream()
                .map(t -> new TiempoPedidoResponseDto(t.idPedido(), t.idEmpleado(), t.inicio(), t.fin(),
                        enMinutos(t.duracion())))
                .toList();
        var empleados = reporte.empleados().stream()
                .map(r -> new RankingEmpleadoResponseDto(r.idEmpleado(), r.pedidosAtendidos(),
                        enMinutos(r.tiempoPromedio())))
                .toList();
        return new EficienciaResponseDto(reporte.idRestaurante(), pedidos, empleados);
    }

    private double enMinutos(Duration duracion) {
        return Math.round(duracion.toMillis() / 600.0) / 100.0;
    }
}
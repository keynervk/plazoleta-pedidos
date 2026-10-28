package com.sistemas.pedidos.domain.usecase;

import com.sistemas.pedidos.domain.exception.NoAutorizadoException;
import com.sistemas.pedidos.domain.exception.RestauranteNoEncontradoException;
import com.sistemas.pedidos.domain.exception.ValidacionException;
import com.sistemas.pedidos.domain.model.EstadoPedido;
import com.sistemas.pedidos.domain.model.Pedido;
import com.sistemas.pedidos.domain.model.PedidoEstadoLog;
import com.sistemas.pedidos.domain.model.RankingEmpleado;
import com.sistemas.pedidos.domain.model.ReporteEficiencia;
import com.sistemas.pedidos.domain.model.Restaurante;
import com.sistemas.pedidos.domain.spi.IPedidoEstadoLogPersistencePort;
import com.sistemas.pedidos.domain.spi.IPedidoPersistencePort;
import com.sistemas.pedidos.domain.spi.IPlazoletaExternoPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EficienciaUseCaseTest {

    private static final Long ID_RESTAURANTE = 1L;
    private static final Long ID_PROPIETARIO = 5L;
    private static final Long EMPLEADO_A = 30L;
    private static final Long EMPLEADO_B = 31L;
    private static final LocalDateTime BASE = LocalDateTime.of(2026, 10, 1, 12, 0);

    @Mock
    private IPedidoPersistencePort pedidoPersistencePort;

    @Mock
    private IPedidoEstadoLogPersistencePort pedidoEstadoLogPersistencePort;

    @Mock
    private IPlazoletaExternoPort plazoletaExternoPort;

    @InjectMocks
    private EficienciaUseCase eficienciaUseCase;

    private Pedido entregado(Long id, Long idEmpleado) {
        return new Pedido(id, 20L, ID_RESTAURANTE, EstadoPedido.ENTREGADO, idEmpleado, "123456", BASE,
                new ArrayList<>());
    }

    private List<PedidoEstadoLog> ciclo(Long idPedido, int minutoInicio, int minutosHastaEntrega) {
        LocalDateTime inicio = BASE.plusMinutes(minutoInicio);
        return List.of(
                new PedidoEstadoLog(null, idPedido, null, EstadoPedido.PENDIENTE, inicio),
                new PedidoEstadoLog(null, idPedido, EstadoPedido.PENDIENTE, EstadoPedido.EN_PREPARACION,
                        inicio.plusMinutes(2)),
                new PedidoEstadoLog(null, idPedido, EstadoPedido.EN_PREPARACION, EstadoPedido.LISTO,
                        inicio.plusMinutes(minutosHastaEntrega - 1)),
                new PedidoEstadoLog(null, idPedido, EstadoPedido.LISTO, EstadoPedido.ENTREGADO,
                        inicio.plusMinutes(minutosHastaEntrega)));
    }

    private void elRestauranteEsDelPropietario() {
        when(plazoletaExternoPort.buscarRestaurante(ID_RESTAURANTE))
                .thenReturn(new Restaurante(ID_RESTAURANTE, "Restaurante de Juan", ID_PROPIETARIO));
    }

    @Test
    void consultarEficiencia_calculaElTiempoDeCadaPedidoYElPromedioPorEmpleado() {
        elRestauranteEsDelPropietario();
        when(pedidoPersistencePort.listarTodosPorRestauranteYEstado(ID_RESTAURANTE, EstadoPedido.ENTREGADO))
                .thenReturn(List.of(entregado(1L, EMPLEADO_A), entregado(2L, EMPLEADO_A), entregado(3L, EMPLEADO_B)));
        List<PedidoEstadoLog> logs = new ArrayList<>();
        logs.addAll(ciclo(1L, 0, 30));
        logs.addAll(ciclo(2L, 10, 20));
        logs.addAll(ciclo(3L, 5, 15));
        when(pedidoEstadoLogPersistencePort.listarPorPedidos(List.of(1L, 2L, 3L))).thenReturn(logs);

        ReporteEficiencia reporte = eficienciaUseCase.consultarEficiencia(ID_RESTAURANTE, ID_PROPIETARIO);

        assertEquals(3, reporte.pedidos().size());
        assertEquals(3L, reporte.pedidos().get(0).idPedido());
        assertEquals(Duration.ofMinutes(15), reporte.pedidos().get(0).duracion());
        assertEquals(Duration.ofMinutes(20), reporte.pedidos().get(1).duracion());
        assertEquals(Duration.ofMinutes(30), reporte.pedidos().get(2).duracion());
        assertEquals(BASE, reporte.pedidos().get(2).inicio());
        assertEquals(BASE.plusMinutes(30), reporte.pedidos().get(2).fin());

        List<RankingEmpleado> ranking = reporte.empleados();
        assertEquals(2, ranking.size());
        assertEquals(EMPLEADO_B, ranking.get(0).idEmpleado());
        assertEquals(Duration.ofMinutes(15), ranking.get(0).tiempoPromedio());
        assertEquals(EMPLEADO_A, ranking.get(1).idEmpleado());
        assertEquals(2, ranking.get(1).pedidosAtendidos());
        assertEquals(Duration.ofMinutes(25), ranking.get(1).tiempoPromedio());
    }

    @Test
    void consultarEficiencia_ignoraLosPedidosSinTrazabilidadCompleta() {
        elRestauranteEsDelPropietario();
        when(pedidoPersistencePort.listarTodosPorRestauranteYEstado(ID_RESTAURANTE, EstadoPedido.ENTREGADO))
                .thenReturn(List.of(entregado(1L, EMPLEADO_A), entregado(2L, EMPLEADO_A)));
        List<PedidoEstadoLog> logs = new ArrayList<>(ciclo(1L, 0, 30));
        logs.add(new PedidoEstadoLog(null, 2L, EstadoPedido.LISTO, EstadoPedido.ENTREGADO, BASE.plusMinutes(40)));
        when(pedidoEstadoLogPersistencePort.listarPorPedidos(anyList())).thenReturn(logs);

        ReporteEficiencia reporte = eficienciaUseCase.consultarEficiencia(ID_RESTAURANTE, ID_PROPIETARIO);

        assertEquals(1, reporte.pedidos().size());
        assertEquals(1L, reporte.pedidos().get(0).idPedido());
        assertEquals(1, reporte.empleados().get(0).pedidosAtendidos());
    }

    @Test
    void consultarEficiencia_sinPedidosEntregados_devuelveUnReporteVacio() {
        elRestauranteEsDelPropietario();
        when(pedidoPersistencePort.listarTodosPorRestauranteYEstado(ID_RESTAURANTE, EstadoPedido.ENTREGADO))
                .thenReturn(List.of());

        ReporteEficiencia reporte = eficienciaUseCase.consultarEficiencia(ID_RESTAURANTE, ID_PROPIETARIO);

        assertTrue(reporte.pedidos().isEmpty());
        assertTrue(reporte.empleados().isEmpty());
        verify(pedidoEstadoLogPersistencePort, never()).listarPorPedidos(anyList());
    }

    @Test
    void consultarEficiencia_deUnRestauranteAjeno_lanzaNoAutorizadoException() {
        elRestauranteEsDelPropietario();

        assertThrows(NoAutorizadoException.class, () -> eficienciaUseCase.consultarEficiencia(ID_RESTAURANTE, 99L));
        verify(pedidoPersistencePort, never()).listarTodosPorRestauranteYEstado(any(), any());
    }

    @Test
    void consultarEficiencia_siElRestauranteNoExiste_lanzaRestauranteNoEncontradoException() {
        when(plazoletaExternoPort.buscarRestaurante(ID_RESTAURANTE)).thenReturn(null);

        assertThrows(RestauranteNoEncontradoException.class,
                () -> eficienciaUseCase.consultarEficiencia(ID_RESTAURANTE, ID_PROPIETARIO));
    }

    @Test
    void consultarEficiencia_sinIdRestaurante_lanzaValidacionException() {
        assertThrows(ValidacionException.class, () -> eficienciaUseCase.consultarEficiencia(null, ID_PROPIETARIO));
    }
}
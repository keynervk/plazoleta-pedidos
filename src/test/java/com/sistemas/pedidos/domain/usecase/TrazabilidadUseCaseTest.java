package com.sistemas.pedidos.domain.usecase;

import com.sistemas.pedidos.domain.exception.NoAutorizadoException;
import com.sistemas.pedidos.domain.exception.PedidoNoEncontradoException;
import com.sistemas.pedidos.domain.exception.ValidacionException;
import com.sistemas.pedidos.domain.model.EstadoPedido;
import com.sistemas.pedidos.domain.model.Pedido;
import com.sistemas.pedidos.domain.model.PedidoEstadoLog;
import com.sistemas.pedidos.domain.spi.IPedidoEstadoLogPersistencePort;
import com.sistemas.pedidos.domain.spi.IPedidoPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrazabilidadUseCaseTest {

    private static final Long ID_PEDIDO = 100L;
    private static final Long ID_CLIENTE = 20L;
    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 10, 1, 12, 0);

    @Mock
    private IPedidoPersistencePort pedidoPersistencePort;

    @Mock
    private IPedidoEstadoLogPersistencePort pedidoEstadoLogPersistencePort;

    @InjectMocks
    private TrazabilidadUseCase trazabilidadUseCase;

    private Pedido pedidoDelCliente() {
        return new Pedido(ID_PEDIDO, ID_CLIENTE, 1L, EstadoPedido.EN_PREPARACION, 30L, null, INICIO, new ArrayList<>());
    }

    @Test
    void consultarTrazabilidad_dePedidoPropio_devuelveLosCambiosEnOrdenCronologico() {
        PedidoEstadoLog creado = new PedidoEstadoLog(1L, ID_PEDIDO, null, EstadoPedido.PENDIENTE, INICIO);
        PedidoEstadoLog asignado = new PedidoEstadoLog(2L, ID_PEDIDO, EstadoPedido.PENDIENTE,
                EstadoPedido.EN_PREPARACION, INICIO.plusMinutes(5));
        when(pedidoPersistencePort.buscarPorId(ID_PEDIDO)).thenReturn(pedidoDelCliente());
        when(pedidoEstadoLogPersistencePort.listarPorPedido(ID_PEDIDO)).thenReturn(List.of(asignado, creado));

        List<PedidoEstadoLog> resultado = trazabilidadUseCase.consultarTrazabilidad(ID_PEDIDO, ID_CLIENTE);

        assertEquals(List.of(creado, asignado), resultado);
    }

    @Test
    void consultarTrazabilidad_dePedidoDeOtroCliente_lanzaNoAutorizadoException() {
        when(pedidoPersistencePort.buscarPorId(ID_PEDIDO)).thenReturn(pedidoDelCliente());

        assertThrows(NoAutorizadoException.class, () -> trazabilidadUseCase.consultarTrazabilidad(ID_PEDIDO, 999L));
        verify(pedidoEstadoLogPersistencePort, never()).listarPorPedido(anyLong());
    }

    @Test
    void consultarTrazabilidad_siElPedidoNoExiste_lanzaPedidoNoEncontradoException() {
        when(pedidoPersistencePort.buscarPorId(ID_PEDIDO)).thenReturn(null);

        assertThrows(PedidoNoEncontradoException.class,
                () -> trazabilidadUseCase.consultarTrazabilidad(ID_PEDIDO, ID_CLIENTE));
    }

    @Test
    void consultarTrazabilidad_sinIdPedido_lanzaValidacionException() {
        assertThrows(ValidacionException.class, () -> trazabilidadUseCase.consultarTrazabilidad(null, ID_CLIENTE));
    }
}
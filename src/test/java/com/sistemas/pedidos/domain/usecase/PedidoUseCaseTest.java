package com.sistemas.pedidos.domain.usecase;

import com.sistemas.pedidos.domain.exception.NoAutorizadoException;
import com.sistemas.pedidos.domain.exception.PedidoActivoExistenteException;
import com.sistemas.pedidos.domain.exception.ValidacionException;
import com.sistemas.pedidos.domain.model.EstadoPedido;
import com.sistemas.pedidos.domain.model.Pedido;
import com.sistemas.pedidos.domain.model.PedidoPlato;
import com.sistemas.pedidos.domain.model.Plato;
import com.sistemas.pedidos.domain.model.Restaurante;
import com.sistemas.pedidos.domain.spi.IPedidoPersistencePort;
import com.sistemas.pedidos.domain.spi.IPlazoletaExternoPort;
import com.sistemas.pedidos.domain.spi.IUsuarioExternoPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoUseCaseTest {

    private static final Long ID_CLIENTE = 20L;
    private static final Long ID_RESTAURANTE = 1L;
    private static final Long ID_EMPLEADO = 30L;

    @Mock
    private IPedidoPersistencePort pedidoPersistencePort;

    @Mock
    private IPlazoletaExternoPort plazoletaExternoPort;

    @Mock
    private IUsuarioExternoPort usuarioExternoPort;

    @InjectMocks
    private PedidoUseCase pedidoUseCase;

    private Pedido pedidoNuevo(PedidoPlato... platos) {
        Pedido pedido = new Pedido();
        pedido.setIdRestaurante(ID_RESTAURANTE);
        pedido.setPlatos(new ArrayList<>(List.of(platos)));
        return pedido;
    }

    private Pedido pedidoNuevoValido() {
        return pedidoNuevo(new PedidoPlato(10L, 2, null), new PedidoPlato(11L, 1, null));
    }

    private Restaurante restaurante() {
        return new Restaurante(ID_RESTAURANTE, "Restaurante de Juan", 5L);
    }

    private List<Plato> menuDelRestaurante() {
        return List.of(
                new Plato(10L, "Hamburguesa", 15000, true, ID_RESTAURANTE),
                new Plato(11L, "Papas", 6000, true, ID_RESTAURANTE),
                new Plato(12L, "Malteada", 9000, false, ID_RESTAURANTE));
    }

    private void elClienteNoTienePedidosActivos() {
        when(pedidoPersistencePort.existePedidoDeClienteEnEstados(eq(ID_CLIENTE), anyList())).thenReturn(false);
    }

    // ---------- HU 11: crearPedido ----------

    @Test
    void crearPedido_conDatosValidos_quedaPendienteConPreciosDelMenuYSeGuarda() {
        elClienteNoTienePedidosActivos();
        when(plazoletaExternoPort.buscarRestaurante(ID_RESTAURANTE)).thenReturn(restaurante());
        when(plazoletaExternoPort.listarPlatosDeRestaurante(ID_RESTAURANTE)).thenReturn(menuDelRestaurante());
        when(pedidoPersistencePort.guardarPedido(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        pedidoUseCase.crearPedido(pedidoNuevoValido(), ID_CLIENTE);

        ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoPersistencePort).guardarPedido(captor.capture());
        Pedido guardado = captor.getValue();
        assertEquals(EstadoPedido.PENDIENTE, guardado.getEstado());
        assertEquals(ID_CLIENTE, guardado.getIdCliente());
        assertEquals(15000, guardado.getPlatos().get(0).getPrecioUnitario());
        assertEquals(6000, guardado.getPlatos().get(1).getPrecioUnitario());
        assertNotNull(guardado.getFechaCreacion());
        assertNull(guardado.getIdEmpleadoAsignado());
        assertNull(guardado.getPin());
    }

    @Test
    void crearPedido_siElClienteYaTieneUnPedidoActivo_lanzaPedidoActivoExistenteException() {
        when(pedidoPersistencePort.existePedidoDeClienteEnEstados(ID_CLIENTE,
                List.of(EstadoPedido.PENDIENTE, EstadoPedido.EN_PREPARACION, EstadoPedido.LISTO))).thenReturn(true);

        assertThrows(PedidoActivoExistenteException.class,
                () -> pedidoUseCase.crearPedido(pedidoNuevoValido(), ID_CLIENTE));
        verify(pedidoPersistencePort, never()).guardarPedido(any());
    }

    @Test
    void crearPedido_siElRestauranteNoExiste_lanzaValidacionException() {
        elClienteNoTienePedidosActivos();
        when(plazoletaExternoPort.buscarRestaurante(ID_RESTAURANTE)).thenReturn(null);

        assertThrows(ValidacionException.class, () -> pedidoUseCase.crearPedido(pedidoNuevoValido(), ID_CLIENTE));
        verify(pedidoPersistencePort, never()).guardarPedido(any());
    }

    @Test
    void crearPedido_conPlatoQueNoEstaEnElMenuDelRestaurante_lanzaValidacionException() {
        elClienteNoTienePedidosActivos();
        when(plazoletaExternoPort.buscarRestaurante(ID_RESTAURANTE)).thenReturn(restaurante());
        when(plazoletaExternoPort.listarPlatosDeRestaurante(ID_RESTAURANTE)).thenReturn(menuDelRestaurante());

        Pedido pedido = pedidoNuevo(new PedidoPlato(10L, 1, null), new PedidoPlato(99L, 1, null));

        assertThrows(ValidacionException.class, () -> pedidoUseCase.crearPedido(pedido, ID_CLIENTE));
        verify(pedidoPersistencePort, never()).guardarPedido(any());
    }

    @Test
    void crearPedido_conPlatoDeOtroRestaurante_lanzaValidacionException() {
        elClienteNoTienePedidosActivos();
        when(plazoletaExternoPort.buscarRestaurante(ID_RESTAURANTE)).thenReturn(restaurante());
        when(plazoletaExternoPort.listarPlatosDeRestaurante(ID_RESTAURANTE))
                .thenReturn(List.of(new Plato(10L, "Hamburguesa", 15000, true, 2L)));

        assertThrows(ValidacionException.class,
                () -> pedidoUseCase.crearPedido(pedidoNuevo(new PedidoPlato(10L, 1, null)), ID_CLIENTE));
        verify(pedidoPersistencePort, never()).guardarPedido(any());
    }

    @Test
    void crearPedido_conPlatoInactivo_lanzaValidacionException() {
        elClienteNoTienePedidosActivos();
        when(plazoletaExternoPort.buscarRestaurante(ID_RESTAURANTE)).thenReturn(restaurante());
        when(plazoletaExternoPort.listarPlatosDeRestaurante(ID_RESTAURANTE)).thenReturn(menuDelRestaurante());

        assertThrows(ValidacionException.class,
                () -> pedidoUseCase.crearPedido(pedidoNuevo(new PedidoPlato(12L, 1, null)), ID_CLIENTE));
        verify(pedidoPersistencePort, never()).guardarPedido(any());
    }

    @Test
    void crearPedido_sinPlatos_lanzaValidacionException() {
        assertThrows(ValidacionException.class, () -> pedidoUseCase.crearPedido(pedidoNuevo(), ID_CLIENTE));
        verify(pedidoPersistencePort, never()).guardarPedido(any());
    }

    @Test
    void crearPedido_conListaDePlatosNula_lanzaValidacionException() {
        Pedido pedido = pedidoNuevo();
        pedido.setPlatos(null);

        assertThrows(ValidacionException.class, () -> pedidoUseCase.crearPedido(pedido, ID_CLIENTE));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void crearPedido_conCantidadNoPositiva_lanzaValidacionException(int cantidad) {
        Pedido pedido = pedidoNuevo(new PedidoPlato(10L, cantidad, null));

        assertThrows(ValidacionException.class, () -> pedidoUseCase.crearPedido(pedido, ID_CLIENTE));
        verify(pedidoPersistencePort, never()).guardarPedido(any());
    }

    @Test
    void crearPedido_sinCantidad_lanzaValidacionException() {
        Pedido pedido = pedidoNuevo(new PedidoPlato(10L, null, null));

        assertThrows(ValidacionException.class, () -> pedidoUseCase.crearPedido(pedido, ID_CLIENTE));
    }

    @Test
    void crearPedido_sinIdPlato_lanzaValidacionException() {
        Pedido pedido = pedidoNuevo(new PedidoPlato(null, 1, null));

        assertThrows(ValidacionException.class, () -> pedidoUseCase.crearPedido(pedido, ID_CLIENTE));
    }

    @Test
    void crearPedido_conPlatoRepetido_lanzaValidacionException() {
        Pedido pedido = pedidoNuevo(new PedidoPlato(10L, 1, null), new PedidoPlato(10L, 2, null));

        assertThrows(ValidacionException.class, () -> pedidoUseCase.crearPedido(pedido, ID_CLIENTE));
        verify(pedidoPersistencePort, never()).guardarPedido(any());
    }

    @Test
    void crearPedido_sinIdRestaurante_lanzaValidacionException() {
        Pedido pedido = pedidoNuevoValido();
        pedido.setIdRestaurante(null);

        assertThrows(ValidacionException.class, () -> pedidoUseCase.crearPedido(pedido, ID_CLIENTE));
    }

    @Test
    void crearPedido_conPedidoNulo_lanzaValidacionException() {
        assertThrows(ValidacionException.class, () -> pedidoUseCase.crearPedido(null, ID_CLIENTE));
    }

    // ---------- HU 12: listarPedidosPorEstado ----------

    @Test
    void listarPedidosPorEstado_conEmpleadoDeUnRestaurante_listaSoloLosDeSuRestaurante() {
        List<Pedido> esperado = List.of(new Pedido());
        when(usuarioExternoPort.obtenerIdRestauranteDeEmpleado(ID_EMPLEADO)).thenReturn(ID_RESTAURANTE);
        when(pedidoPersistencePort.listarPorRestauranteYEstado(ID_RESTAURANTE, EstadoPedido.PENDIENTE, 0, 10))
                .thenReturn(esperado);

        List<Pedido> resultado = pedidoUseCase.listarPedidosPorEstado(EstadoPedido.PENDIENTE, 0, 10, ID_EMPLEADO);

        assertEquals(esperado, resultado);
    }

    @Test
    void listarPedidosPorEstado_siElEmpleadoNoTieneRestaurante_lanzaNoAutorizadoException() {
        when(usuarioExternoPort.obtenerIdRestauranteDeEmpleado(ID_EMPLEADO)).thenReturn(null);

        assertThrows(NoAutorizadoException.class,
                () -> pedidoUseCase.listarPedidosPorEstado(EstadoPedido.PENDIENTE, 0, 10, ID_EMPLEADO));
        verify(pedidoPersistencePort, never()).listarPorRestauranteYEstado(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    void listarPedidosPorEstado_sinEstado_lanzaValidacionException() {
        assertThrows(ValidacionException.class,
                () -> pedidoUseCase.listarPedidosPorEstado(null, 0, 10, ID_EMPLEADO));
    }

    @Test
    void listarPedidosPorEstado_conPaginaNegativa_lanzaValidacionException() {
        assertThrows(ValidacionException.class,
                () -> pedidoUseCase.listarPedidosPorEstado(EstadoPedido.LISTO, -1, 10, ID_EMPLEADO));
    }

    @Test
    void listarPedidosPorEstado_conTamanioCero_lanzaValidacionException() {
        assertThrows(ValidacionException.class,
                () -> pedidoUseCase.listarPedidosPorEstado(EstadoPedido.LISTO, 0, 0, ID_EMPLEADO));
    }
}

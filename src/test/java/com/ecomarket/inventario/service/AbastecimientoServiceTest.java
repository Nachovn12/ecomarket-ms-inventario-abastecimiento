package com.ecomarket.inventario.service;

import com.ecomarket.inventario.dto.PedidoReabastecimientoRequestDTO;
import com.ecomarket.inventario.dto.PedidoReabastecimientoResponseDTO;
import com.ecomarket.inventario.exception.RecursoNoEncontradoException;
import com.ecomarket.inventario.model.PedidoReabastecimiento;
import com.ecomarket.inventario.model.Producto;
import com.ecomarket.inventario.repository.PedidoReabastecimientoRepository;
import com.ecomarket.inventario.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbastecimientoServiceTest {

    @Mock
    private PedidoReabastecimientoRepository pedidoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private PedidoReabastecimientoService pedidoReabastecimientoService;

    private Producto buildProducto(int stock, int stockMinimo) {
        Producto p = new Producto();
        p.setId(1L);
        p.setNombre("Bolsa Biodegradable");
        p.setSku("ECO-001");
        p.setPrecio(1990.0);
        p.setStock(stock);
        p.setStockMinimo(stockMinimo);
        p.setCategoria("Biodegradables");
        return p;
    }

    private PedidoReabastecimientoRequestDTO buildDTO(int cantidad) {
        PedidoReabastecimientoRequestDTO dto = new PedidoReabastecimientoRequestDTO();
        dto.setProductoId(1L);
        dto.setCantidad(cantidad);
        dto.setCreadoPor("jefeBodega");
        return dto;
    }

    private PedidoReabastecimiento buildPedidoConEstado(PedidoReabastecimiento.Estado estado) {
        Producto producto = buildProducto(10, 5);
        PedidoReabastecimiento pedido = new PedidoReabastecimiento();
        pedido.setId(1L);
        pedido.setProducto(producto);
        pedido.setCantidad(100);
        pedido.setEstado(estado);
        pedido.setCreadoPor("jefeBodega");
        return pedido;
    }

    @Test
    void crearPedido_conStockBajoPuntoDeReorden_generaPedidoPendiente() {
        Producto producto = buildProducto(5, 10);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(pedidoRepository.save(any(PedidoReabastecimiento.class))).thenAnswer(inv -> {
            PedidoReabastecimiento p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        PedidoReabastecimientoResponseDTO result =
                pedidoReabastecimientoService.crearPedido(buildDTO(100));

        assertNotNull(result);
        assertEquals(PedidoReabastecimiento.Estado.PENDIENTE.name(), result.getEstado());
        assertEquals(1L, result.getId());
        verify(pedidoRepository).save(any(PedidoReabastecimiento.class));
    }

    @Test
    void crearPedido_cantidadSugeridaConFormula_seAlmacenaCorrectamente() {
        Producto producto = buildProducto(10, 20);
        int cantidadSugerida = 240;

        PedidoReabastecimientoRequestDTO dto = new PedidoReabastecimientoRequestDTO();
        dto.setProductoId(1L);
        dto.setCantidad(cantidadSugerida);
        dto.setCreadoPor("sistemaAutomatico");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(pedidoRepository.save(any(PedidoReabastecimiento.class))).thenAnswer(inv -> {
            PedidoReabastecimiento p = inv.getArgument(0);
            p.setId(2L);
            return p;
        });

        PedidoReabastecimientoResponseDTO result =
                pedidoReabastecimientoService.crearPedido(dto);

        assertEquals(cantidadSugerida, result.getCantidad());
    }

    @Test
    void crearPedido_estadoInicial_esSiemprePendiente() {
        Producto producto = buildProducto(50, 10);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(pedidoRepository.save(any(PedidoReabastecimiento.class))).thenAnswer(inv -> {
            PedidoReabastecimiento p = inv.getArgument(0);
            p.setId(3L);
            return p;
        });

        PedidoReabastecimientoResponseDTO result =
                pedidoReabastecimientoService.crearPedido(buildDTO(50));

        assertEquals(PedidoReabastecimiento.Estado.PENDIENTE.name(), result.getEstado());
    }

    @Test
    void crearPedido_conProductoInexistente_lanzaRecursoNoEncontradoException() {
        PedidoReabastecimientoRequestDTO dto = buildDTO(100);
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> pedidoReabastecimientoService.crearPedido(dto));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void aprobarPedido_conEstadoPendiente_cambiaAAprobado() {
        PedidoReabastecimiento pedido = buildPedidoConEstado(PedidoReabastecimiento.Estado.PENDIENTE);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(PedidoReabastecimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        PedidoReabastecimientoResponseDTO result = pedidoReabastecimientoService.aprobarPedido(1L);

        assertEquals(PedidoReabastecimiento.Estado.APROBADO.name(), result.getEstado());
        verify(pedidoRepository).save(any(PedidoReabastecimiento.class));
    }

    @Test
    void aprobarPedido_conEstadoNoEsPendiente_lanzaIllegalArgumentException() {
        PedidoReabastecimiento pedido = buildPedidoConEstado(PedidoReabastecimiento.Estado.APROBADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(IllegalArgumentException.class,
                () -> pedidoReabastecimientoService.aprobarPedido(1L));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void aprobarPedido_conIdInexistente_lanzaRecursoNoEncontradoException() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> pedidoReabastecimientoService.aprobarPedido(99L));
    }

    @Test
    void rechazarPedido_conEstadoPendienteYMotivo_cambiaARechazado() {
        PedidoReabastecimiento pedido = buildPedidoConEstado(PedidoReabastecimiento.Estado.PENDIENTE);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(PedidoReabastecimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        PedidoReabastecimientoResponseDTO result =
                pedidoReabastecimientoService.rechazarPedido(1L, "Stock suficiente");

        assertEquals(PedidoReabastecimiento.Estado.RECHAZADO.name(), result.getEstado());
        assertEquals("Stock suficiente", result.getMotivoRechazo());
    }

    @Test
    void rechazarPedido_conEstadoNoEsPendiente_lanzaIllegalArgumentException() {
        PedidoReabastecimiento pedido = buildPedidoConEstado(PedidoReabastecimiento.Estado.RECHAZADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(IllegalArgumentException.class,
                () -> pedidoReabastecimientoService.rechazarPedido(1L, "motivo"));
    }

    @Test
    void rechazarPedido_conMotivoVacio_lanzaIllegalArgumentException() {
        PedidoReabastecimiento pedido = buildPedidoConEstado(PedidoReabastecimiento.Estado.PENDIENTE);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(IllegalArgumentException.class,
                () -> pedidoReabastecimientoService.rechazarPedido(1L, ""));
    }

    @Test
    void obtenerPedido_conIdExistente_retornaDTO() {
        PedidoReabastecimiento pedido = buildPedidoConEstado(PedidoReabastecimiento.Estado.PENDIENTE);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        PedidoReabastecimientoResponseDTO result = pedidoReabastecimientoService.obtenerPedido(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("PENDIENTE", result.getEstado());
    }

    @Test
    void obtenerPedido_conIdInexistente_lanzaRecursoNoEncontradoException() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> pedidoReabastecimientoService.obtenerPedido(99L));
    }

    @Test
    void listarPedidos_retornaListaCompleta() {
        PedidoReabastecimiento p1 = buildPedidoConEstado(PedidoReabastecimiento.Estado.PENDIENTE);
        PedidoReabastecimiento p2 = buildPedidoConEstado(PedidoReabastecimiento.Estado.APROBADO);
        p2.setId(2L);

        when(pedidoRepository.findAll()).thenReturn(List.of(p1, p2));

        List<PedidoReabastecimientoResponseDTO> result = pedidoReabastecimientoService.listarPedidos();

        assertEquals(2, result.size());
        verify(pedidoRepository).findAll();
    }

    @Test
    void rechazarPedido_conMotivoNulo_lanzaIllegalArgumentException() {
        PedidoReabastecimiento pedido = buildPedidoConEstado(PedidoReabastecimiento.Estado.PENDIENTE);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(IllegalArgumentException.class,
                () -> pedidoReabastecimientoService.rechazarPedido(1L, null));
        verify(pedidoRepository, never()).save(any());
    }
}

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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests del flujo de abastecimiento (gestión de pedidos de reabastecimiento).
 * Mapea a PedidoReabastecimientoService (AC-6).
 */
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

    // AC-6: Stock <= punto de reorden → genera orden de compra con estado PENDIENTE
    @Test
    void crearPedido_conStockBajoPuntoDeReorden_generaPedidoPendiente() {
        Producto producto = buildProducto(5, 10); // stock 5 <= stockMinimo 10

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

    // AC-6: Cálculo de cantidad sugerida con fórmula (stockMax - stockActual + demandaPronosticada)
    @Test
    void crearPedido_cantidadSugeridaConFormula_seAlmacenaCorrectamente() {
        Producto producto = buildProducto(10, 20);
        int stockMax = 200;
        int stockActual = 10;
        int demandaPronosticada = 50;
        int cantidadSugerida = stockMax - stockActual + demandaPronosticada; // = 240

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

    // AC-6: Orden generada tiene estado inicial correcto (PENDIENTE)
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
}

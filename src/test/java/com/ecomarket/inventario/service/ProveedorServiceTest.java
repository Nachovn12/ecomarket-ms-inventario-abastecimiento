package com.ecomarket.inventario.service;

import com.ecomarket.inventario.dto.RecepcionMercanciaRequestDTO;
import com.ecomarket.inventario.dto.RecepcionMercanciaResponseDTO;
import com.ecomarket.inventario.exception.RecursoNoEncontradoException;
import com.ecomarket.inventario.model.PedidoReabastecimiento;
import com.ecomarket.inventario.model.Producto;
import com.ecomarket.inventario.model.RecepcionMercancia;
import com.ecomarket.inventario.repository.PedidoReabastecimientoRepository;
import com.ecomarket.inventario.repository.ProductoRepository;
import com.ecomarket.inventario.repository.RecepcionMercanciaRepository;
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
 * Tests del servicio de recepción de mercancía (gestión de abastecimiento desde proveedores).
 * Mapea a RecepcionMercanciaService.
 */
@ExtendWith(MockitoExtension.class)
class ProveedorServiceTest {

    @Mock
    private RecepcionMercanciaRepository recepcionRepository;

    @Mock
    private PedidoReabastecimientoRepository pedidoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private RecepcionMercanciaService recepcionMercanciaService;

    private Producto buildProducto() {
        Producto p = new Producto();
        p.setId(1L);
        p.setNombre("Bolsa Biodegradable");
        p.setSku("ECO-001");
        p.setPrecio(1990.0);
        p.setStock(50);
        p.setCategoria("Biodegradables");
        return p;
    }

    private PedidoReabastecimiento buildPedidoAprobado(Producto producto, int cantidad) {
        PedidoReabastecimiento pedido = new PedidoReabastecimiento();
        pedido.setId(1L);
        pedido.setProducto(producto);
        pedido.setCantidad(cantidad);
        pedido.setEstado(PedidoReabastecimiento.Estado.APROBADO);
        pedido.setCreadoPor("jefeBodega");
        return pedido;
    }

    private RecepcionMercancia buildRecepcion(PedidoReabastecimiento pedido,
            int recibida, int danada, RecepcionMercancia.EstadoRecepcion estado) {
        RecepcionMercancia r = new RecepcionMercancia();
        r.setId(1L);
        r.setPedido(pedido);
        r.setCantidadRecibida(recibida);
        r.setCantidadDanada(danada);
        r.setEstado(estado);
        r.setRegistradoPor("recepcionista1");
        return r;
    }

    // Alta de recepción → persiste y retorna DTO con datos completos
    @Test
    void registrarRecepcion_conDatosValidos_persisteYRetornaDTO() {
        Producto producto = buildProducto();
        PedidoReabastecimiento pedido = buildPedidoAprobado(producto, 100);

        RecepcionMercanciaRequestDTO dto = new RecepcionMercanciaRequestDTO();
        dto.setPedidoId(1L);
        dto.setCantidadRecibida(100);
        dto.setCantidadDanada(0);
        dto.setRegistradoPor("recepcionista1");

        RecepcionMercancia guardada = buildRecepcion(pedido, 100, 0,
                RecepcionMercancia.EstadoRecepcion.CONFORME);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(pedidoRepository.save(any(PedidoReabastecimiento.class))).thenReturn(pedido);
        when(recepcionRepository.save(any(RecepcionMercancia.class))).thenReturn(guardada);

        RecepcionMercanciaResponseDTO result =
                recepcionMercanciaService.registrarRecepcion(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100, result.getCantidadRecibida());
        assertEquals("CONFORME", result.getEstado());
        verify(recepcionRepository).save(any(RecepcionMercancia.class));
    }

    // Evaluación de desempeño: recepción con daños → estado CON_DANOS
    @Test
    void registrarRecepcion_conMercanciaConDanos_retornaEstadoConDanos() {
        Producto producto = buildProducto();
        PedidoReabastecimiento pedido = buildPedidoAprobado(producto, 100);

        RecepcionMercanciaRequestDTO dto = new RecepcionMercanciaRequestDTO();
        dto.setPedidoId(1L);
        dto.setCantidadRecibida(100);
        dto.setCantidadDanada(10);
        dto.setRegistradoPor("recepcionista1");

        RecepcionMercancia guardada = buildRecepcion(pedido, 100, 10,
                RecepcionMercancia.EstadoRecepcion.CON_DANOS);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(pedidoRepository.save(any(PedidoReabastecimiento.class))).thenReturn(pedido);
        when(recepcionRepository.save(any(RecepcionMercancia.class))).thenReturn(guardada);

        RecepcionMercanciaResponseDTO result =
                recepcionMercanciaService.registrarRecepcion(dto);

        assertEquals("CON_DANOS", result.getEstado());
        assertEquals(10, result.getCantidadDanada());
    }

    // Proveedor/Pedido inexistente → lanza RecursoNoEncontradoException
    @Test
    void registrarRecepcion_conPedidoInexistente_lanzaRecursoNoEncontradoException() {
        RecepcionMercanciaRequestDTO dto = new RecepcionMercanciaRequestDTO();
        dto.setPedidoId(99L);
        dto.setCantidadRecibida(50);
        dto.setRegistradoPor("recepcionista1");

        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> recepcionMercanciaService.registrarRecepcion(dto));
        verify(recepcionRepository, never()).save(any());
    }
}

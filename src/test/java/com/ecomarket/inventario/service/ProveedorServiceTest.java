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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Test
    void registrarRecepcion_conPedidoNoAprobado_lanzaIllegalArgumentException() {
        Producto producto = buildProducto();
        PedidoReabastecimiento pedido = buildPedidoAprobado(producto, 100);
        pedido.setEstado(PedidoReabastecimiento.Estado.PENDIENTE);

        RecepcionMercanciaRequestDTO dto = new RecepcionMercanciaRequestDTO();
        dto.setPedidoId(1L);
        dto.setCantidadRecibida(100);
        dto.setRegistradoPor("recepcionista1");

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(IllegalArgumentException.class,
                () -> recepcionMercanciaService.registrarRecepcion(dto));
        verify(recepcionRepository, never()).save(any());
    }

    @Test
    void registrarRecepcion_conCantidadRecibidaNula_lanzaIllegalArgumentException() {
        Producto producto = buildProducto();
        PedidoReabastecimiento pedido = buildPedidoAprobado(producto, 100);

        RecepcionMercanciaRequestDTO dto = new RecepcionMercanciaRequestDTO();
        dto.setPedidoId(1L);
        dto.setCantidadRecibida(null);
        dto.setRegistradoPor("recepcionista1");

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(IllegalArgumentException.class,
                () -> recepcionMercanciaService.registrarRecepcion(dto));
    }

    @Test
    void registrarRecepcion_conCantidadRecibidaCero_lanzaIllegalArgumentException() {
        Producto producto = buildProducto();
        PedidoReabastecimiento pedido = buildPedidoAprobado(producto, 100);

        RecepcionMercanciaRequestDTO dto = new RecepcionMercanciaRequestDTO();
        dto.setPedidoId(1L);
        dto.setCantidadRecibida(0);
        dto.setRegistradoPor("recepcionista1");

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(IllegalArgumentException.class,
                () -> recepcionMercanciaService.registrarRecepcion(dto));
    }

    @Test
    void registrarRecepcion_conCantidadDanadaNegativa_lanzaIllegalArgumentException() {
        Producto producto = buildProducto();
        PedidoReabastecimiento pedido = buildPedidoAprobado(producto, 100);

        RecepcionMercanciaRequestDTO dto = new RecepcionMercanciaRequestDTO();
        dto.setPedidoId(1L);
        dto.setCantidadRecibida(100);
        dto.setCantidadDanada(-1);
        dto.setRegistradoPor("recepcionista1");

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(IllegalArgumentException.class,
                () -> recepcionMercanciaService.registrarRecepcion(dto));
    }

    @Test
    void registrarRecepcion_conCantidadDanadaMayorQueRecibida_lanzaIllegalArgumentException() {
        Producto producto = buildProducto();
        PedidoReabastecimiento pedido = buildPedidoAprobado(producto, 100);

        RecepcionMercanciaRequestDTO dto = new RecepcionMercanciaRequestDTO();
        dto.setPedidoId(1L);
        dto.setCantidadRecibida(50);
        dto.setCantidadDanada(60);
        dto.setRegistradoPor("recepcionista1");

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(IllegalArgumentException.class,
                () -> recepcionMercanciaService.registrarRecepcion(dto));
    }

    @Test
    void registrarRecepcion_conCantidadDiferenteAlPedido_retornaEstadoConDiferencias() {
        Producto producto = buildProducto();
        PedidoReabastecimiento pedido = buildPedidoAprobado(producto, 100);

        RecepcionMercanciaRequestDTO dto = new RecepcionMercanciaRequestDTO();
        dto.setPedidoId(1L);
        dto.setCantidadRecibida(80);
        dto.setCantidadDanada(0);
        dto.setRegistradoPor("recepcionista1");

        RecepcionMercancia guardada = buildRecepcion(pedido, 80, 0,
                RecepcionMercancia.EstadoRecepcion.CON_DIFERENCIAS);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(pedidoRepository.save(any(PedidoReabastecimiento.class))).thenReturn(pedido);
        when(recepcionRepository.save(any(RecepcionMercancia.class))).thenReturn(guardada);

        RecepcionMercanciaResponseDTO result =
                recepcionMercanciaService.registrarRecepcion(dto);

        assertEquals("CON_DIFERENCIAS", result.getEstado());
    }

    @Test
    void registrarRecepcion_conTodaDanada_noActualizaStock() {
        Producto producto = buildProducto();
        PedidoReabastecimiento pedido = buildPedidoAprobado(producto, 100);

        RecepcionMercanciaRequestDTO dto = new RecepcionMercanciaRequestDTO();
        dto.setPedidoId(1L);
        dto.setCantidadRecibida(50);
        dto.setCantidadDanada(50);
        dto.setRegistradoPor("recepcionista1");

        RecepcionMercancia guardada = buildRecepcion(pedido, 50, 50,
                RecepcionMercancia.EstadoRecepcion.CON_DANOS);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(PedidoReabastecimiento.class))).thenReturn(pedido);
        when(recepcionRepository.save(any(RecepcionMercancia.class))).thenReturn(guardada);

        recepcionMercanciaService.registrarRecepcion(dto);

        verify(productoRepository, never()).save(any());
    }

    @Test
    void listarRecepciones_retornaListaCompleta() {
        Producto producto = buildProducto();
        PedidoReabastecimiento pedido = buildPedidoAprobado(producto, 100);
        RecepcionMercancia r1 = buildRecepcion(pedido, 100, 0, RecepcionMercancia.EstadoRecepcion.CONFORME);
        RecepcionMercancia r2 = buildRecepcion(pedido, 50, 5, RecepcionMercancia.EstadoRecepcion.CON_DANOS);
        r2.setId(2L);

        when(recepcionRepository.findAll()).thenReturn(List.of(r1, r2));

        List<RecepcionMercanciaResponseDTO> result = recepcionMercanciaService.listarRecepciones();

        assertEquals(2, result.size());
        verify(recepcionRepository).findAll();
    }

    @Test
    void obtenerPorPedido_retornaListaDelPedido() {
        Producto producto = buildProducto();
        PedidoReabastecimiento pedido = buildPedidoAprobado(producto, 100);
        RecepcionMercancia r = buildRecepcion(pedido, 100, 0, RecepcionMercancia.EstadoRecepcion.CONFORME);

        when(recepcionRepository.findByPedidoId(1L)).thenReturn(List.of(r));

        List<RecepcionMercanciaResponseDTO> result = recepcionMercanciaService.obtenerPorPedido(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getPedidoId());
        verify(recepcionRepository).findByPedidoId(1L);
    }

    @Test
    void registrarRecepcion_conCantidadDanadaNoEspecificada_tomaCeroComoDefault() {
        Producto producto = buildProducto();
        PedidoReabastecimiento pedido = buildPedidoAprobado(producto, 100);

        RecepcionMercanciaRequestDTO dto = new RecepcionMercanciaRequestDTO();
        dto.setPedidoId(1L);
        dto.setCantidadRecibida(100);
        // cantidadDanada no seteada → null → ternario toma el branch else → 0
        dto.setRegistradoPor("recepcionista1");

        RecepcionMercancia guardada = buildRecepcion(pedido, 100, 0,
                RecepcionMercancia.EstadoRecepcion.CONFORME);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(pedidoRepository.save(any(PedidoReabastecimiento.class))).thenReturn(pedido);
        when(recepcionRepository.save(any(RecepcionMercancia.class))).thenReturn(guardada);

        RecepcionMercanciaResponseDTO result = recepcionMercanciaService.registrarRecepcion(dto);

        assertNotNull(result);
        assertEquals("CONFORME", result.getEstado());
        assertEquals(0, result.getCantidadDanada());
        verify(recepcionRepository).save(any(RecepcionMercancia.class));
    }
}

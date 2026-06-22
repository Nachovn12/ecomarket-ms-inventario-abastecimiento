package com.ecomarket.inventario.service;

import com.ecomarket.inventario.dto.AjusteStockRequestDTO;
import com.ecomarket.inventario.dto.AjusteStockResponseDTO;
import com.ecomarket.inventario.exception.RecursoNoEncontradoException;
import com.ecomarket.inventario.exception.ReglaDeNegocioException;
import com.ecomarket.inventario.model.AjusteStock;
import com.ecomarket.inventario.model.Producto;
import com.ecomarket.inventario.repository.AjusteStockRepository;
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
class AjusteStockServiceTest {

    @Mock
    private AjusteStockRepository ajusteStockRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private AjusteStockService ajusteStockService;

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

    private AjusteStock buildAjuste(Producto producto, int anterior, int nuevo, String motivo) {
        AjusteStock a = new AjusteStock();
        a.setId(1L);
        a.setProducto(producto);
        a.setCantidadAnterior(anterior);
        a.setCantidadNueva(nuevo);
        a.setMotivo(motivo);
        a.setUsuarioResponsable("operador1");
        return a;
    }

    private AjusteStockRequestDTO buildDTO(int cantidadNueva, String motivo) {
        AjusteStockRequestDTO dto = new AjusteStockRequestDTO();
        dto.setProductoId(1L);
        dto.setCantidadNueva(cantidadNueva);
        dto.setMotivo(motivo);
        dto.setUsuarioResponsable("operador1");
        return dto;
    }

    @Test
    void ajustarStock_conMotivoMerma_registraMovimientoNegativo() {
        Producto producto = buildProducto(50, 5);
        AjusteStockRequestDTO dto = buildDTO(30, "MERMA");

        AjusteStock ajuste = buildAjuste(producto, 50, 30, "MERMA");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(ajusteStockRepository.save(any(AjusteStock.class))).thenReturn(ajuste);

        AjusteStockResponseDTO result = ajusteStockService.ajustarStock(dto);

        assertNotNull(result);
        assertEquals("MERMA", result.getMotivo());
        assertTrue(result.getCantidadNueva() < result.getCantidadAnterior(),
                "El ajuste por MERMA debe ser negativo (cantidadNueva < cantidadAnterior)");
        verify(ajusteStockRepository).save(any(AjusteStock.class));
    }

    @Test
    void ajustarStock_conMotivoRecepcionCompra_registraMovimientoPositivo() {
        Producto producto = buildProducto(20, 5);
        AjusteStockRequestDTO dto = buildDTO(80, "RECEPCION_COMPRA");

        AjusteStock ajuste = buildAjuste(producto, 20, 80, "RECEPCION_COMPRA");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(ajusteStockRepository.save(any(AjusteStock.class))).thenReturn(ajuste);

        AjusteStockResponseDTO result = ajusteStockService.ajustarStock(dto);

        assertNotNull(result);
        assertEquals("RECEPCION_COMPRA", result.getMotivo());
        assertTrue(result.getCantidadNueva() > result.getCantidadAnterior(),
                "El ajuste por RECEPCION_COMPRA debe ser positivo (cantidadNueva > cantidadAnterior)");
    }

    @Test
    void ajustarStock_conMotivoCorreccionInventario_corrigeStockYGeneraMovimiento() {
        Producto producto = buildProducto(50, 5);
        AjusteStockRequestDTO dto = buildDTO(45, "CORRECCION_INVENTARIO");

        AjusteStock ajuste = buildAjuste(producto, 50, 45, "CORRECCION_INVENTARIO");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(ajusteStockRepository.save(any(AjusteStock.class))).thenReturn(ajuste);

        AjusteStockResponseDTO result = ajusteStockService.ajustarStock(dto);

        assertNotNull(result);
        assertEquals("CORRECCION_INVENTARIO", result.getMotivo());
        verify(productoRepository).save(any(Producto.class));
        verify(ajusteStockRepository).save(any(AjusteStock.class));
    }

    @Test
    void ajustarStock_conStockResultanteBajoMinimo_lanzaReglaDeNegocioException() {
        Producto producto = buildProducto(50, 20);
        AjusteStockRequestDTO dto = buildDTO(5, "MERMA");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertThrows(ReglaDeNegocioException.class,
                () -> ajusteStockService.ajustarStock(dto));
        verify(ajusteStockRepository, never()).save(any());
    }

    @Test
    void ajustarStock_conProductoInexistente_lanzaRecursoNoEncontradoException() {
        AjusteStockRequestDTO dto = buildDTO(30, "MERMA");
        dto.setProductoId(99L);
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> ajusteStockService.ajustarStock(dto));
        verify(ajusteStockRepository, never()).save(any());
    }

    @Test
    void obtenerHistorialPorProducto_retornaListaDelRepository() {
        Producto producto = buildProducto(50, 5);
        AjusteStock ajuste1 = buildAjuste(producto, 50, 30, "MERMA");
        AjusteStock ajuste2 = buildAjuste(producto, 30, 80, "RECEPCION_COMPRA");
        ajuste2.setId(2L);

        when(ajusteStockRepository.findByProductoId(1L)).thenReturn(List.of(ajuste1, ajuste2));

        List<AjusteStockResponseDTO> result = ajusteStockService.obtenerHistorialPorProducto(1L);

        assertEquals(2, result.size());
        assertEquals("MERMA", result.get(0).getMotivo());
        assertEquals("RECEPCION_COMPRA", result.get(1).getMotivo());
        verify(ajusteStockRepository).findByProductoId(1L);
    }

    @Test
    void listarAjustes_retornaListaCompleta() {
        Producto producto = buildProducto(50, 5);
        AjusteStock ajuste = buildAjuste(producto, 50, 30, "MERMA");

        when(ajusteStockRepository.findAll()).thenReturn(List.of(ajuste));

        List<AjusteStockResponseDTO> result = ajusteStockService.listarAjustes();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(ajusteStockRepository).findAll();
    }
}

package com.ecomarket.inventario.service;

import com.ecomarket.inventario.dto.AjusteStockRequestDTO;
import com.ecomarket.inventario.dto.AjusteStockResponseDTO;
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

    // AC-5: Ajuste con motivo MERMA → registra movimiento negativo en historial
    @Test
    void ajustarStock_conMotivoMerma_registraMovimientoNegativo() {
        Producto producto = buildProducto(50, 5);
        AjusteStockRequestDTO dto = buildDTO(30, "MERMA"); // 30 < 50 → movimiento negativo; 30 >= 5 mínimo

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

    // AC-5: Ajuste con motivo RECEPCION_COMPRA → registra movimiento positivo en historial
    @Test
    void ajustarStock_conMotivoRecepcionCompra_registraMovimientoPositivo() {
        Producto producto = buildProducto(20, 5);
        AjusteStockRequestDTO dto = buildDTO(80, "RECEPCION_COMPRA"); // 80 > 20 → movimiento positivo

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

    // AC-5: Ajuste con motivo CORRECCION_INVENTARIO → corrige stock y genera movimiento
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

    // AC-3: Ajuste que deja stock bajo mínimo → lanza excepción de negocio
    @Test
    void ajustarStock_conStockResultanteBajoMinimo_lanzaReglaDeNegocioException() {
        Producto producto = buildProducto(50, 20); // mínimo = 20
        AjusteStockRequestDTO dto = buildDTO(5, "MERMA"); // 5 < 20 → debe lanzar excepción

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertThrows(ReglaDeNegocioException.class,
                () -> ajusteStockService.ajustarStock(dto));
        verify(ajusteStockRepository, never()).save(any());
    }
}

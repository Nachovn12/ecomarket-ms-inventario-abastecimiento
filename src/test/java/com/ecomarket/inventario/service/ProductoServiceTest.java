package com.ecomarket.inventario.service;

import com.ecomarket.inventario.dto.ProductoRequestDTO;
import com.ecomarket.inventario.dto.ProductoResponseDTO;
import com.ecomarket.inventario.exception.RecursoNoEncontradoException;
import com.ecomarket.inventario.exception.SkuDuplicadoException;
import com.ecomarket.inventario.model.Producto;
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
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private ProductoRequestDTO buildRequestDTO(String nombre, String sku, int stock) {
        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre(nombre);
        dto.setSku(sku);
        dto.setPrecio(1990.0);
        dto.setStock(stock);
        dto.setCategoria("Biodegradables");
        dto.setSucursal("Santiago Centro");
        return dto;
    }

    private Producto buildProducto(Long id, String nombre, String sku, int stock) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        p.setSku(sku);
        p.setPrecio(1990.0);
        p.setStock(stock);
        p.setCategoria("Biodegradables");
        p.setSucursal("Santiago Centro");
        return p;
    }

    @Test
    void agregarProducto_conDatosValidos_persisteYRetornaDtoConId() {
        ProductoRequestDTO dto = buildRequestDTO("Bolsa Biodegradable", "ECO-001", 100);
        Producto saved = buildProducto(1L, "Bolsa Biodegradable", "ECO-001", 100);

        when(productoRepository.existsBySku("ECO-001")).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(saved);

        ProductoResponseDTO result = productoService.agregarProducto(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("DISPONIBLE", result.getDisponibilidad());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void agregarProducto_conSkuDuplicado_lanzaSkuDuplicadoException() {
        ProductoRequestDTO dto = buildRequestDTO("Bolsa Biodegradable", "ECO-001", 100);
        when(productoRepository.existsBySku("ECO-001")).thenReturn(true);

        assertThrows(SkuDuplicadoException.class,
                () -> productoService.agregarProducto(dto));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void listarProductos_retornaListaCompleta() {
        Producto p1 = buildProducto(1L, "Bolsa Biodegradable", "ECO-001", 100);
        Producto p2 = buildProducto(2L, "Envase Reutilizable", "ECO-002", 0);
        when(productoRepository.findAll()).thenReturn(List.of(p1, p2));

        List<ProductoResponseDTO> result = productoService.listarProductos();

        assertEquals(2, result.size());
        assertEquals("DISPONIBLE", result.get(0).getDisponibilidad());
        assertEquals("SIN STOCK", result.get(1).getDisponibilidad());
        verify(productoRepository).findAll();
    }

    @Test
    void obtenerProducto_conIdValido_retornaDTO() {
        Producto p = buildProducto(1L, "Bolsa Biodegradable", "ECO-001", 100);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(p));

        ProductoResponseDTO result = productoService.obtenerProducto(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ECO-001", result.getSku());
    }

    @Test
    void obtenerProducto_conIdInexistente_lanzaRecursoNoEncontradoException() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> productoService.obtenerProducto(99L));
        verify(productoRepository).findById(99L);
    }

    @Test
    void obtenerPorSku_conSkuExistente_retornaDTO() {
        Producto p = buildProducto(1L, "Bolsa Biodegradable", "ECO-001", 50);
        when(productoRepository.findBySku("ECO-001")).thenReturn(Optional.of(p));

        ProductoResponseDTO result = productoService.obtenerPorSku("ECO-001");

        assertNotNull(result);
        assertEquals("ECO-001", result.getSku());
    }

    @Test
    void obtenerPorSku_conSkuInexistente_lanzaRecursoNoEncontradoException() {
        when(productoRepository.findBySku("NOEXISTE")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> productoService.obtenerPorSku("NOEXISTE"));
    }

    @Test
    void actualizarProducto_conDatosValidos_modificaCamposYRetornaDtoActualizado() {
        Producto existente = buildProducto(1L, "Nombre Viejo", "ECO-001", 50);
        ProductoRequestDTO dto = buildRequestDTO("Nombre Nuevo", "ECO-001", 200);
        Producto actualizado = buildProducto(1L, "Nombre Nuevo", "ECO-001", 200);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.save(any(Producto.class))).thenReturn(actualizado);

        ProductoResponseDTO result = productoService.actualizarProducto(1L, dto);

        assertNotNull(result);
        assertEquals("Nombre Nuevo", result.getNombre());
        assertEquals(200, result.getStock());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void actualizarProducto_conSkuCambiado_yaExiste_lanzaSkuDuplicadoException() {
        Producto existente = buildProducto(1L, "Bolsa", "ECO-001", 50);
        ProductoRequestDTO dto = buildRequestDTO("Bolsa", "ECO-002", 50);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.existsBySku("ECO-002")).thenReturn(true);

        assertThrows(SkuDuplicadoException.class,
                () -> productoService.actualizarProducto(1L, dto));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void actualizarProducto_conIdInexistente_lanzaRecursoNoEncontradoException() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> productoService.actualizarProducto(99L, buildRequestDTO("X", "SKU", 1)));
    }

    @Test
    void eliminarProducto_conIdExistente_llamaDeleteById() {
        when(productoRepository.existsById(1L)).thenReturn(true);

        productoService.eliminarProducto(1L);

        verify(productoRepository).deleteById(1L);
    }

    @Test
    void eliminarProducto_conIdInexistente_lanzaRecursoNoEncontradoException() {
        when(productoRepository.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class,
                () -> productoService.eliminarProducto(99L));
        verify(productoRepository, never()).deleteById(any());
    }

    @Test
    void buscarPorNombre_retornaListaFiltrada() {
        Producto p = buildProducto(1L, "Bolsa Biodegradable", "ECO-001", 10);
        when(productoRepository.findByNombreContainingIgnoreCase("bolsa")).thenReturn(List.of(p));

        List<ProductoResponseDTO> result = productoService.buscarPorNombre("bolsa");

        assertEquals(1, result.size());
        assertEquals("Bolsa Biodegradable", result.get(0).getNombre());
    }

    @Test
    void buscarPorCategoria_retornaListaFiltrada() {
        Producto p = buildProducto(1L, "Bolsa Biodegradable", "ECO-001", 10);
        when(productoRepository.findByCategoriaIgnoreCase("Biodegradables")).thenReturn(List.of(p));

        List<ProductoResponseDTO> result = productoService.buscarPorCategoria("Biodegradables");

        assertEquals(1, result.size());
        assertEquals("Biodegradables", result.get(0).getCategoria());
    }

    @Test
    void buscarPorSucursal_retornaListaFiltrada() {
        Producto p = buildProducto(1L, "Bolsa Biodegradable", "ECO-001", 10);
        when(productoRepository.findBySucursalIgnoreCase("Santiago Centro")).thenReturn(List.of(p));

        List<ProductoResponseDTO> result = productoService.buscarPorSucursal("Santiago Centro");

        assertEquals(1, result.size());
        assertEquals("Santiago Centro", result.get(0).getSucursal());
    }

    @Test
    void agregarProducto_conStockCero_retornaDisponibilidadSinStock() {
        ProductoRequestDTO dto = buildRequestDTO("Bolsa Biodegradable", "ECO-003", 0);
        Producto saved = buildProducto(3L, "Bolsa Biodegradable", "ECO-003", 0);

        when(productoRepository.existsBySku("ECO-003")).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(saved);

        ProductoResponseDTO result = productoService.agregarProducto(dto);

        assertEquals("SIN STOCK", result.getDisponibilidad());
    }
}

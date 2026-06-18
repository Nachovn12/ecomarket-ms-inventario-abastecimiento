package com.ecomarket.inventario.service;

import com.ecomarket.inventario.dto.ProductoRequestDTO;
import com.ecomarket.inventario.dto.ProductoResponseDTO;
import com.ecomarket.inventario.exception.RecursoNoEncontradoException;
import com.ecomarket.inventario.model.Producto;
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

    // Alta de nuevo producto → persiste y retorna DTO con id
    @Test
    void agregarProducto_conDatosValidos_persisteYRetornaDtoConId() {
        ProductoRequestDTO dto = buildRequestDTO("Bolsa Biodegradable", "ECO-001", 100);
        Producto saved = buildProducto(1L, "Bolsa Biodegradable", "ECO-001", 100);

        when(productoRepository.existsBySku("ECO-001")).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(saved);

        ProductoResponseDTO result = productoService.agregarProducto(dto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(1L, result.getId());
        assertEquals("Bolsa Biodegradable", result.getNombre());
        assertEquals("ECO-001", result.getSku());
        assertEquals("DISPONIBLE", result.getDisponibilidad());
        verify(productoRepository).save(any(Producto.class));
    }

    // Actualización de producto existente → modifica campos y retorna DTO actualizado
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

    // Obtener producto inexistente → lanza RecursoNoEncontradoException
    @Test
    void obtenerProducto_conIdInexistente_lanzaRecursoNoEncontradoException() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> productoService.obtenerProducto(99L));
        verify(productoRepository).findById(99L);
    }
}

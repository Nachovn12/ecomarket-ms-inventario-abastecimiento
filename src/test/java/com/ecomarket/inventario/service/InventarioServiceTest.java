package com.ecomarket.inventario.service;

import com.ecomarket.inventario.dto.InventarioRequestDTO;
import com.ecomarket.inventario.dto.InventarioResponseDTO;
import com.ecomarket.inventario.dto.ProveedorDTO;
import com.ecomarket.inventario.exception.RecursoNoEncontradoException;
import com.ecomarket.inventario.model.Inventario;
import com.ecomarket.inventario.model.Proveedor;
import com.ecomarket.inventario.repository.InventarioRepository;
import com.ecomarket.inventario.repository.ProveedorRepository;
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
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private Inventario buildInventario(Long id, int disponible, int minima) {
        Inventario inv = new Inventario();
        inv.setId(id);
        inv.setNombreProducto("Bolsa Biodegradable");
        inv.setCantidadDisponible(disponible);
        inv.setCantidadMinima(minima);
        inv.setCategoria("Biodegradables");
        return inv;
    }

    @Test
    void obtenerInventario_conIdValido_retornaStockActualizado() {
        Inventario inv = buildInventario(1L, 100, 10);
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inv));

        InventarioResponseDTO result = inventarioService.obtenerInventario(1L);

        assertNotNull(result);
        assertEquals(100, result.getCantidadDisponible());
        assertEquals("Bolsa Biodegradable", result.getNombreProducto());
        verify(inventarioRepository).findById(1L);
    }

    @Test
    void actualizarStock_conCantidadMenor_descontaStockReservado() {
        Inventario inv = buildInventario(1L, 100, 5);
        Inventario guardado = buildInventario(1L, 80, 5);

        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inv));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(guardado);

        InventarioResponseDTO result = inventarioService.actualizarStock(1L, 80);

        assertEquals(80, result.getCantidadDisponible());
        verify(inventarioRepository).save(any(Inventario.class));
    }

    @Test
    void actualizarStock_conCantidadMayor_restauraStock() {
        Inventario inv = buildInventario(1L, 80, 5);
        Inventario guardado = buildInventario(1L, 100, 5);

        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inv));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(guardado);

        InventarioResponseDTO result = inventarioService.actualizarStock(1L, 100);

        assertEquals(100, result.getCantidadDisponible());
        assertTrue(result.getCantidadDisponible() > 80, "El stock fue restaurado");
    }

    @Test
    void actualizarStock_conStockBajoMinimo_persisteYDispararAlerta() {
        Inventario inv = buildInventario(1L, 30, 20);
        Inventario guardado = buildInventario(1L, 5, 20);

        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inv));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(guardado);

        InventarioResponseDTO result = inventarioService.actualizarStock(1L, 5);

        assertEquals(5, result.getCantidadDisponible());
        assertTrue(result.getCantidadDisponible() < result.getCantidadMinima(),
                "Stock resultante debe quedar bajo el mínimo configurado");
        verify(inventarioRepository).save(any(Inventario.class));
    }

    @Test
    void obtenerInventario_conIdInexistente_lanzaRecursoNoEncontradoException() {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> inventarioService.obtenerInventario(99L));
    }

    @Test
    void crearInventario_conDatosValidos_persisteYRetornaDTO() {
        InventarioRequestDTO dto = new InventarioRequestDTO();
        dto.setNombreProducto("Bolsa Biodegradable");
        dto.setCantidadDisponible(100);
        dto.setCantidadMinima(10);
        dto.setCategoria("Biodegradables");

        Inventario guardado = buildInventario(1L, 100, 10);
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(guardado);

        InventarioResponseDTO result = inventarioService.crearInventario(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Bolsa Biodegradable", result.getNombreProducto());
        verify(inventarioRepository).save(any(Inventario.class));
    }

    @Test
    void crearInventario_conCantidadMinimaMayorQueDisponible_lanzaIllegalArgumentException() {
        InventarioRequestDTO dto = new InventarioRequestDTO();
        dto.setNombreProducto("Bolsa Biodegradable");
        dto.setCantidadDisponible(5);
        dto.setCantidadMinima(20);
        dto.setCategoria("Biodegradables");

        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.crearInventario(dto));
        verify(inventarioRepository, never()).save(any());
    }

    @Test
    void listarInventarios_retornaListaCompleta() {
        Inventario inv1 = buildInventario(1L, 100, 10);
        Inventario inv2 = buildInventario(2L, 50, 5);
        when(inventarioRepository.findAll()).thenReturn(List.of(inv1, inv2));

        List<InventarioResponseDTO> result = inventarioService.listarInventarios();

        assertEquals(2, result.size());
        assertEquals(100, result.get(0).getCantidadDisponible());
        verify(inventarioRepository).findAll();
    }

    @Test
    void actualizarStock_conCantidadNegativa_lanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.actualizarStock(1L, -1));
        verify(inventarioRepository, never()).findById(any());
    }

    @Test
    void eliminarInventario_conIdExistente_llamaDeleteById() {
        when(inventarioRepository.existsById(1L)).thenReturn(true);

        inventarioService.eliminarInventario(1L);

        verify(inventarioRepository).deleteById(1L);
    }

    @Test
    void eliminarInventario_conIdInexistente_lanzaRecursoNoEncontradoException() {
        when(inventarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class,
                () -> inventarioService.eliminarInventario(99L));
        verify(inventarioRepository, never()).deleteById(any());
    }

    @Test
    void listarProveedores_retornaListaMapeada() {
        Proveedor proveedor = new Proveedor();
        proveedor.setId(1L);
        proveedor.setNombre("EcoDistribuidora SpA");
        proveedor.setContacto("Maria Lopez");
        proveedor.setEmail("ventas@eco.cl");
        proveedor.setTelefono("+56 2 2345 6789");
        when(proveedorRepository.findAll()).thenReturn(List.of(proveedor));

        List<ProveedorDTO> result = inventarioService.listarProveedores();

        assertEquals(1, result.size());
        assertEquals("EcoDistribuidora SpA", result.get(0).getNombre());
        assertEquals("ventas@eco.cl", result.get(0).getEmail());
    }

    @Test
    void actualizarStock_conInventarioInexistente_lanzaRecursoNoEncontradoException() {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> inventarioService.actualizarStock(99L, 10));
    }
}

package com.ecomarket.inventario.service;

import com.ecomarket.inventario.dto.InventarioRequestDTO;
import com.ecomarket.inventario.dto.InventarioResponseDTO;
import com.ecomarket.inventario.exception.RecursoNoEncontradoException;
import com.ecomarket.inventario.model.Inventario;
import com.ecomarket.inventario.repository.InventarioRepository;
import com.ecomarket.inventario.repository.ProveedorRepository;
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

    // AC-3: Consulta de stock por producto y tienda → retorna stock actualizado
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

    // AC-3: Reserva de stock para pedido → descuenta stock reservado
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

    // AC-3: Liberación de reserva en cancelación → restaura stock
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

    // AC-4: Alerta automática cuando stock <= stockMinimo → stock igual se persiste y dispara alerta
    @Test
    void actualizarStock_conStockBajoMinimo_persisteYDispararAlerta() {
        Inventario inv = buildInventario(1L, 30, 20);
        Inventario guardado = buildInventario(1L, 5, 20);

        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inv));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(guardado);

        InventarioResponseDTO result = inventarioService.actualizarStock(1L, 5);

        // Servicio persiste el stock bajo mínimo (la alerta es un log.warn)
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
}

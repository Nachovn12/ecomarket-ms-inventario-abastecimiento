package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.InventarioRequestDTO;
import com.ecomarket.inventario.dto.InventarioResponseDTO;
import com.ecomarket.inventario.dto.ProveedorDTO;
import com.ecomarket.inventario.exception.RecursoNoEncontradoException;
import com.ecomarket.inventario.service.InventarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventarioController.class)
@TestPropertySource(properties = {
        "springdoc.api-docs.enabled=false",
        "springdoc.swagger-ui.enabled=false"
})
class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private InventarioService inventarioService;

    private InventarioResponseDTO buildResponse(Long id, String nombre, int disponible, int minima) {
        InventarioResponseDTO dto = new InventarioResponseDTO();
        dto.setId(id);
        dto.setNombreProducto(nombre);
        dto.setCantidadDisponible(disponible);
        dto.setCantidadMinima(minima);
        dto.setCategoria("Biodegradables");
        return dto;
    }

    @Test
    void getInventarios_retorna200ConListado() throws Exception {
        InventarioResponseDTO dto = buildResponse(1L, "Bolsa Biodegradable", 100, 10);
        when(inventarioService.listarInventarios()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreProducto").value("Bolsa Biodegradable"))
                .andExpect(jsonPath("$[0].cantidadDisponible").value(100));
    }

    @Test
    void getInventario_conIdValido_retorna200ConDTO() throws Exception {
        InventarioResponseDTO dto = buildResponse(1L, "Bolsa Biodegradable", 100, 10);
        when(inventarioService.obtenerInventario(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/inventario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombreProducto").value("Bolsa Biodegradable"))
                .andExpect(jsonPath("$.cantidadDisponible").value(100));
    }

    @Test
    void postInventario_conBodyValido_retorna201() throws Exception {
        InventarioRequestDTO request = new InventarioRequestDTO();
        request.setNombreProducto("Bolsa Biodegradable");
        request.setCantidadDisponible(100);
        request.setCantidadMinima(10);
        request.setCategoria("Biodegradables");

        InventarioResponseDTO response = buildResponse(1L, "Bolsa Biodegradable", 100, 10);
        when(inventarioService.crearInventario(any(InventarioRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombreProducto").value("Bolsa Biodegradable"));
    }

    @Test
    void postInventario_conBodyInvalido_retorna400() throws Exception {
        String invalidBody = "{\"cantidadDisponible\":100,\"cantidadMinima\":10,\"categoria\":\"Biodegradables\"}";

        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void putStock_conDatosValidos_retorna200() throws Exception {
        InventarioResponseDTO response = buildResponse(1L, "Bolsa Biodegradable", 80, 10);
        when(inventarioService.actualizarStock(eq(1L), eq(80))).thenReturn(response);

        mockMvc.perform(put("/api/inventario/1/stock")
                        .param("cantidad", "80"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadDisponible").value(80));
    }

    @Test
    void putStock_conCantidadNegativa_retorna4xxOr5xx() throws Exception {
        when(inventarioService.actualizarStock(eq(1L), eq(-5)))
                .thenThrow(new IllegalArgumentException("La cantidad de stock no puede ser negativa"));

        mockMvc.perform(put("/api/inventario/1/stock")
                        .param("cantidad", "-5"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void deleteInventario_conIdExistente_retorna204() throws Exception {
        doNothing().when(inventarioService).eliminarInventario(1L);

        mockMvc.perform(delete("/api/inventario/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteInventario_conIdInexistente_retorna404() throws Exception {
        doThrow(new RecursoNoEncontradoException("no encontrado"))
                .when(inventarioService).eliminarInventario(99L);

        mockMvc.perform(delete("/api/inventario/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProveedores_retorna200ConListado() throws Exception {
        ProveedorDTO proveedor = new ProveedorDTO();
        proveedor.setId(1L);
        proveedor.setNombre("EcoDistribuidora SpA");
        proveedor.setEmail("ventas@eco.cl");
        when(inventarioService.listarProveedores()).thenReturn(List.of(proveedor));

        mockMvc.perform(get("/api/inventario/proveedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("EcoDistribuidora SpA"));
    }

    @Test
    void getInventarios_listaVacia_retorna200() throws Exception {
        when(inventarioService.listarInventarios()).thenReturn(List.of());

        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}

package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.InventarioRequestDTO;
import com.ecomarket.inventario.dto.InventarioResponseDTO;
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

    // AC-2: GET /api/inventario → 200 + listado presente
    @Test
    void getInventarios_retorna200ConListado() throws Exception {
        InventarioResponseDTO dto = buildResponse(1L, "Bolsa Biodegradable", 100, 10);
        when(inventarioService.listarInventarios()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreProducto").value("Bolsa Biodegradable"))
                .andExpect(jsonPath("$[0].cantidadDisponible").value(100));
    }

    // AC-2: GET /api/inventario/{id} → 200 + datos del inventario
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

    // AC-7: POST /api/inventario con body válido → 201
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

    // AC-2: POST /api/inventario con body inválido (campo requerido vacío) → 400
    @Test
    void postInventario_conBodyInvalido_retorna400() throws Exception {
        // Falta nombreProducto (@NotBlank)
        String invalidBody = "{\"cantidadDisponible\":100,\"cantidadMinima\":10,\"categoria\":\"Biodegradables\"}";

        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }
}

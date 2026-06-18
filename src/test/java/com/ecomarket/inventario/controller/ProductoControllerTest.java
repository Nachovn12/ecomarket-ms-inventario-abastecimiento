package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.ProductoRequestDTO;
import com.ecomarket.inventario.dto.ProductoResponseDTO;
import com.ecomarket.inventario.service.ProductoService;
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

@WebMvcTest(ProductoController.class)
@TestPropertySource(properties = {
        "springdoc.api-docs.enabled=false",
        "springdoc.swagger-ui.enabled=false"
})
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ProductoService productoService;

    private ProductoResponseDTO buildResponse(Long id, String nombre, String sku) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(id);
        dto.setNombre(nombre);
        dto.setSku(sku);
        dto.setPrecio(1990.0);
        dto.setStock(100);
        dto.setCategoria("Biodegradables");
        dto.setSucursal("Santiago Centro");
        dto.setDisponibilidad("DISPONIBLE");
        return dto;
    }

    // AC-2: GET /api/inventario/productos → 200 + _embedded presente (CollectionModel con items)
    @Test
    void getProductos_retorna200ConEmbeddedYLinks() throws Exception {
        ProductoResponseDTO dto = buildResponse(1L, "Bolsa Biodegradable", "ECO-001");
        when(productoService.listarProductos()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/inventario/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._links.self").exists());
    }

    // AC-2: GET /api/inventario/productos/{id} → 200 + _links.self presente
    @Test
    void getProducto_conIdValido_retorna200ConSelfLink() throws Exception {
        ProductoResponseDTO dto = buildResponse(1L, "Bolsa Biodegradable", "ECO-001");
        when(productoService.obtenerProducto(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/inventario/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Bolsa Biodegradable"))
                .andExpect(jsonPath("$._links.self").exists());
    }

    // AC-7: POST /api/inventario/productos con body válido → 201 + _links.self presente
    @Test
    void postProducto_conBodyValido_retorna201ConSelfLink() throws Exception {
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setNombre("Bolsa Biodegradable");
        request.setSku("ECO-001");
        request.setPrecio(1990.0);
        request.setStock(100);
        request.setCategoria("Biodegradables");
        request.setSucursal("Santiago Centro");

        ProductoResponseDTO response = buildResponse(1L, "Bolsa Biodegradable", "ECO-001");
        when(productoService.agregarProducto(any(ProductoRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/inventario/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$._links.self").exists());
    }

    // AC-2: POST con body inválido (falta nombre @NotBlank) → 400
    @Test
    void postProducto_conBodyInvalido_retorna400() throws Exception {
        // Falta nombre (@NotBlank) y sku (@NotBlank)
        String invalidBody = "{\"precio\":1990.0,\"stock\":100,\"categoria\":\"Biodegradables\"}";

        mockMvc.perform(post("/api/inventario/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }
}

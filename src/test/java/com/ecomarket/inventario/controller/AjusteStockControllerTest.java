package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.AjusteStockRequestDTO;
import com.ecomarket.inventario.dto.AjusteStockResponseDTO;
import com.ecomarket.inventario.service.AjusteStockService;
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

@WebMvcTest(AjusteStockController.class)
@TestPropertySource(properties = {
        "springdoc.api-docs.enabled=false",
        "springdoc.swagger-ui.enabled=false"
})
class AjusteStockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AjusteStockService ajusteStockService;

    private AjusteStockResponseDTO buildResponse(Long id, Long productoId, int anterior, int nuevo, String motivo) {
        AjusteStockResponseDTO dto = new AjusteStockResponseDTO();
        dto.setId(id);
        dto.setProductoId(productoId);
        dto.setNombreProducto("Bolsa Biodegradable");
        dto.setCantidadAnterior(anterior);
        dto.setCantidadNueva(nuevo);
        dto.setMotivo(motivo);
        dto.setUsuarioResponsable("operador1");
        return dto;
    }

    // AC-2: GET /api/inventario/ajustes-stock → 200 + _links.self presente (CollectionModel)
    @Test
    void getAjustesStock_retorna200ConLinksHateoas() throws Exception {
        AjusteStockResponseDTO dto = buildResponse(1L, 1L, 50, 30, "MERMA");
        when(ajusteStockService.listarAjustes()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/inventario/ajustes-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._embedded").exists());
    }

    // AC-2: GET /api/inventario/ajustes-stock/producto/{id} → 200 + _links.self presente
    @Test
    void getHistorialProducto_conIdValido_retorna200ConLinks() throws Exception {
        AjusteStockResponseDTO dto = buildResponse(1L, 1L, 50, 30, "MERMA");
        when(ajusteStockService.obtenerHistorialPorProducto(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/inventario/ajustes-stock/producto/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._embedded").exists());
    }

    // AC-7: POST /api/inventario/ajustes-stock con body válido → 201 + _links presente
    @Test
    void postAjusteStock_conBodyValido_retorna201ConLinks() throws Exception {
        AjusteStockRequestDTO request = new AjusteStockRequestDTO();
        request.setProductoId(1L);
        request.setCantidadNueva(50);
        request.setMotivo("RECEPCION_COMPRA");
        request.setUsuarioResponsable("operador1");

        AjusteStockResponseDTO response = buildResponse(1L, 1L, 30, 50, "RECEPCION_COMPRA");
        when(ajusteStockService.ajustarStock(any(AjusteStockRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/inventario/ajustes-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$._links").exists())
                .andExpect(jsonPath("$._links['ajustes-stock']").exists());
    }

    // AC-2: POST con body inválido (falta motivo @NotBlank) → 400
    @Test
    void postAjusteStock_conBodyInvalido_retorna400() throws Exception {
        // Falta motivo (@NotBlank) y usuarioResponsable (@NotBlank)
        String invalidBody = "{\"productoId\":1,\"cantidadNueva\":50}";

        mockMvc.perform(post("/api/inventario/ajustes-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }
}

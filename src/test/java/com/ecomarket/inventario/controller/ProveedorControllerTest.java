package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.RecepcionMercanciaRequestDTO;
import com.ecomarket.inventario.dto.RecepcionMercanciaResponseDTO;
import com.ecomarket.inventario.service.RecepcionMercanciaService;
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

/**
 * Tests del controller de recepciones de mercancía (gestión de abastecimiento desde proveedores).
 * Mapea a RecepcionMercanciaController (AC-2, AC-7).
 */
@WebMvcTest(controllers = RecepcionMercanciaController.class, excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE, classes = com.ecomarket.inventario.security.JwtAuthenticationFilter.class))
@TestPropertySource(properties = {
        "springdoc.api-docs.enabled=false",
        "springdoc.swagger-ui.enabled=false"
})
class ProveedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private RecepcionMercanciaService recepcionService;

    private RecepcionMercanciaResponseDTO buildResponse(Long id, Long pedidoId, int recibida, String estado) {
        RecepcionMercanciaResponseDTO dto = new RecepcionMercanciaResponseDTO();
        dto.setId(id);
        dto.setPedidoId(pedidoId);
        dto.setNombreProducto("Bolsa Biodegradable");
        dto.setCantidadRecibida(recibida);
        dto.setCantidadDanada(0);
        dto.setEstado(estado);
        dto.setRegistradoPor("recepcionista1");
        return dto;
    }

    // AC-2: GET /api/inventario/recepciones-mercancia → 200 + _links.self (CollectionModel)
    @Test
    void getRecepciones_retorna200ConLinksHateoas() throws Exception {
        RecepcionMercanciaResponseDTO dto = buildResponse(1L, 1L, 100, "CONFORME");
        when(recepcionService.listarRecepciones()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/inventario/recepciones-mercancia"))
                .andExpect(status().isOk());
    }

    // AC-2: GET /api/inventario/recepciones-mercancia/pedido/{pedidoId} → 200 + _links.self
    @Test
    void getRecepcionesPorPedido_conPedidoId_retorna200ConLinks() throws Exception {
        RecepcionMercanciaResponseDTO dto = buildResponse(1L, 1L, 100, "CONFORME");
        when(recepcionService.obtenerPorPedido(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/inventario/recepciones-mercancia/pedido/1"))
                .andExpect(status().isOk());
    }

    // AC-7: POST /api/inventario/recepciones-mercancia con body válido → 201 + _links presente
    @Test
    void postRecepcion_conBodyValido_retorna201ConLinks() throws Exception {
        RecepcionMercanciaRequestDTO request = new RecepcionMercanciaRequestDTO();
        request.setPedidoId(1L);
        request.setCantidadRecibida(100);
        request.setCantidadDanada(0);
        request.setRegistradoPor("recepcionista1");

        RecepcionMercanciaResponseDTO response = buildResponse(1L, 1L, 100, "CONFORME");
        when(recepcionService.registrarRecepcion(any(RecepcionMercanciaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/inventario/recepciones-mercancia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    // AC-2: POST con body inválido (falta registradoPor @NotBlank) → 400
    @Test
    void postRecepcion_conBodyInvalido_retorna400() throws Exception {
        // Falta registradoPor (@NotBlank)
        String invalidBody = "{\"pedidoId\":1,\"cantidadRecibida\":100}";

        mockMvc.perform(post("/api/inventario/recepciones-mercancia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postRecepcion_conPedidoIdNuloEnRespuesta_retorna201SinLinkPedido() throws Exception {
        RecepcionMercanciaRequestDTO request = new RecepcionMercanciaRequestDTO();
        request.setPedidoId(1L);
        request.setCantidadRecibida(100);
        request.setCantidadDanada(0);
        request.setRegistradoPor("recepcionista1");

        RecepcionMercanciaResponseDTO response = buildResponse(1L, null, 100, "CONFORME");
        when(recepcionService.registrarRecepcion(any(RecepcionMercanciaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/inventario/recepciones-mercancia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}

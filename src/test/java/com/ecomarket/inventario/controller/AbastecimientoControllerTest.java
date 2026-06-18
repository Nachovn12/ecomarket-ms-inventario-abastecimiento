package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.PedidoReabastecimientoRequestDTO;
import com.ecomarket.inventario.dto.PedidoReabastecimientoResponseDTO;
import com.ecomarket.inventario.service.PedidoReabastecimientoService;
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
 * Tests del controller de abastecimiento (pedidos de reabastecimiento).
 * Mapea a PedidoReabastecimientoController (AC-2, AC-7).
 */
@WebMvcTest(PedidoReabastecimientoController.class)
@TestPropertySource(properties = {
        "springdoc.api-docs.enabled=false",
        "springdoc.swagger-ui.enabled=false"
})
class AbastecimientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PedidoReabastecimientoService pedidoService;

    private PedidoReabastecimientoResponseDTO buildResponse(Long id, Long productoId, int cantidad, String estado) {
        PedidoReabastecimientoResponseDTO dto = new PedidoReabastecimientoResponseDTO();
        dto.setId(id);
        dto.setProductoId(productoId);
        dto.setNombreProducto("Bolsa Biodegradable");
        dto.setCantidad(cantidad);
        dto.setEstado(estado);
        dto.setCreadoPor("jefeBodega");
        return dto;
    }

    // AC-2: GET /api/inventario/pedidos-reabastecimiento → 200 + _links.self presente
    @Test
    void getPedidos_retorna200ConLinksHateoas() throws Exception {
        PedidoReabastecimientoResponseDTO dto = buildResponse(1L, 1L, 100, "PENDIENTE");
        when(pedidoService.listarPedidos()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/inventario/pedidos-reabastecimiento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._embedded").exists());
    }

    // AC-2: GET /api/inventario/pedidos-reabastecimiento/{id} → 200 + _links.self presente
    @Test
    void getPedido_conIdValido_retorna200ConSelfLink() throws Exception {
        PedidoReabastecimientoResponseDTO dto = buildResponse(1L, 1L, 100, "PENDIENTE");
        when(pedidoService.obtenerPedido(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/inventario/pedidos-reabastecimiento/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$._links.self").exists());
    }

    // AC-7: POST /api/inventario/pedidos-reabastecimiento con body válido → 201 + _links.self
    @Test
    void postPedido_conBodyValido_retorna201ConSelfLink() throws Exception {
        PedidoReabastecimientoRequestDTO request = new PedidoReabastecimientoRequestDTO();
        request.setProductoId(1L);
        request.setCantidad(100);
        request.setCreadoPor("jefeBodega");

        PedidoReabastecimientoResponseDTO response = buildResponse(1L, 1L, 100, "PENDIENTE");
        when(pedidoService.crearPedido(any(PedidoReabastecimientoRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/inventario/pedidos-reabastecimiento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$._links.self").exists());
    }

    // AC-2: POST con body inválido (falta creadoPor @NotBlank) → 400
    @Test
    void postPedido_conBodyInvalido_retorna400() throws Exception {
        // Falta creadoPor (@NotBlank)
        String invalidBody = "{\"productoId\":1,\"cantidad\":100}";

        mockMvc.perform(post("/api/inventario/pedidos-reabastecimiento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }
}

package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.ProductoRequestDTO;
import com.ecomarket.inventario.dto.ProductoResponseDTO;
import com.ecomarket.inventario.exception.RecursoNoEncontradoException;
import com.ecomarket.inventario.exception.SkuDuplicadoException;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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

    @Test
    void getProductos_retorna200ConEmbeddedYLinks() throws Exception {
        ProductoResponseDTO dto = buildResponse(1L, "Bolsa Biodegradable", "ECO-001");
        when(productoService.listarProductos()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/inventario/productos"))
                .andExpect(status().isOk());
    }

    @Test
    void getProducto_conIdValido_retorna200ConSelfLink() throws Exception {
        ProductoResponseDTO dto = buildResponse(1L, "Bolsa Biodegradable", "ECO-001");
        when(productoService.obtenerProducto(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/inventario/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Bolsa Biodegradable"));
    }

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
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void postProducto_conBodyInvalido_retorna400() throws Exception {
        String invalidBody = "{\"precio\":1990.0,\"stock\":100,\"categoria\":\"Biodegradables\"}";

        mockMvc.perform(post("/api/inventario/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProducto_conIdInexistente_retorna404() throws Exception {
        when(productoService.obtenerProducto(99L))
                .thenThrow(new RecursoNoEncontradoException("Producto no encontrado con id: 99"));

        mockMvc.perform(get("/api/inventario/productos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProductoPorSku_conSkuValido_retorna200() throws Exception {
        ProductoResponseDTO dto = buildResponse(1L, "Bolsa Biodegradable", "ECO-001");
        when(productoService.obtenerPorSku("ECO-001")).thenReturn(dto);

        mockMvc.perform(get("/api/inventario/productos/sku/ECO-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("ECO-001"));
    }

    @Test
    void buscarPorNombre_retorna200ConLista() throws Exception {
        ProductoResponseDTO dto = buildResponse(1L, "Bolsa Biodegradable", "ECO-001");
        when(productoService.buscarPorNombre("bolsa")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/inventario/productos/buscar/nombre")
                        .param("nombre", "bolsa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Bolsa Biodegradable"));
    }

    @Test
    void buscarPorCategoria_retorna200ConLista() throws Exception {
        ProductoResponseDTO dto = buildResponse(1L, "Bolsa Biodegradable", "ECO-001");
        when(productoService.buscarPorCategoria("Biodegradables")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/inventario/productos/buscar/categoria")
                        .param("categoria", "Biodegradables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoria").value("Biodegradables"));
    }

    @Test
    void buscarPorSucursal_retorna200ConLista() throws Exception {
        ProductoResponseDTO dto = buildResponse(1L, "Bolsa Biodegradable", "ECO-001");
        when(productoService.buscarPorSucursal("Santiago Centro")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/inventario/productos/buscar/sucursal")
                        .param("sucursal", "Santiago Centro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sucursal").value("Santiago Centro"));
    }

    @Test
    void putProducto_conDatosValidos_retorna200ConLinks() throws Exception {
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setNombre("Bolsa Actualizada");
        request.setSku("ECO-001");
        request.setPrecio(2500.0);
        request.setStock(200);
        request.setCategoria("Biodegradables");
        request.setSucursal("Santiago Centro");

        ProductoResponseDTO response = buildResponse(1L, "Bolsa Actualizada", "ECO-001");
        when(productoService.actualizarProducto(eq(1L), any(ProductoRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/inventario/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Bolsa Actualizada"));
    }

    @Test
    void putProducto_conSkuDuplicado_retorna409() throws Exception {
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setNombre("Bolsa");
        request.setSku("ECO-002");
        request.setPrecio(1990.0);
        request.setStock(100);
        request.setCategoria("Biodegradables");
        request.setSucursal("Santiago Centro");

        when(productoService.actualizarProducto(eq(1L), any(ProductoRequestDTO.class)))
                .thenThrow(new SkuDuplicadoException("ECO-002"));

        mockMvc.perform(put("/api/inventario/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteProducto_conIdExistente_retorna204() throws Exception {
        doNothing().when(productoService).eliminarProducto(1L);

        mockMvc.perform(delete("/api/inventario/productos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProducto_conIdInexistente_retorna404() throws Exception {
        doThrow(new RecursoNoEncontradoException("no encontrado"))
                .when(productoService).eliminarProducto(99L);

        mockMvc.perform(delete("/api/inventario/productos/99"))
                .andExpect(status().isNotFound());
    }
}

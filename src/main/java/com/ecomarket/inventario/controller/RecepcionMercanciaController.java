package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.RecepcionMercanciaRequestDTO;
import com.ecomarket.inventario.dto.RecepcionMercanciaResponseDTO;
import com.ecomarket.inventario.service.RecepcionMercanciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario/recepciones-mercancia")
@Tag(name = "Recepciones de Mercancia", description = "Registro de recepciones fisicas contra pedidos de reabastecimiento")
public class RecepcionMercanciaController {

    @Autowired
    private RecepcionMercanciaService recepcionService;

    @Operation(summary = "Registrar una recepcion de mercancia",
            description = "Asocia la mercancia recibida a un pedido de reabastecimiento e incrementa stock.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recepcion registrada",
                    content = @Content(schema = @Schema(implementation = RecepcionMercanciaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<RecepcionMercanciaResponseDTO> registrarRecepcion(
            @Valid @RequestBody RecepcionMercanciaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recepcionService.registrarRecepcion(dto));
    }

    @Operation(summary = "Listar todas las recepciones de mercancia")
    @ApiResponse(responseCode = "200", description = "Listado de recepciones",
            content = @Content(schema = @Schema(implementation = RecepcionMercanciaResponseDTO.class)))
    @GetMapping
    public ResponseEntity<List<RecepcionMercanciaResponseDTO>> listarRecepciones() {
        return ResponseEntity.ok(recepcionService.listarRecepciones());
    }

    @Operation(summary = "Obtener recepciones de un pedido de reabastecimiento")
    @ApiResponse(responseCode = "200", description = "Recepciones del pedido",
            content = @Content(schema = @Schema(implementation = RecepcionMercanciaResponseDTO.class)))
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<RecepcionMercanciaResponseDTO>> obtenerPorPedido(
            @Parameter(description = "ID del pedido", example = "1", required = true) @PathVariable Long pedidoId) {
        return ResponseEntity.ok(recepcionService.obtenerPorPedido(pedidoId));
    }
}

package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.AjusteStockRequestDTO;
import com.ecomarket.inventario.dto.AjusteStockResponseDTO;
import com.ecomarket.inventario.service.AjusteStockService;
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
@RequestMapping("/api/inventario/ajustes-stock")
@Tag(name = "Ajustes de Stock", description = "Registro y consulta de ajustes manuales de stock")
public class AjusteStockController {

    @Autowired
    private AjusteStockService ajusteStockService;

    @Operation(summary = "Registrar un ajuste de stock",
            description = "Aplica un ajuste manual (positivo o negativo) al stock de un producto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ajuste registrado",
                    content = @Content(schema = @Schema(implementation = AjusteStockResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<AjusteStockResponseDTO> ajustarStock(@Valid @RequestBody AjusteStockRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ajusteStockService.ajustarStock(dto));
    }

    @Operation(summary = "Obtener historial de ajustes por producto")
    @ApiResponse(responseCode = "200", description = "Historial encontrado",
            content = @Content(schema = @Schema(implementation = AjusteStockResponseDTO.class)))
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<AjusteStockResponseDTO>> obtenerHistorial(
            @Parameter(description = "ID del producto", example = "1", required = true) @PathVariable Long productoId) {
        return ResponseEntity.ok(ajusteStockService.obtenerHistorialPorProducto(productoId));
    }

    @Operation(summary = "Listar todos los ajustes de stock")
    @ApiResponse(responseCode = "200", description = "Listado de ajustes",
            content = @Content(schema = @Schema(implementation = AjusteStockResponseDTO.class)))
    @GetMapping
    public ResponseEntity<List<AjusteStockResponseDTO>> listarAjustes() {
        return ResponseEntity.ok(ajusteStockService.listarAjustes());
    }
}

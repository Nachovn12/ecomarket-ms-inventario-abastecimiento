package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.InventarioRequestDTO;
import com.ecomarket.inventario.dto.InventarioResponseDTO;
import com.ecomarket.inventario.dto.ProveedorDTO;
import com.ecomarket.inventario.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller del inventario legado.
 * Patron CSR: recibe HTTP, delega al service, retorna ResponseEntity.
 * CRUD completo: POST, GET (all y by id), PUT (stock), DELETE.
 */
@RestController
@RequestMapping("/api/inventario")
@Tag(name = "Inventario (Legado)", description = "Gestion de registros de inventario por tienda/sucursal")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Operation(summary = "Crear un nuevo registro de inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inventario creado",
                    content = @Content(schema = @Schema(implementation = InventarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflicto al crear el registro", content = @Content)
    })
    @PostMapping
    public ResponseEntity<InventarioResponseDTO> crearInventario(
            @Valid @RequestBody InventarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventarioService.crearInventario(dto));
    }

    @Operation(summary = "Listar todos los registros de inventario")
    @ApiResponse(responseCode = "200", description = "Listado de inventarios",
            content = @Content(schema = @Schema(implementation = InventarioResponseDTO.class)))
    @GetMapping
    public ResponseEntity<List<InventarioResponseDTO>> listarInventarios() {
        return ResponseEntity.ok(inventarioService.listarInventarios());
    }

    @Operation(summary = "Obtener un registro de inventario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario encontrado",
                    content = @Content(schema = @Schema(implementation = InventarioResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<InventarioResponseDTO> obtenerInventario(
            @Parameter(description = "ID del inventario", example = "1", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerInventario(id));
    }

    @Operation(summary = "Actualizar el stock de un registro de inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock actualizado",
                    content = @Content(schema = @Schema(implementation = InventarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Cantidad invalida", content = @Content),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado", content = @Content)
    })
    @PutMapping("/{id}/stock")
    public ResponseEntity<InventarioResponseDTO> actualizarStock(
            @Parameter(description = "ID del inventario", example = "1", required = true) @PathVariable Long id,
            @Parameter(description = "Nueva cantidad de stock (no negativa)", example = "50", required = true)
            @RequestParam @Min(value = 0, message = "La cantidad no puede ser negativa") int cantidad) {
        return ResponseEntity.ok(inventarioService.actualizarStock(id, cantidad));
    }

    @Operation(summary = "Eliminar un registro de inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Inventario eliminado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarInventario(
            @Parameter(description = "ID del inventario", example = "1", required = true) @PathVariable Long id) {
        inventarioService.eliminarInventario(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar los proveedores del sistema")
    @ApiResponse(responseCode = "200", description = "Listado de proveedores",
            content = @Content(schema = @Schema(implementation = ProveedorDTO.class)))
    @GetMapping("/proveedores")
    public ResponseEntity<List<ProveedorDTO>> listarProveedores() {
        return ResponseEntity.ok(inventarioService.listarProveedores());
    }
}

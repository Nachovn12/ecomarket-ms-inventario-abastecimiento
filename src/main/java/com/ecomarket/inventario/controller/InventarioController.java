package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.InventarioRequestDTO;
import com.ecomarket.inventario.dto.InventarioResponseDTO;
import com.ecomarket.inventario.dto.ProveedorDTO;
import com.ecomarket.inventario.service.InventarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller del inventario legado.
 * Patrón CSR: recibe HTTP, delega al Service, retorna ResponseEntity.
 * CRUD completo: POST, GET (all y by id), PUT (stock), DELETE.
 */
@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @PostMapping
    public ResponseEntity<InventarioResponseDTO> crearInventario(
            @Valid @RequestBody InventarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventarioService.crearInventario(dto));
    }

    @GetMapping
    public ResponseEntity<List<InventarioResponseDTO>> listarInventarios() {
        return ResponseEntity.ok(inventarioService.listarInventarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioResponseDTO> obtenerInventario(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerInventario(id));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<InventarioResponseDTO> actualizarStock(
            @PathVariable Long id,
            @RequestParam @Min(value = 0, message = "La cantidad no puede ser negativa") int cantidad) {
        return ResponseEntity.ok(inventarioService.actualizarStock(id, cantidad));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarInventario(@PathVariable Long id) {
        inventarioService.eliminarInventario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/proveedores")
    public ResponseEntity<List<ProveedorDTO>> listarProveedores() {
        return ResponseEntity.ok(inventarioService.listarProveedores());
    }
}

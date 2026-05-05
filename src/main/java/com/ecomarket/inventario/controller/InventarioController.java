package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.InventarioRequestDTO;
import com.ecomarket.inventario.dto.InventarioResponseDTO;
import com.ecomarket.inventario.dto.ProveedorDTO;
import com.ecomarket.inventario.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @PostMapping
    public ResponseEntity<InventarioResponseDTO> crearInventario(@RequestBody InventarioRequestDTO dto) {
        return ResponseEntity.ok(inventarioService.crearInventario(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioResponseDTO> obtenerInventario(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerInventario(id));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<InventarioResponseDTO> actualizarStock(@PathVariable Long id, @RequestParam int cantidad) {
        return ResponseEntity.ok(inventarioService.actualizarStock(id, cantidad));
    }

    @GetMapping("/proveedores")
    public ResponseEntity<List<ProveedorDTO>> listarProveedores() {
        return ResponseEntity.ok(inventarioService.listarProveedores());
    }
}
package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.ProductoRequestDTO;
import com.ecomarket.inventario.dto.ProductoResponseDTO;
import com.ecomarket.inventario.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> agregarProducto(@Valid @RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.agregarProducto(dto));
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listarProductos() {
        return ResponseEntity.ok(productoService.listarProductos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerProducto(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerProducto(id));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductoResponseDTO> obtenerPorSku(@PathVariable String sku) {
        return ResponseEntity.ok(productoService.obtenerPorSku(sku));
    }

    @GetMapping("/buscar/nombre")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
    }

    @GetMapping("/buscar/categoria")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorCategoria(@RequestParam String categoria) {
        return ResponseEntity.ok(productoService.buscarPorCategoria(categoria));
    }

    @GetMapping("/buscar/sucursal")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorSucursal(@RequestParam String sucursal) {
        return ResponseEntity.ok(productoService.buscarPorSucursal(sucursal));
    }
}
package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.AjusteStockRequestDTO;
import com.ecomarket.inventario.dto.AjusteStockResponseDTO;
import com.ecomarket.inventario.service.AjusteStockService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ajustes-stock")
public class AjusteStockController {

    @Autowired
    private AjusteStockService ajusteStockService;

    // CA1 y CA2: Ajuste con motivo obligatorio
    @PostMapping
    public ResponseEntity<AjusteStockResponseDTO> ajustarStock(@Valid @RequestBody AjusteStockRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ajusteStockService.ajustarStock(dto));
    }

    // CA3: Historial por producto
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<AjusteStockResponseDTO>> obtenerHistorial(@PathVariable Long productoId) {
        return ResponseEntity.ok(ajusteStockService.obtenerHistorialPorProducto(productoId));
    }

    @GetMapping
    public ResponseEntity<List<AjusteStockResponseDTO>> listarAjustes() {
        return ResponseEntity.ok(ajusteStockService.listarAjustes());
    }
}
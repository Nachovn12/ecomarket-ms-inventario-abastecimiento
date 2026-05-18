package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.RecepcionMercanciaRequestDTO;
import com.ecomarket.inventario.dto.RecepcionMercanciaResponseDTO;
import com.ecomarket.inventario.service.RecepcionMercanciaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recepciones-mercancia")
public class RecepcionMercanciaController {

    @Autowired
    private RecepcionMercanciaService recepcionService;

    // CA1, CA2, CA3, CA4
    @PostMapping
    public ResponseEntity<RecepcionMercanciaResponseDTO> registrarRecepcion(@Valid @RequestBody RecepcionMercanciaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recepcionService.registrarRecepcion(dto));
    }

    @GetMapping
    public ResponseEntity<List<RecepcionMercanciaResponseDTO>> listarRecepciones() {
        return ResponseEntity.ok(recepcionService.listarRecepciones());
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<RecepcionMercanciaResponseDTO>> obtenerPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(recepcionService.obtenerPorPedido(pedidoId));
    }
}
package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.PedidoReabastecimientoRequestDTO;
import com.ecomarket.inventario.dto.PedidoReabastecimientoResponseDTO;
import com.ecomarket.inventario.service.PedidoReabastecimientoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos-reabastecimiento")
public class PedidoReabastecimientoController {

    @Autowired
    private PedidoReabastecimientoService pedidoService;

    // CA1: Crear pedido
    @PostMapping
    public ResponseEntity<PedidoReabastecimientoResponseDTO> crearPedido(@Valid @RequestBody PedidoReabastecimientoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crearPedido(dto));
    }

    // CA2: Aprobar pedido
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<PedidoReabastecimientoResponseDTO> aprobarPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.aprobarPedido(id));
    }

    // CA3: Rechazar pedido
    @PutMapping("/{id}/rechazar")
    public ResponseEntity<PedidoReabastecimientoResponseDTO> rechazarPedido(@PathVariable Long id, @RequestParam String motivo) {
        return ResponseEntity.ok(pedidoService.rechazarPedido(id, motivo));
    }

    // CA4: Consultar estado
    @GetMapping("/{id}")
    public ResponseEntity<PedidoReabastecimientoResponseDTO> obtenerPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPedido(id));
    }

    @GetMapping
    public ResponseEntity<List<PedidoReabastecimientoResponseDTO>> listarPedidos() {
        return ResponseEntity.ok(pedidoService.listarPedidos());
    }
}
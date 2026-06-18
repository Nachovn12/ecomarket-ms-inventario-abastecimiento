package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.PedidoReabastecimientoRequestDTO;
import com.ecomarket.inventario.dto.PedidoReabastecimientoResponseDTO;
import com.ecomarket.inventario.service.PedidoReabastecimientoService;
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
@RequestMapping("/api/inventario/pedidos-reabastecimiento")
@Tag(name = "Pedidos de Reabastecimiento", description = "Solicitud, aprobacion y rechazo de pedidos a proveedores")
public class PedidoReabastecimientoController {

    @Autowired
    private PedidoReabastecimientoService pedidoService;

    @Operation(summary = "Crear un pedido de reabastecimiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado",
                    content = @Content(schema = @Schema(implementation = PedidoReabastecimientoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PedidoReabastecimientoResponseDTO> crearPedido(@Valid @RequestBody PedidoReabastecimientoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crearPedido(dto));
    }

    @Operation(summary = "Aprobar un pedido de reabastecimiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido aprobado",
                    content = @Content(schema = @Schema(implementation = PedidoReabastecimientoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Pedido no se puede aprobar en su estado actual", content = @Content)
    })
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<PedidoReabastecimientoResponseDTO> aprobarPedido(
            @Parameter(description = "ID del pedido", example = "1", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.aprobarPedido(id));
    }

    @Operation(summary = "Rechazar un pedido de reabastecimiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido rechazado",
                    content = @Content(schema = @Schema(implementation = PedidoReabastecimientoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    })
    @PutMapping("/{id}/rechazar")
    public ResponseEntity<PedidoReabastecimientoResponseDTO> rechazarPedido(
            @Parameter(description = "ID del pedido", example = "1", required = true) @PathVariable Long id,
            @Parameter(description = "Motivo del rechazo", example = "Stock suficiente", required = true)
            @RequestParam String motivo) {
        return ResponseEntity.ok(pedidoService.rechazarPedido(id, motivo));
    }

    @Operation(summary = "Obtener un pedido de reabastecimiento por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado",
                    content = @Content(schema = @Schema(implementation = PedidoReabastecimientoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PedidoReabastecimientoResponseDTO> obtenerPedido(
            @Parameter(description = "ID del pedido", example = "1", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPedido(id));
    }

    @Operation(summary = "Listar todos los pedidos de reabastecimiento")
    @ApiResponse(responseCode = "200", description = "Listado de pedidos",
            content = @Content(schema = @Schema(implementation = PedidoReabastecimientoResponseDTO.class)))
    @GetMapping
    public ResponseEntity<List<PedidoReabastecimientoResponseDTO>> listarPedidos() {
        return ResponseEntity.ok(pedidoService.listarPedidos());
    }
}

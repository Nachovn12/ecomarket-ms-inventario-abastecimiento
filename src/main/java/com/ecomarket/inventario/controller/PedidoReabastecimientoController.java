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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

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
    public ResponseEntity<EntityModel<PedidoReabastecimientoResponseDTO>> crearPedido(
            @Valid @RequestBody PedidoReabastecimientoRequestDTO dto) {

        PedidoReabastecimientoResponseDTO response = pedidoService.crearPedido(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(response));
    }

    @Operation(summary = "Aprobar un pedido de reabastecimiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido aprobado",
                    content = @Content(schema = @Schema(implementation = PedidoReabastecimientoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Pedido no se puede aprobar en su estado actual", content = @Content)
    })
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<EntityModel<PedidoReabastecimientoResponseDTO>> aprobarPedido(
            @Parameter(description = "ID del pedido", example = "1", required = true) @PathVariable Long id) {
        PedidoReabastecimientoResponseDTO response = pedidoService.aprobarPedido(id);
        return ResponseEntity.ok(toModel(response));
    }

    @Operation(summary = "Rechazar un pedido de reabastecimiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido rechazado",
                    content = @Content(schema = @Schema(implementation = PedidoReabastecimientoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    })
    @PutMapping("/{id}/rechazar")
    public ResponseEntity<EntityModel<PedidoReabastecimientoResponseDTO>> rechazarPedido(
            @Parameter(description = "ID del pedido", example = "1", required = true) @PathVariable Long id,
            @Parameter(description = "Motivo del rechazo", example = "Stock suficiente", required = true)
            @RequestParam String motivo) {

        PedidoReabastecimientoResponseDTO response = pedidoService.rechazarPedido(id, motivo);
        return ResponseEntity.ok(toModel(response));
    }

    @Operation(summary = "Obtener un pedido de reabastecimiento por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado",
                    content = @Content(schema = @Schema(implementation = PedidoReabastecimientoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PedidoReabastecimientoResponseDTO>> obtenerPedido(
            @Parameter(description = "ID del pedido", example = "1", required = true) @PathVariable Long id) {
        PedidoReabastecimientoResponseDTO response = pedidoService.obtenerPedido(id);
        return ResponseEntity.ok(toModel(response));
    }

    @Operation(summary = "Listar todos los pedidos de reabastecimiento")
    @ApiResponse(responseCode = "200", description = "Listado de pedidos",
            content = @Content(schema = @Schema(implementation = PedidoReabastecimientoResponseDTO.class)))
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PedidoReabastecimientoResponseDTO>>> listarPedidos() {
        List<EntityModel<PedidoReabastecimientoResponseDTO>> pedidos = pedidoService.listarPedidos()
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<PedidoReabastecimientoResponseDTO>> collection = CollectionModel.of(pedidos);
        collection.add(linkTo(methodOn(PedidoReabastecimientoController.class).listarPedidos()).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    private EntityModel<PedidoReabastecimientoResponseDTO> toModel(PedidoReabastecimientoResponseDTO pedido) {
        EntityModel<PedidoReabastecimientoResponseDTO> model = EntityModel.of(pedido);

        model.add(linkTo(methodOn(PedidoReabastecimientoController.class).listarPedidos()).withRel("pedidos-reabastecimiento"));

        if (pedido.getId() != null) {
            model.add(linkTo(methodOn(PedidoReabastecimientoController.class).obtenerPedido(pedido.getId())).withSelfRel());
            model.add(linkTo(methodOn(PedidoReabastecimientoController.class).aprobarPedido(pedido.getId())).withRel("aprobar"));
            model.add(linkTo(methodOn(PedidoReabastecimientoController.class).rechazarPedido(pedido.getId(), "motivo")).withRel("rechazar"));
        }

        return model;
    }
}

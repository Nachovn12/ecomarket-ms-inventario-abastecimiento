package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.RecepcionMercanciaRequestDTO;
import com.ecomarket.inventario.dto.RecepcionMercanciaResponseDTO;
import com.ecomarket.inventario.service.RecepcionMercanciaService;
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
@RequestMapping("/api/inventario/recepciones-mercancia")
@Tag(name = "Recepciones de Mercancia", description = "Registro de recepciones fisicas contra pedidos de reabastecimiento")
public class RecepcionMercanciaController {

    @Autowired
    private RecepcionMercanciaService recepcionService;

    @Operation(summary = "Registrar una recepcion de mercancia",
            description = "Asocia la mercancia recibida a un pedido de reabastecimiento e incrementa stock.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recepcion registrada",
                    content = @Content(schema = @Schema(implementation = RecepcionMercanciaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<RecepcionMercanciaResponseDTO>> registrarRecepcion(
            @Valid @RequestBody RecepcionMercanciaRequestDTO dto) {

        RecepcionMercanciaResponseDTO response = recepcionService.registrarRecepcion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(response));
    }

    @Operation(summary = "Listar todas las recepciones de mercancia")
    @ApiResponse(responseCode = "200", description = "Listado de recepciones",
            content = @Content(schema = @Schema(implementation = RecepcionMercanciaResponseDTO.class)))
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<RecepcionMercanciaResponseDTO>>> listarRecepciones() {
        List<EntityModel<RecepcionMercanciaResponseDTO>> recepciones = recepcionService.listarRecepciones()
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<RecepcionMercanciaResponseDTO>> collection = CollectionModel.of(recepciones);
        collection.add(linkTo(methodOn(RecepcionMercanciaController.class).listarRecepciones()).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Obtener recepciones de un pedido de reabastecimiento")
    @ApiResponse(responseCode = "200", description = "Recepciones del pedido",
            content = @Content(schema = @Schema(implementation = RecepcionMercanciaResponseDTO.class)))
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<CollectionModel<EntityModel<RecepcionMercanciaResponseDTO>>> obtenerPorPedido(
            @Parameter(description = "ID del pedido", example = "1", required = true) @PathVariable Long pedidoId) {

        List<EntityModel<RecepcionMercanciaResponseDTO>> recepciones = recepcionService.obtenerPorPedido(pedidoId)
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<RecepcionMercanciaResponseDTO>> collection = CollectionModel.of(recepciones);
        collection.add(linkTo(methodOn(RecepcionMercanciaController.class).obtenerPorPedido(pedidoId)).withSelfRel());
        collection.add(linkTo(methodOn(RecepcionMercanciaController.class).listarRecepciones()).withRel("recepciones-mercancia"));

        return ResponseEntity.ok(collection);
    }

    private EntityModel<RecepcionMercanciaResponseDTO> toModel(RecepcionMercanciaResponseDTO recepcion) {
        EntityModel<RecepcionMercanciaResponseDTO> model = EntityModel.of(recepcion);

        model.add(linkTo(methodOn(RecepcionMercanciaController.class).listarRecepciones()).withRel("recepciones-mercancia"));

        if (recepcion.getPedidoId() != null) {
            model.add(linkTo(methodOn(RecepcionMercanciaController.class)
                    .obtenerPorPedido(recepcion.getPedidoId())).withRel("recepciones-del-pedido"));
        }

        return model;
    }
}

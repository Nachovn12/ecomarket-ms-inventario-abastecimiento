package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.RecepcionMercanciaRequestDTO;
import com.ecomarket.inventario.dto.RecepcionMercanciaResponseDTO;
import com.ecomarket.inventario.service.RecepcionMercanciaService;
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
public class RecepcionMercanciaController {

    @Autowired
    private RecepcionMercanciaService recepcionService;

    @PostMapping
    public ResponseEntity<EntityModel<RecepcionMercanciaResponseDTO>> registrarRecepcion(
            @Valid @RequestBody RecepcionMercanciaRequestDTO dto) {

        RecepcionMercanciaResponseDTO response = recepcionService.registrarRecepcion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(response));
    }

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

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<CollectionModel<EntityModel<RecepcionMercanciaResponseDTO>>> obtenerPorPedido(
            @PathVariable Long pedidoId) {

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
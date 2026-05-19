package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.PedidoReabastecimientoRequestDTO;
import com.ecomarket.inventario.dto.PedidoReabastecimientoResponseDTO;
import com.ecomarket.inventario.service.PedidoReabastecimientoService;
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
public class PedidoReabastecimientoController {

    @Autowired
    private PedidoReabastecimientoService pedidoService;

    @PostMapping
    public ResponseEntity<EntityModel<PedidoReabastecimientoResponseDTO>> crearPedido(
            @Valid @RequestBody PedidoReabastecimientoRequestDTO dto) {

        PedidoReabastecimientoResponseDTO response = pedidoService.crearPedido(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(response));
    }

    @PutMapping("/{id}/aprobar")
    public ResponseEntity<EntityModel<PedidoReabastecimientoResponseDTO>> aprobarPedido(@PathVariable Long id) {
        PedidoReabastecimientoResponseDTO response = pedidoService.aprobarPedido(id);
        return ResponseEntity.ok(toModel(response));
    }

    @PutMapping("/{id}/rechazar")
    public ResponseEntity<EntityModel<PedidoReabastecimientoResponseDTO>> rechazarPedido(
            @PathVariable Long id,
            @RequestParam String motivo) {

        PedidoReabastecimientoResponseDTO response = pedidoService.rechazarPedido(id, motivo);
        return ResponseEntity.ok(toModel(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PedidoReabastecimientoResponseDTO>> obtenerPedido(@PathVariable Long id) {
        PedidoReabastecimientoResponseDTO response = pedidoService.obtenerPedido(id);
        return ResponseEntity.ok(toModel(response));
    }

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
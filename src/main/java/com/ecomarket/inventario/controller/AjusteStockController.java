package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.AjusteStockRequestDTO;
import com.ecomarket.inventario.dto.AjusteStockResponseDTO;
import com.ecomarket.inventario.service.AjusteStockService;
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
@RequestMapping("/api/inventario/ajustes-stock")
public class AjusteStockController {

    @Autowired
    private AjusteStockService ajusteStockService;

    @PostMapping
    public ResponseEntity<EntityModel<AjusteStockResponseDTO>> ajustarStock(
            @Valid @RequestBody AjusteStockRequestDTO dto) {

        AjusteStockResponseDTO response = ajusteStockService.ajustarStock(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(response));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<CollectionModel<EntityModel<AjusteStockResponseDTO>>> obtenerHistorial(
            @PathVariable Long productoId) {

        List<EntityModel<AjusteStockResponseDTO>> ajustes = ajusteStockService.obtenerHistorialPorProducto(productoId)
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<AjusteStockResponseDTO>> collection = CollectionModel.of(ajustes);
        collection.add(linkTo(methodOn(AjusteStockController.class).obtenerHistorial(productoId)).withSelfRel());
        collection.add(linkTo(methodOn(AjusteStockController.class).listarAjustes()).withRel("ajustes-stock"));

        return ResponseEntity.ok(collection);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<AjusteStockResponseDTO>>> listarAjustes() {
        List<EntityModel<AjusteStockResponseDTO>> ajustes = ajusteStockService.listarAjustes()
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<AjusteStockResponseDTO>> collection = CollectionModel.of(ajustes);
        collection.add(linkTo(methodOn(AjusteStockController.class).listarAjustes()).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    private EntityModel<AjusteStockResponseDTO> toModel(AjusteStockResponseDTO ajuste) {
        EntityModel<AjusteStockResponseDTO> model = EntityModel.of(ajuste);

        model.add(linkTo(methodOn(AjusteStockController.class).listarAjustes()).withRel("ajustes-stock"));

        if (ajuste.getProductoId() != null) {
            model.add(linkTo(methodOn(AjusteStockController.class)
                    .obtenerHistorial(ajuste.getProductoId())).withRel("historial-producto"));
        }

        return model;
    }
}
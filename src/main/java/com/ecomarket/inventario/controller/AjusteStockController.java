package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.AjusteStockRequestDTO;
import com.ecomarket.inventario.dto.AjusteStockResponseDTO;
import com.ecomarket.inventario.service.AjusteStockService;
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
@RequestMapping("/api/inventario/ajustes-stock")
@Tag(name = "Ajustes de Stock", description = "Registro y consulta de ajustes manuales de stock")
public class AjusteStockController {

    @Autowired
    private AjusteStockService ajusteStockService;

    @Operation(summary = "Registrar un ajuste de stock",
            description = "Aplica un ajuste manual (positivo o negativo) al stock de un producto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ajuste registrado",
                    content = @Content(schema = @Schema(implementation = AjusteStockResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<AjusteStockResponseDTO>> ajustarStock(
            @Valid @RequestBody AjusteStockRequestDTO dto) {

        AjusteStockResponseDTO response = ajusteStockService.ajustarStock(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(response));
    }

    @Operation(summary = "Obtener historial de ajustes por producto")
    @ApiResponse(responseCode = "200", description = "Historial encontrado",
            content = @Content(schema = @Schema(implementation = AjusteStockResponseDTO.class)))
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<CollectionModel<EntityModel<AjusteStockResponseDTO>>> obtenerHistorial(
            @Parameter(description = "ID del producto", example = "1", required = true) @PathVariable Long productoId) {

        List<EntityModel<AjusteStockResponseDTO>> ajustes = ajusteStockService.obtenerHistorialPorProducto(productoId)
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<AjusteStockResponseDTO>> collection = CollectionModel.of(ajustes);
        collection.add(linkTo(methodOn(AjusteStockController.class).obtenerHistorial(productoId)).withSelfRel());
        collection.add(linkTo(methodOn(AjusteStockController.class).listarAjustes()).withRel("ajustes-stock"));

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Listar todos los ajustes de stock")
    @ApiResponse(responseCode = "200", description = "Listado de ajustes",
            content = @Content(schema = @Schema(implementation = AjusteStockResponseDTO.class)))
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

package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.ProductoRequestDTO;
import com.ecomarket.inventario.dto.ProductoResponseDTO;
import com.ecomarket.inventario.service.ProductoService;
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
@RequestMapping("/api/inventario/productos")
@Tag(name = "Productos de Inventario", description = "Productos gestionados en inventario (distintos del catalogo comercial)")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Operation(summary = "Agregar un producto al inventario",
            description = "Crea un producto con su SKU unico. Lanza 409 si el SKU ya existe.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado",
                    content = @Content(schema = @Schema(implementation = ProductoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "SKU duplicado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<ProductoResponseDTO>> agregarProducto(@Valid @RequestBody ProductoRequestDTO dto) {
        ProductoResponseDTO response = productoService.agregarProducto(dto);
        EntityModel<ProductoResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(ProductoController.class).obtenerProducto(response.getId())).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("productos"));
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @Operation(summary = "Listar todos los productos del inventario")
    @ApiResponse(responseCode = "200", description = "Listado de productos",
            content = @Content(schema = @Schema(implementation = ProductoResponseDTO.class)))
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ProductoResponseDTO>>> listarProductos() {
        List<EntityModel<ProductoResponseDTO>> productos = productoService.listarProductos()
                .stream()
                .map(p -> EntityModel.of(p,
                        linkTo(methodOn(ProductoController.class).obtenerProducto(p.getId())).withSelfRel()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(productos,
                linkTo(methodOn(ProductoController.class).listarProductos()).withSelfRel()));
    }

    @Operation(summary = "Obtener un producto de inventario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(schema = @Schema(implementation = ProductoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProductoResponseDTO>> obtenerProducto(
            @Parameter(description = "ID del producto", example = "1", required = true) @PathVariable Long id) {
        ProductoResponseDTO response = productoService.obtenerProducto(id);
        EntityModel<ProductoResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(ProductoController.class).obtenerProducto(id)).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("productos"));
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Obtener un producto por su SKU")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(schema = @Schema(implementation = ProductoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @GetMapping("/sku/{sku}")
    public ResponseEntity<EntityModel<ProductoResponseDTO>> obtenerPorSku(
            @Parameter(description = "SKU del producto", example = "ECO-001", required = true) @PathVariable String sku) {
        ProductoResponseDTO response = productoService.obtenerPorSku(sku);
        EntityModel<ProductoResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(ProductoController.class).obtenerPorSku(sku)).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("productos"));
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Buscar productos por nombre")
    @ApiResponse(responseCode = "200", description = "Coincidencias encontradas",
            content = @Content(schema = @Schema(implementation = ProductoResponseDTO.class)))
    @GetMapping("/buscar/nombre")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorNombre(
            @Parameter(description = "Texto a buscar en el nombre", example = "bolsa", required = true)
            @RequestParam String nombre) {
        return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Buscar productos por categoria")
    @ApiResponse(responseCode = "200", description = "Coincidencias encontradas",
            content = @Content(schema = @Schema(implementation = ProductoResponseDTO.class)))
    @GetMapping("/buscar/categoria")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorCategoria(
            @Parameter(description = "Categoria a buscar", example = "Biodegradables", required = true)
            @RequestParam String categoria) {
        return ResponseEntity.ok(productoService.buscarPorCategoria(categoria));
    }

    @Operation(summary = "Buscar productos por sucursal")
    @ApiResponse(responseCode = "200", description = "Coincidencias encontradas",
            content = @Content(schema = @Schema(implementation = ProductoResponseDTO.class)))
    @GetMapping("/buscar/sucursal")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorSucursal(
            @Parameter(description = "Sucursal a buscar", example = "Santiago Centro", required = true)
            @RequestParam String sucursal) {
        return ResponseEntity.ok(productoService.buscarPorSucursal(sucursal));
    }

    @Operation(summary = "Actualizar un producto de inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado",
                    content = @Content(schema = @Schema(implementation = ProductoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflicto con SKU", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ProductoResponseDTO>> actualizarProducto(
            @Parameter(description = "ID del producto", example = "1", required = true) @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO dto) {
        ProductoResponseDTO response = productoService.actualizarProducto(id, dto);
        EntityModel<ProductoResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(ProductoController.class).obtenerProducto(id)).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("productos"));
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Eliminar un producto de inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(
            @Parameter(description = "ID del producto", example = "1", required = true) @PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}

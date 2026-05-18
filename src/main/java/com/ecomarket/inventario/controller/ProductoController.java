package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.ProductoRequestDTO;
import com.ecomarket.inventario.dto.ProductoResponseDTO;
import com.ecomarket.inventario.service.ProductoService;
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
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @PostMapping
    public ResponseEntity<EntityModel<ProductoResponseDTO>> agregarProducto(@Valid @RequestBody ProductoRequestDTO dto) {
        ProductoResponseDTO response = productoService.agregarProducto(dto);
        EntityModel<ProductoResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(ProductoController.class).obtenerProducto(response.getId())).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("productos"));
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProductoResponseDTO>> obtenerProducto(@PathVariable Long id) {
        ProductoResponseDTO response = productoService.obtenerProducto(id);
        EntityModel<ProductoResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(ProductoController.class).obtenerProducto(id)).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("productos"));
        return ResponseEntity.ok(model);
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<EntityModel<ProductoResponseDTO>> obtenerPorSku(@PathVariable String sku) {
        ProductoResponseDTO response = productoService.obtenerPorSku(sku);
        EntityModel<ProductoResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(ProductoController.class).obtenerPorSku(sku)).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("productos"));
        return ResponseEntity.ok(model);
    }

    @GetMapping("/buscar/nombre")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
    }

    @GetMapping("/buscar/categoria")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorCategoria(@RequestParam String categoria) {
        return ResponseEntity.ok(productoService.buscarPorCategoria(categoria));
    }

    @GetMapping("/buscar/sucursal")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorSucursal(@RequestParam String sucursal) {
        return ResponseEntity.ok(productoService.buscarPorSucursal(sucursal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ProductoResponseDTO>> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoRequestDTO dto) {
        ProductoResponseDTO response = productoService.actualizarProducto(id, dto);
        EntityModel<ProductoResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(ProductoController.class).obtenerProducto(id)).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("productos"));
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
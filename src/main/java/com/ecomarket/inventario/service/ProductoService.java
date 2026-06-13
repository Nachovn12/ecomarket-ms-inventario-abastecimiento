package com.ecomarket.inventario.service;

import com.ecomarket.inventario.dto.ProductoRequestDTO;
import com.ecomarket.inventario.dto.ProductoResponseDTO;
import com.ecomarket.inventario.model.Producto;
import com.ecomarket.inventario.exception.RecursoNoEncontradoException;
import com.ecomarket.inventario.exception.SkuDuplicadoException;
import com.ecomarket.inventario.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    @Autowired
    private ProductoRepository productoRepository;

    public ProductoResponseDTO agregarProducto(ProductoRequestDTO dto) {
        log.info("Creando producto: {}", dto.getNombre());
        if (productoRepository.existsBySku(dto.getSku())) {
            throw new SkuDuplicadoException(dto.getSku());
        }
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setSku(dto.getSku());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setCategoria(dto.getCategoria());
        producto.setSucursal(dto.getSucursal());
        return mapToResponse(productoRepository.save(producto));
    }

    public List<ProductoResponseDTO> listarProductos() {
        return productoRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProductoResponseDTO obtenerProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + id));
        return mapToResponse(producto);
    }

    public ProductoResponseDTO obtenerPorSku(String sku) {
        Producto producto = productoRepository.findBySku(sku)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con SKU: " + sku));
        return mapToResponse(producto);
    }

    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO dto) {
        log.info("Actualizando producto id={}, nombre={}", id, dto.getNombre());
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + id));
        if (!producto.getSku().equals(dto.getSku()) && productoRepository.existsBySku(dto.getSku())) {
            throw new SkuDuplicadoException(dto.getSku());
        }
        producto.setNombre(dto.getNombre());
        producto.setSku(dto.getSku());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setCategoria(dto.getCategoria());
        producto.setSucursal(dto.getSucursal());
        return mapToResponse(productoRepository.save(producto));
    }

    public void eliminarProducto(Long id) {
        log.info("Eliminando producto id={}", id);
        if (!productoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
    }

    public List<ProductoResponseDTO> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ProductoResponseDTO> buscarPorCategoria(String categoria) {
        return productoRepository.findByCategoriaIgnoreCase(categoria)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ProductoResponseDTO> buscarPorSucursal(String sucursal) {
        return productoRepository.findBySucursalIgnoreCase(sucursal)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ProductoResponseDTO mapToResponse(Producto producto) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setSku(producto.getSku());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setCategoria(producto.getCategoria());
        dto.setSucursal(producto.getSucursal());
        dto.setDisponibilidad(producto.getStock() > 0 ? "DISPONIBLE" : "SIN STOCK");
        return dto;
    }
}
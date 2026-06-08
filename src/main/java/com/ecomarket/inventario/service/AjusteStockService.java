package com.ecomarket.inventario.service;

import com.ecomarket.inventario.dto.AjusteStockRequestDTO;
import com.ecomarket.inventario.dto.AjusteStockResponseDTO;
import com.ecomarket.inventario.model.AjusteStock;
import com.ecomarket.inventario.model.Producto;
import com.ecomarket.inventario.exception.RecursoNoEncontradoException;
import com.ecomarket.inventario.repository.AjusteStockRepository;
import com.ecomarket.inventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AjusteStockService {

    @Autowired
    private AjusteStockRepository ajusteStockRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // CA1 y CA2: Ajuste de stock con motivo obligatorio
    public AjusteStockResponseDTO ajustarStock(AjusteStockRequestDTO dto) {
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + dto.getProductoId()));

        AjusteStock ajuste = new AjusteStock();
        ajuste.setProducto(producto);
        ajuste.setCantidadAnterior(producto.getStock());
        ajuste.setCantidadNueva(dto.getCantidadNueva());
        ajuste.setMotivo(dto.getMotivo());
        ajuste.setUsuarioResponsable(dto.getUsuarioResponsable());

        producto.setStock(dto.getCantidadNueva());
        productoRepository.save(producto);

        return mapToResponse(ajusteStockRepository.save(ajuste));
    }

    // CA3: Historial de ajustes por producto
    public List<AjusteStockResponseDTO> obtenerHistorialPorProducto(Long productoId) {
        return ajusteStockRepository.findByProductoId(productoId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<AjusteStockResponseDTO> listarAjustes() {
        return ajusteStockRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AjusteStockResponseDTO mapToResponse(AjusteStock ajuste) {
        AjusteStockResponseDTO dto = new AjusteStockResponseDTO();
        dto.setId(ajuste.getId());
        dto.setProductoId(ajuste.getProducto().getId());
        dto.setNombreProducto(ajuste.getProducto().getNombre());
        dto.setCantidadAnterior(ajuste.getCantidadAnterior());
        dto.setCantidadNueva(ajuste.getCantidadNueva());
        dto.setMotivo(ajuste.getMotivo());
        dto.setUsuarioResponsable(ajuste.getUsuarioResponsable());
        dto.setFechaAjuste(ajuste.getFechaAjuste());
        return dto;
    }
}
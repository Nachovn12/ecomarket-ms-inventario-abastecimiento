package com.ecomarket.inventario.service;

import com.ecomarket.inventario.dto.AjusteStockRequestDTO;
import com.ecomarket.inventario.dto.AjusteStockResponseDTO;
import com.ecomarket.inventario.exception.RecursoNoEncontradoException;
import com.ecomarket.inventario.model.AjusteStock;
import com.ecomarket.inventario.model.Producto;
import com.ecomarket.inventario.repository.AjusteStockRepository;
import com.ecomarket.inventario.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de ajuste de stock.
 * Registra cambios de cantidad con motivo, manteniendo historial de ajustes.
 */
@Service
public class AjusteStockService {

    private static final Logger log = LoggerFactory.getLogger(AjusteStockService.class);

    @Autowired
    private AjusteStockRepository ajusteStockRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Transactional
    public AjusteStockResponseDTO ajustarStock(AjusteStockRequestDTO dto) {
        log.info("Ajustando stock. productoId={}, cantidadNueva={}",
                dto.getProductoId(), dto.getCantidadNueva());

        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con id: " + dto.getProductoId()));

        AjusteStock ajuste = new AjusteStock();
        ajuste.setProducto(producto);
        ajuste.setCantidadAnterior(producto.getStock());
        ajuste.setCantidadNueva(dto.getCantidadNueva());
        ajuste.setMotivo(dto.getMotivo());
        ajuste.setUsuarioResponsable(dto.getUsuarioResponsable());

        producto.setStock(dto.getCantidadNueva());
        productoRepository.save(producto);

        AjusteStockResponseDTO response = mapToResponse(ajusteStockRepository.save(ajuste));
        log.info("Stock ajustado correctamente. productoId={}, anterior={}, nuevo={}",
                dto.getProductoId(), ajuste.getCantidadAnterior(), dto.getCantidadNueva());

        if (producto.getStockMinimo() > 0 && dto.getCantidadNueva() < producto.getStockMinimo()) {
            log.warn("ALERTA STOCK MINIMO: productoId={}, nombre={}, stock actual={}, minimo={}. Se sugiere reabastecimiento.",
                    producto.getId(), producto.getNombre(), dto.getCantidadNueva(), producto.getStockMinimo());
        }

        return response;
    }

    @Transactional(readOnly = true)
    public List<AjusteStockResponseDTO> obtenerHistorialPorProducto(Long productoId) {
        log.info("Consultando historial de ajustes. productoId={}", productoId);
        return ajusteStockRepository.findByProductoId(productoId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AjusteStockResponseDTO> listarAjustes() {
        log.info("Listando todos los ajustes de stock");
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

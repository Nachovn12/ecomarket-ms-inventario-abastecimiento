package com.ecomarket.inventario.service;

import com.ecomarket.inventario.dto.InventarioRequestDTO;
import com.ecomarket.inventario.dto.InventarioResponseDTO;
import com.ecomarket.inventario.dto.ProveedorDTO;
import com.ecomarket.inventario.exception.RecursoNoEncontradoException;
import com.ecomarket.inventario.model.Inventario;
import com.ecomarket.inventario.model.Proveedor;
import com.ecomarket.inventario.repository.InventarioRepository;
import com.ecomarket.inventario.repository.ProveedorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de Inventario legado.
 * Gestiona el registro básico de inventario y proveedores.
 */
@Service
public class InventarioService {

    private static final Logger log = LoggerFactory.getLogger(InventarioService.class);

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Transactional
    public InventarioResponseDTO crearInventario(InventarioRequestDTO dto) {
        log.info("Creando registro de inventario para producto: {}", dto.getNombreProducto());

        if (dto.getCantidadMinima() > dto.getCantidadDisponible()) {
            log.warn("Cantidad mínima ({}) mayor a cantidad disponible ({}) para producto: {}",
                    dto.getCantidadMinima(), dto.getCantidadDisponible(), dto.getNombreProducto());
            throw new IllegalArgumentException(
                    "La cantidad mínima no puede ser mayor que la cantidad disponible");
        }

        Inventario inventario = new Inventario();
        inventario.setNombreProducto(dto.getNombreProducto().trim());
        inventario.setCantidadDisponible(dto.getCantidadDisponible());
        inventario.setCantidadMinima(dto.getCantidadMinima());
        inventario.setCategoria(dto.getCategoria().trim());

        Inventario guardado = inventarioRepository.save(inventario);
        log.info("Inventario creado correctamente. id={}, producto={}",
                guardado.getId(), guardado.getNombreProducto());
        return mapToResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> listarInventarios() {
        log.info("Listando todos los registros de inventario");
        return inventarioRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InventarioResponseDTO obtenerInventario(Long id) {
        log.info("Consultando inventario por ID: {}", id);
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado con id: " + id));
        return mapToResponse(inventario);
    }

    @Transactional
    public InventarioResponseDTO actualizarStock(Long id, int cantidad) {
        log.info("Actualizando stock de inventario id={}, nueva cantidad={}", id, cantidad);
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad de stock no puede ser negativa");
        }
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inventario no encontrado con id: " + id));
        inventario.setCantidadDisponible(cantidad);
        log.info("Stock actualizado correctamente. id={}", id);
        return mapToResponse(inventarioRepository.save(inventario));
    }

    @Transactional
    public void eliminarInventario(Long id) {
        log.info("Eliminando inventario id={}", id);
        if (!inventarioRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Inventario no encontrado con id: " + id);
        }
        inventarioRepository.deleteById(id);
        log.info("Inventario eliminado correctamente. id={}", id);
    }

    @Transactional(readOnly = true)
    public List<ProveedorDTO> listarProveedores() {
        log.info("Listando proveedores de inventario");
        return proveedorRepository.findAll().stream()
                .map(this::mapProveedorToDTO)
                .collect(Collectors.toList());
    }

    private InventarioResponseDTO mapToResponse(Inventario inventario) {
        InventarioResponseDTO dto = new InventarioResponseDTO();
        dto.setId(inventario.getId());
        dto.setNombreProducto(inventario.getNombreProducto());
        dto.setCantidadDisponible(inventario.getCantidadDisponible());
        dto.setCantidadMinima(inventario.getCantidadMinima());
        dto.setCategoria(inventario.getCategoria());
        return dto;
    }

    private ProveedorDTO mapProveedorToDTO(Proveedor proveedor) {
        ProveedorDTO dto = new ProveedorDTO();
        dto.setId(proveedor.getId());
        dto.setNombre(proveedor.getNombre());
        dto.setContacto(proveedor.getContacto());
        dto.setEmail(proveedor.getEmail());
        dto.setTelefono(proveedor.getTelefono());
        return dto;
    }
}

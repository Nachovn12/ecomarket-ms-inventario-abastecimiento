package com.ecomarket.inventario.service;

import com.ecomarket.inventario.dto.InventarioRequestDTO;
import com.ecomarket.inventario.dto.InventarioResponseDTO;
import com.ecomarket.inventario.dto.ProveedorDTO;
import com.ecomarket.inventario.model.Inventario;
import com.ecomarket.inventario.model.Proveedor;
import com.ecomarket.inventario.repository.InventarioRepository;
import com.ecomarket.inventario.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    public InventarioResponseDTO crearInventario(InventarioRequestDTO dto) {
        Inventario inventario = new Inventario();
        inventario.setNombreProducto(dto.getNombreProducto());
        inventario.setCantidadDisponible(dto.getCantidadDisponible());
        inventario.setCantidadMinima(dto.getCantidadMinima());
        inventario.setCategoria(dto.getCategoria());
        Inventario guardado = inventarioRepository.save(inventario);
        return mapToResponse(guardado);
    }

    public InventarioResponseDTO obtenerInventario(Long id) {
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));
        return mapToResponse(inventario);
    }

    public InventarioResponseDTO actualizarStock(Long id, int cantidad) {
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));
        inventario.setCantidadDisponible(cantidad);
        return mapToResponse(inventarioRepository.save(inventario));
    }

    public List<ProveedorDTO> listarProveedores() {
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
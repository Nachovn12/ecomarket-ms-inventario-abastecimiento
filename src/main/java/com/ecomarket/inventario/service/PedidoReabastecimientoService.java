package com.ecomarket.inventario.service;

import com.ecomarket.inventario.dto.PedidoReabastecimientoRequestDTO;
import com.ecomarket.inventario.dto.PedidoReabastecimientoResponseDTO;
import com.ecomarket.inventario.entity.PedidoReabastecimiento;
import com.ecomarket.inventario.entity.Producto;
import com.ecomarket.inventario.exception.RecursoNoEncontradoException;
import com.ecomarket.inventario.repository.PedidoReabastecimientoRepository;
import com.ecomarket.inventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoReabastecimientoService {

    @Autowired
    private PedidoReabastecimientoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public PedidoReabastecimientoResponseDTO crearPedido(PedidoReabastecimientoRequestDTO dto) {
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + dto.getProductoId()));

        PedidoReabastecimiento pedido = new PedidoReabastecimiento();
        pedido.setProducto(producto);
        pedido.setCantidad(dto.getCantidad());
        pedido.setCreadoPor(dto.getCreadoPor());

        return mapToResponse(pedidoRepository.save(pedido));
    }

    public PedidoReabastecimientoResponseDTO aprobarPedido(Long id) {
        PedidoReabastecimiento pedido = obtenerEntidad(id);

        if (pedido.getEstado() != PedidoReabastecimiento.Estado.PENDIENTE) {
            throw new IllegalArgumentException("Solo se pueden aprobar pedidos en estado PENDIENTE");
        }

        pedido.setEstado(PedidoReabastecimiento.Estado.APROBADO);
        return mapToResponse(pedidoRepository.save(pedido));
    }

    public PedidoReabastecimientoResponseDTO rechazarPedido(Long id, String motivo) {
        PedidoReabastecimiento pedido = obtenerEntidad(id);

        if (pedido.getEstado() != PedidoReabastecimiento.Estado.PENDIENTE) {
            throw new IllegalArgumentException("Solo se pueden rechazar pedidos en estado PENDIENTE");
        }

        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo de rechazo es obligatorio");
        }

        pedido.setEstado(PedidoReabastecimiento.Estado.RECHAZADO);
        pedido.setMotivoRechazo(motivo);
        return mapToResponse(pedidoRepository.save(pedido));
    }

    public PedidoReabastecimientoResponseDTO obtenerPedido(Long id) {
        return mapToResponse(obtenerEntidad(id));
    }

    public List<PedidoReabastecimientoResponseDTO> listarPedidos() {
        return pedidoRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PedidoReabastecimiento obtenerEntidad(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con id: " + id));
    }

    private PedidoReabastecimientoResponseDTO mapToResponse(PedidoReabastecimiento pedido) {
        PedidoReabastecimientoResponseDTO dto = new PedidoReabastecimientoResponseDTO();
        dto.setId(pedido.getId());
        dto.setProductoId(pedido.getProducto().getId());
        dto.setNombreProducto(pedido.getProducto().getNombre());
        dto.setCantidad(pedido.getCantidad());
        dto.setEstado(pedido.getEstado().name());
        dto.setMotivoRechazo(pedido.getMotivoRechazo());
        dto.setCreadoPor(pedido.getCreadoPor());
        dto.setFechaCreacion(pedido.getFechaCreacion());
        return dto;
    }
}
package com.ecomarket.inventario.service;

import com.ecomarket.inventario.dto.PedidoReabastecimientoRequestDTO;
import com.ecomarket.inventario.dto.PedidoReabastecimientoResponseDTO;
import com.ecomarket.inventario.exception.RecursoNoEncontradoException;
import com.ecomarket.inventario.model.PedidoReabastecimiento;
import com.ecomarket.inventario.model.Producto;
import com.ecomarket.inventario.repository.PedidoReabastecimientoRepository;
import com.ecomarket.inventario.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de Pedidos de Reabastecimiento.
 * Gestiona el ciclo de vida: PENDIENTE → APROBADO / RECHAZADO → RECIBIDO.
 */
@Service
public class PedidoReabastecimientoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoReabastecimientoService.class);

    @Autowired
    private PedidoReabastecimientoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Transactional
    public PedidoReabastecimientoResponseDTO crearPedido(PedidoReabastecimientoRequestDTO dto) {
        log.info("Creando pedido de reabastecimiento. productoId={}, cantidad={}",
                dto.getProductoId(), dto.getCantidad());

        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con id: " + dto.getProductoId()));

        PedidoReabastecimiento pedido = new PedidoReabastecimiento();
        pedido.setProducto(producto);
        pedido.setCantidad(dto.getCantidad());
        pedido.setCreadoPor(dto.getCreadoPor());

        PedidoReabastecimientoResponseDTO response = mapToResponse(pedidoRepository.save(pedido));
        log.info("Pedido de reabastecimiento creado. id={}", response.getId());
        return response;
    }

    @Transactional
    public PedidoReabastecimientoResponseDTO aprobarPedido(Long id) {
        log.info("Aprobando pedido de reabastecimiento. id={}", id);
        PedidoReabastecimiento pedido = obtenerEntidad(id);

        if (pedido.getEstado() != PedidoReabastecimiento.Estado.PENDIENTE) {
            throw new IllegalArgumentException(
                    "Solo se pueden aprobar pedidos en estado PENDIENTE. Estado actual: " + pedido.getEstado());
        }

        pedido.setEstado(PedidoReabastecimiento.Estado.APROBADO);
        log.info("Pedido aprobado correctamente. id={}", id);
        return mapToResponse(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoReabastecimientoResponseDTO rechazarPedido(Long id, String motivo) {
        log.info("Rechazando pedido de reabastecimiento. id={}", id);
        PedidoReabastecimiento pedido = obtenerEntidad(id);

        if (pedido.getEstado() != PedidoReabastecimiento.Estado.PENDIENTE) {
            throw new IllegalArgumentException(
                    "Solo se pueden rechazar pedidos en estado PENDIENTE. Estado actual: " + pedido.getEstado());
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo de rechazo es obligatorio");
        }

        pedido.setEstado(PedidoReabastecimiento.Estado.RECHAZADO);
        pedido.setMotivoRechazo(motivo.trim());
        log.info("Pedido rechazado. id={}, motivo={}", id, motivo);
        return mapToResponse(pedidoRepository.save(pedido));
    }

    @Transactional(readOnly = true)
    public PedidoReabastecimientoResponseDTO obtenerPedido(Long id) {
        log.info("Consultando pedido de reabastecimiento. id={}", id);
        return mapToResponse(obtenerEntidad(id));
    }

    @Transactional(readOnly = true)
    public List<PedidoReabastecimientoResponseDTO> listarPedidos() {
        log.info("Listando pedidos de reabastecimiento");
        return pedidoRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PedidoReabastecimiento obtenerEntidad(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Pedido de reabastecimiento no encontrado con id: " + id));
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

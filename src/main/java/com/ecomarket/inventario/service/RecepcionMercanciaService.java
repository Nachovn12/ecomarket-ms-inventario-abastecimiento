package com.ecomarket.inventario.service;

import com.ecomarket.inventario.dto.RecepcionMercanciaRequestDTO;
import com.ecomarket.inventario.dto.RecepcionMercanciaResponseDTO;
import com.ecomarket.inventario.entity.PedidoReabastecimiento;
import com.ecomarket.inventario.entity.RecepcionMercancia;
import com.ecomarket.inventario.exception.RecursoNoEncontradoException;
import com.ecomarket.inventario.repository.PedidoReabastecimientoRepository;
import com.ecomarket.inventario.repository.ProductoRepository;
import com.ecomarket.inventario.repository.RecepcionMercanciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecepcionMercanciaService {

    @Autowired
    private RecepcionMercanciaRepository recepcionRepository;

    @Autowired
    private PedidoReabastecimientoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public RecepcionMercanciaResponseDTO registrarRecepcion(RecepcionMercanciaRequestDTO dto) {
        PedidoReabastecimiento pedido = pedidoRepository.findById(dto.getPedidoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con id: " + dto.getPedidoId()));

        if (pedido.getEstado() != PedidoReabastecimiento.Estado.APROBADO) {
            throw new IllegalArgumentException("Solo se puede registrar recepción de mercadería para pedidos en estado APROBADO");
        }

        if (dto.getCantidadRecibida() == null || dto.getCantidadRecibida() <= 0) {
            throw new IllegalArgumentException("La cantidad recibida debe ser mayor a cero");
        }

        int cantidadRecibida = dto.getCantidadRecibida();
        int cantidadDanada = dto.getCantidadDanada() != null ? dto.getCantidadDanada() : 0;

        if (cantidadDanada < 0) {
            throw new IllegalArgumentException("La cantidad dañada no puede ser negativa");
        }

        if (cantidadDanada > cantidadRecibida) {
            throw new IllegalArgumentException("La cantidad dañada no puede ser mayor que la cantidad recibida");
        }

        RecepcionMercancia recepcion = new RecepcionMercancia();
        recepcion.setPedido(pedido);
        recepcion.setCantidadRecibida(cantidadRecibida);
        recepcion.setCantidadDanada(cantidadDanada);
        recepcion.setDiferencias(dto.getDiferencias());
        recepcion.setRegistradoPor(dto.getRegistradoPor());

        int cantidadEsperada = pedido.getCantidad();

        if (cantidadDanada > 0) {
            recepcion.setEstado(RecepcionMercancia.EstadoRecepcion.CON_DANOS);
        } else if (cantidadRecibida != cantidadEsperada) {
            recepcion.setEstado(RecepcionMercancia.EstadoRecepcion.CON_DIFERENCIAS);
        } else {
            recepcion.setEstado(RecepcionMercancia.EstadoRecepcion.CONFORME);
        }

        int cantidadAceptada = cantidadRecibida - cantidadDanada;
        if (cantidadAceptada > 0) {
            var producto = pedido.getProducto();
            producto.setStock(producto.getStock() + cantidadAceptada);
            productoRepository.save(producto);
        }

        pedido.setEstado(PedidoReabastecimiento.Estado.RECIBIDO);
        pedidoRepository.save(pedido);

        return mapToResponse(recepcionRepository.save(recepcion));
    }

    public List<RecepcionMercanciaResponseDTO> listarRecepciones() {
        return recepcionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<RecepcionMercanciaResponseDTO> obtenerPorPedido(Long pedidoId) {
        return recepcionRepository.findByPedidoId(pedidoId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private RecepcionMercanciaResponseDTO mapToResponse(RecepcionMercancia recepcion) {
        RecepcionMercanciaResponseDTO dto = new RecepcionMercanciaResponseDTO();
        dto.setId(recepcion.getId());
        dto.setPedidoId(recepcion.getPedido().getId());
        dto.setNombreProducto(recepcion.getPedido().getProducto().getNombre());
        dto.setCantidadRecibida(recepcion.getCantidadRecibida());
        dto.setCantidadDanada(recepcion.getCantidadDanada());
        dto.setDiferencias(recepcion.getDiferencias());
        dto.setEstado(recepcion.getEstado().name());
        dto.setRegistradoPor(recepcion.getRegistradoPor());
        dto.setFechaRecepcion(recepcion.getFechaRecepcion());
        return dto;
    }
}
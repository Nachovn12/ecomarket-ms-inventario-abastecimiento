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

    // CA1, CA2, CA3, CA4
    public RecepcionMercanciaResponseDTO registrarRecepcion(RecepcionMercanciaRequestDTO dto) {
        PedidoReabastecimiento pedido = pedidoRepository.findById(dto.getPedidoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con id: " + dto.getPedidoId()));

        RecepcionMercancia recepcion = new RecepcionMercancia();
        recepcion.setPedido(pedido);
        recepcion.setCantidadRecibida(dto.getCantidadRecibida());
        recepcion.setCantidadDanada(dto.getCantidadDanada() != null ? dto.getCantidadDanada() : 0);
        recepcion.setDiferencias(dto.getDiferencias());
        recepcion.setRegistradoPor(dto.getRegistradoPor());

        // Determinar estado
        int cantidadDanada = recepcion.getCantidadDanada();
        int cantidadEsperada = pedido.getCantidad();
        int cantidadRecibida = dto.getCantidadRecibida();

        if (cantidadDanada > 0) {
            recepcion.setEstado(RecepcionMercancia.EstadoRecepcion.CON_DANOS);
        } else if (cantidadRecibida != cantidadEsperada) {
            recepcion.setEstado(RecepcionMercancia.EstadoRecepcion.CON_DIFERENCIAS);
        } else {
            recepcion.setEstado(RecepcionMercancia.EstadoRecepcion.CONFORME);
        }

        // CA2: Actualizar stock solo con productos en buen estado
        int cantidadAceptada = cantidadRecibida - cantidadDanada;
        if (cantidadAceptada > 0) {
            var producto = pedido.getProducto();
            producto.setStock(producto.getStock() + cantidadAceptada);
            productoRepository.save(producto);
        }

        // CA4: Actualizar pedido a RECIBIDO
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
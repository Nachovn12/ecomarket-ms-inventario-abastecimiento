package com.ecomarket.inventario.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * DTO de salida para exponer datos de un Pedido de Reabastecimiento sin exponer la entidad JPA.
 */
@Getter
@Setter
public class PedidoReabastecimientoResponseDTO {

    private Long id;
    private Long productoId;
    private String nombreProducto;
    private Integer cantidad;
    private String estado;
    private String motivoRechazo;
    private String creadoPor;
    private LocalDateTime fechaCreacion;
}

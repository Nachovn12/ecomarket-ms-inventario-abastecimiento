package com.ecomarket.inventario.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * DTO de salida para exponer datos de una Recepción de Mercancía sin exponer la entidad JPA.
 */
@Getter
@Setter
public class RecepcionMercanciaResponseDTO {

    private Long id;
    private Long pedidoId;
    private String nombreProducto;
    private Integer cantidadRecibida;
    private Integer cantidadDanada;
    private String diferencias;
    private String estado;
    private String registradoPor;
    private LocalDateTime fechaRecepcion;
}

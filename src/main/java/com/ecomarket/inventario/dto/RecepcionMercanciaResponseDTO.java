package com.ecomarket.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * DTO de salida para exponer datos de una recepcion de mercancia sin exponer la entidad JPA.
 */
@Getter
@Setter
@Schema(description = "Datos de salida de una recepcion de mercancia registrada")
public class RecepcionMercanciaResponseDTO {

    @Schema(description = "ID de la recepcion", example = "1")
    private Long id;

    @Schema(description = "ID del pedido asociado", example = "1")
    private Long pedidoId;

    @Schema(description = "Nombre del producto recibido", example = "Bolsa biodegradable mediana")
    private String nombreProducto;

    @Schema(description = "Cantidad recibida en buen estado", example = "95")
    private Integer cantidadRecibida;

    @Schema(description = "Cantidad recibida con dano", example = "5")
    private Integer cantidadDanada;

    @Schema(description = "Observaciones de la recepcion", example = "Faltaron 5 unidades contra lo pedido")
    private String diferencias;

    @Schema(description = "Estado de la recepcion", example = "PROCESADA", allowableValues = {"PENDIENTE", "PROCESADA", "CON_DIFERENCIAS"})
    private String estado;

    @Schema(description = "Usuario que registro la recepcion", example = "recepcionista1")
    private String registradoPor;

    @Schema(description = "Fecha y hora de la recepcion", example = "2026-06-08T14:30:00")
    private LocalDateTime fechaRecepcion;
}

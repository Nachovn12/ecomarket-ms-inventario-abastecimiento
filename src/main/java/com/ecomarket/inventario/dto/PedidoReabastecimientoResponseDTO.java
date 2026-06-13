package com.ecomarket.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * DTO de salida para exponer datos de un pedido de reabastecimiento sin exponer la entidad JPA.
 */
@Getter
@Setter
@Schema(description = "Datos de salida de un pedido de reabastecimiento")
public class PedidoReabastecimientoResponseDTO {

    @Schema(description = "ID del pedido", example = "1")
    private Long id;

    @Schema(description = "ID del producto solicitado", example = "5")
    private Long productoId;

    @Schema(description = "Nombre del producto", example = "Bolsa biodegradable mediana")
    private String nombreProducto;

    @Schema(description = "Cantidad solicitada", example = "100")
    private Integer cantidad;

    @Schema(description = "Estado del pedido", example = "PENDIENTE", allowableValues = {"PENDIENTE", "APROBADO", "RECHAZADO", "EN_TRANSITO", "RECIBIDO"})
    private String estado;

    @Schema(description = "Motivo del rechazo si aplica", example = "Stock suficiente")
    private String motivoRechazo;

    @Schema(description = "Usuario que creo el pedido", example = "jefeBodega")
    private String creadoPor;

    @Schema(description = "Fecha de creacion del pedido", example = "2026-06-01T10:00:00")
    private LocalDateTime fechaCreacion;
}

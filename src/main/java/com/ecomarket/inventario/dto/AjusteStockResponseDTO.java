package com.ecomarket.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * DTO de salida para exponer datos de un ajuste de stock sin exponer la entidad JPA.
 */
@Getter
@Setter
@Schema(description = "Datos de salida de un ajuste de stock registrado")
public class AjusteStockResponseDTO {

    @Schema(description = "ID del ajuste", example = "1")
    private Long id;

    @Schema(description = "ID del producto afectado", example = "5")
    private Long productoId;

    @Schema(description = "Nombre del producto afectado", example = "Bolsa biodegradable mediana")
    private String nombreProducto;

    @Schema(description = "Cantidad en stock antes del ajuste", example = "30")
    private Integer cantidadAnterior;

    @Schema(description = "Cantidad en stock despues del ajuste", example = "50")
    private Integer cantidadNueva;

    @Schema(description = "Motivo del ajuste", example = "Conteo fisico anual")
    private String motivo;

    @Schema(description = "Usuario que registro el ajuste", example = "operador1")
    private String usuarioResponsable;

    @Schema(description = "Fecha y hora del ajuste", example = "2026-06-10T10:30:00")
    private LocalDateTime fechaAjuste;
}

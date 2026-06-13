package com.ecomarket.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de entrada para registrar la recepcion fisica de mercancia.
 * Incluye validaciones bean validation (JSR 380).
 */
@Getter
@Setter
@Schema(description = "Datos para registrar la recepcion fisica de mercancia contra un pedido")
public class RecepcionMercanciaRequestDTO {

    @NotNull(message = "El ID del pedido es obligatorio")
    @Schema(description = "ID del pedido de reabastecimiento", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long pedidoId;

    @NotNull(message = "La cantidad recibida es obligatoria")
    @Min(value = 1, message = "La cantidad recibida debe ser mayor a cero")
    @Schema(description = "Cantidad recibida en buen estado", example = "95", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer cantidadRecibida;

    @Min(value = 0, message = "La cantidad danada no puede ser negativa")
    @Schema(description = "Cantidad recibida con dano", example = "5", minimum = "0")
    private Integer cantidadDanada;

    @Schema(description = "Observaciones sobre diferencias o problemas", example = "Faltaron 5 unidades contra lo pedido")
    private String diferencias;

    @NotBlank(message = "El usuario responsable del registro es obligatorio")
    @Schema(description = "Usuario que registra la recepcion", example = "recepcionista1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String registradoPor;
}

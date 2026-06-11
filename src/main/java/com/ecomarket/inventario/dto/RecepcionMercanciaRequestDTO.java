package com.ecomarket.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de entrada para registrar la recepción física de mercancía.
 * Incluye validaciones Bean Validation (JSR 380).
 */
@Getter
@Setter
public class RecepcionMercanciaRequestDTO {

    @NotNull(message = "El ID del pedido es obligatorio")
    private Long pedidoId;

    @NotNull(message = "La cantidad recibida es obligatoria")
    @Min(value = 1, message = "La cantidad recibida debe ser mayor a cero")
    private Integer cantidadRecibida;

    @Min(value = 0, message = "La cantidad dañada no puede ser negativa")
    private Integer cantidadDanada;

    private String diferencias;

    @NotBlank(message = "El usuario responsable del registro es obligatorio")
    private String registradoPor;
}

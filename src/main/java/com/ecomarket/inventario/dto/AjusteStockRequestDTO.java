package com.ecomarket.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Solicitud para registrar un ajuste manual de stock de un producto")
public class AjusteStockRequestDTO {

    @NotNull(message = "El ID del producto es obligatorio")
    @Schema(description = "ID del producto al que se le ajusta el stock", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productoId;

    @NotNull(message = "La cantidad nueva es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    @Schema(description = "Nueva cantidad en stock", example = "50", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer cantidadNueva;

    @NotBlank(message = "El motivo es obligatorio")
    @Schema(description = "Motivo del ajuste", example = "Conteo fisico anual", requiredMode = Schema.RequiredMode.REQUIRED)
    private String motivo;

    @NotBlank(message = "El usuario responsable es obligatorio")
    @Schema(description = "Usuario que registra el ajuste", example = "operador1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String usuarioResponsable;
}
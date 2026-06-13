package com.ecomarket.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de entrada para crear un pedido de reabastecimiento.
 * Incluye validaciones bean validation (JSR 380).
 */
@Getter
@Setter
@Schema(description = "Datos para crear un pedido de reabastecimiento")
public class PedidoReabastecimientoRequestDTO {

    @NotNull(message = "El ID del producto es obligatorio")
    @Schema(description = "ID del producto a reabastecer", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @Schema(description = "Cantidad solicitada al proveedor", example = "100", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer cantidad;

    @NotBlank(message = "El usuario que crea el pedido es obligatorio")
    @Schema(description = "Usuario que crea el pedido", example = "jefeBodega", requiredMode = Schema.RequiredMode.REQUIRED)
    private String creadoPor;
}

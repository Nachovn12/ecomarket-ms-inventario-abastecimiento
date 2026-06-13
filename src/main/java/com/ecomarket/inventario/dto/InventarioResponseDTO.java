package com.ecomarket.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de salida para exponer datos de inventario sin exponer la entidad JPA.
 */
@Getter
@Setter
@Schema(description = "Datos de salida de un registro de inventario")
public class InventarioResponseDTO {

    @Schema(description = "ID del inventario", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto", example = "Bolsa biodegradable mediana")
    private String nombreProducto;

    @Schema(description = "Cantidad disponible en stock", example = "100")
    private int cantidadDisponible;

    @Schema(description = "Cantidad minima configurada para alertas", example = "10")
    private int cantidadMinima;

    @Schema(description = "Categoria del producto", example = "Biodegradables")
    private String categoria;
}

package com.ecomarket.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de salida para exponer datos de producto de inventario sin exponer la entidad JPA.
 * Incluye campo calculado 'disponibilidad' para respuesta enriquecida.
 */
@Getter
@Setter
@Schema(description = "Datos de salida de un producto de inventario")
public class ProductoResponseDTO {

    @Schema(description = "ID del producto", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto", example = "Bolsa biodegradable mediana")
    private String nombre;

    @Schema(description = "SKU unico", example = "ECO-001")
    private String sku;

    @Schema(description = "Precio unitario en CLP", example = "1990.0")
    private Double precio;

    @Schema(description = "Stock disponible", example = "100")
    private int stock;

    @Schema(description = "Categoria del producto", example = "Biodegradables")
    private String categoria;

    @Schema(description = "Sucursal donde se almacena", example = "Santiago Centro")
    private String sucursal;

    @Schema(description = "Estado calculado de disponibilidad", example = "DISPONIBLE", allowableValues = {"DISPONIBLE", "STOCK_BAJO", "AGOTADO"})
    private String disponibilidad;
}

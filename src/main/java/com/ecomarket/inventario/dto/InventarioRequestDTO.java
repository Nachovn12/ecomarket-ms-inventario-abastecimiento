package com.ecomarket.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para crear o actualizar un registro de inventario.
 * Incluye validaciones bean validation (JSR 380).
 */
@Schema(description = "Datos para crear o actualizar un registro de inventario legado")
public class InventarioRequestDTO {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 200, message = "El nombre del producto no puede superar 200 caracteres")
    @Schema(description = "Nombre del producto", example = "Bolsa biodegradable mediana", maxLength = 200, requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombreProducto;

    @Min(value = 0, message = "La cantidad disponible no puede ser negativa")
    @Schema(description = "Cantidad disponible en stock", example = "100", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private int cantidadDisponible;

    @Min(value = 0, message = "La cantidad minima no puede ser negativa")
    @Schema(description = "Cantidad minima para alertas", example = "10", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private int cantidadMinima;

    @NotBlank(message = "La categoria es obligatoria")
    @Size(max = 100, message = "La categoria no puede superar 100 caracteres")
    @Schema(description = "Categoria del producto", example = "Biodegradables", maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
    private String categoria;

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public int getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(int cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }

    public int getCantidadMinima() { return cantidadMinima; }
    public void setCantidadMinima(int cantidadMinima) { this.cantidadMinima = cantidadMinima; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}

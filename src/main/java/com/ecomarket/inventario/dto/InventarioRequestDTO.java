package com.ecomarket.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para crear o actualizar un registro de inventario.
 * Incluye validaciones Bean Validation (JSR 380).
 */
public class InventarioRequestDTO {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 200, message = "El nombre del producto no puede superar 200 caracteres")
    private String nombreProducto;

    @Min(value = 0, message = "La cantidad disponible no puede ser negativa")
    private int cantidadDisponible;

    @Min(value = 0, message = "La cantidad mínima no puede ser negativa")
    private int cantidadMinima;

    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 100, message = "La categoría no puede superar 100 caracteres")
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

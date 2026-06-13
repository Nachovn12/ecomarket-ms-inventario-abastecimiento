package com.ecomarket.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos para crear o actualizar un producto de inventario")
public class ProductoRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre del producto", example = "Bolsa biodegradable mediana", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotBlank(message = "El SKU es obligatorio")
    @Schema(description = "Codigo SKU unico del producto", example = "ECO-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sku;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Schema(description = "Precio unitario en CLP", example = "1990.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double precio;

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Schema(description = "Stock inicial del producto", example = "100", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private int stock;

    @NotBlank(message = "La categoria es obligatoria")
    @Schema(description = "Categoria del producto", example = "Biodegradables", requiredMode = Schema.RequiredMode.REQUIRED)
    private String categoria;

    @Schema(description = "Sucursal donde se almacena", example = "Santiago Centro")
    private String sucursal;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getSucursal() { return sucursal; }
    public void setSucursal(String sucursal) { this.sucursal = sucursal; }
}

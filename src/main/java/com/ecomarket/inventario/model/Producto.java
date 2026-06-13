package com.ecomarket.inventario.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Getter;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Entidad JPA que representa un producto gestionado por inventario (OWNER: ms-inventario-abastecimiento).
 * Guarda datos de stock, precio y SKU por sucursal. no es la misma entidad que
 * Ms-catalogo.Producto (que es la fuente de verdad del catalogo maestro).
 * La sincronizacion ocurre via REST (ver CatalogoClientService / inventarioclientservice).
 */
@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Entidad JPA que representa un producto gestionado por inventario")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del producto", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre del producto", example = "Bolsa biodegradable mediana", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotBlank(message = "El SKU es obligatorio")
    @Column(unique = true, nullable = false)
    @Schema(description = "Codigo SKU unico", example = "ECO-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sku;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Schema(description = "Precio unitario en CLP", example = "1990.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double precio;

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Schema(description = "Stock disponible", example = "100", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private int stock;

    @NotBlank(message = "La categoria es obligatoria")
    @Schema(description = "Categoria del producto", example = "Biodegradables", requiredMode = Schema.RequiredMode.REQUIRED)
    private String categoria;

    @Schema(description = "Sucursal donde se almacena", example = "Santiago Centro")
    private String sucursal;

    @Min(value = 0, message = "El stock minimo no puede ser negativo")
    @Schema(description = "Stock minimo para alertas de reabastecimiento", example = "10", minimum = "0")
    private int stockMinimo;
}

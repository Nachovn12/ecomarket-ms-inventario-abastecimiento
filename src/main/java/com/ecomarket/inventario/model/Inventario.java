package com.ecomarket.inventario.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

/**
 * Entidad que representa el registro de inventario de un producto.
 * Modelo legado utilizado para seguimiento basico de stock.
 */
@Entity
@Table(name = "inventario")
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Entidad JPA que representa un registro de inventario legado")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del registro de inventario", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre del producto", example = "Bolsa biodegradable mediana", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombreProducto;

    @Schema(description = "Cantidad disponible en stock", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private int cantidadDisponible;

    @Schema(description = "Cantidad minima para alertas", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private int cantidadMinima;

    @Schema(description = "Categoria del producto", example = "Biodegradables", requiredMode = Schema.RequiredMode.REQUIRED)
    private String categoria;
}

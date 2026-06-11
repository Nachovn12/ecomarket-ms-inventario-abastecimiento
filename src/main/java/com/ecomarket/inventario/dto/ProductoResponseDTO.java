package com.ecomarket.inventario.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO de salida para exponer datos de Producto de inventario sin exponer la entidad JPA.
 * Incluye campo calculado 'disponibilidad' para respuesta enriquecida.
 */
@Getter
@Setter
public class ProductoResponseDTO {

    private Long id;
    private String nombre;
    private String sku;
    private Double precio;
    private int stock;
    private String categoria;
    private String sucursal;
    private String disponibilidad;
}

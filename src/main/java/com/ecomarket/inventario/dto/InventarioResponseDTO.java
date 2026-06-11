package com.ecomarket.inventario.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO de salida para exponer datos de Inventario sin exponer la entidad JPA.
 */
@Getter
@Setter
public class InventarioResponseDTO {

    private Long id;
    private String nombreProducto;
    private int cantidadDisponible;
    private int cantidadMinima;
    private String categoria;
}

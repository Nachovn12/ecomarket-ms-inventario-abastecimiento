package com.ecomarket.inventario.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

/**
 * Entidad que representa el registro de inventario de un producto.
 * Modelo legado utilizado para seguimiento básico de stock.
 */
@Entity
@Table(name = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreProducto;
    private int cantidadDisponible;
    private int cantidadMinima;
    private String categoria;
}

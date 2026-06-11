package com.ecomarket.inventario.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

/**
 * Entidad JPA de Proveedor en el contexto de inventario.
 * Representa al proveedor que suministra productos para reabastecimiento.
 */
@Entity
@Table(name = "proveedores")
@Getter
@Setter
@NoArgsConstructor
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 120)
    private String contacto;

    @Column(length = 120)
    private String email;

    @Column(length = 30)
    private String telefono;
}

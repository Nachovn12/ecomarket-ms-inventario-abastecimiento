package com.ecomarket.inventario.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

/**
 * Entidad JPA de proveedor en el contexto de inventario.
 * Representa al proveedor que suministra productos para reabastecimiento.
 */
@Entity
@Table(name = "proveedores")
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Entidad JPA que representa un proveedor de inventario")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del proveedor", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false, length = 120)
    @Schema(description = "Nombre del proveedor", example = "EcoDistribuidora SpA", maxLength = 120, requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @Column(length = 120)
    @Schema(description = "Persona de contacto", example = "Maria Lopez", maxLength = 120)
    private String contacto;

    @Column(length = 120)
    @Schema(description = "Email de contacto", example = "ventas@ecodistribuidora.cl", maxLength = 120)
    private String email;

    @Column(length = 30)
    @Schema(description = "Telefono de contacto", example = "+56 2 2345 6789", maxLength = 30)
    private String telefono;
}

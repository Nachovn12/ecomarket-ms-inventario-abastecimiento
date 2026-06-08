package com.ecomarket.inventario.model;


import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Getter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordenes_reabastecimiento")
@Getter
@Setter
@NoArgsConstructor
public class OrdenReabastecimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int cantidadSolicitada;
    private String estado;
    private LocalDateTime fechaSolicitud;

    @ManyToOne
    @JoinColumn(name = "inventario_id")
    private Inventario inventario;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;
}
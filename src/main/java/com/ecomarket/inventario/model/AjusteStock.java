package com.ecomarket.inventario.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA de Ajuste de Stock.
 * Registra cada cambio manual de cantidad con su motivo y responsable.
 * Las validaciones de entrada se gestionan en AjusteStockRequestDTO.
 */
@Entity
@Table(name = "ajustes_stock")
@Getter
@Setter
@NoArgsConstructor
public class AjusteStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidadAnterior;

    @Column(nullable = false)
    private Integer cantidadNueva;

    @Column(nullable = false, length = 500)
    private String motivo;

    @Column(length = 120)
    private String usuarioResponsable;

    private LocalDateTime fechaAjuste;

    @PrePersist
    public void prePersist() {
        this.fechaAjuste = LocalDateTime.now();
    }
}

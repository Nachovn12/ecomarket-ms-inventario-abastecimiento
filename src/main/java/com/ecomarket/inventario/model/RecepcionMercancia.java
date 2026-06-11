package com.ecomarket.inventario.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA de Recepción de Mercancía.
 * Registra la recepción física de un pedido de reabastecimiento.
 * Las validaciones de entrada se gestionan en RecepcionMercanciaRequestDTO.
 */
@Entity
@Table(name = "recepciones_mercancia")
@Getter
@Setter
@NoArgsConstructor
public class RecepcionMercancia {

    public enum EstadoRecepcion {
        CONFORME, CON_DIFERENCIAS, CON_DANOS
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoReabastecimiento pedido;

    @Column(nullable = false)
    private Integer cantidadRecibida;

    @Column(nullable = false)
    private Integer cantidadDanada = 0;

    @Column(length = 500)
    private String diferencias;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoRecepcion estado;

    @Column(length = 120)
    private String registradoPor;

    private LocalDateTime fechaRecepcion;

    @PrePersist
    public void prePersist() {
        this.fechaRecepcion = LocalDateTime.now();
        if (this.cantidadDanada == null) {
            this.cantidadDanada = 0;
        }
    }
}

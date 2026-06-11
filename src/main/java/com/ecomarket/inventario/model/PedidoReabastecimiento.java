package com.ecomarket.inventario.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA del Pedido de Reabastecimiento.
 * Ciclo de vida: PENDIENTE → APROBADO / RECHAZADO → RECIBIDO.
 * Las validaciones de entrada se gestionan en PedidoReabastecimientoRequestDTO.
 */
@Entity
@Table(name = "pedidos_reabastecimiento")
@Getter
@Setter
@NoArgsConstructor
public class PedidoReabastecimiento {

    public enum Estado {
        PENDIENTE, APROBADO, RECHAZADO, ENVIADO, RECIBIDO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Estado estado = Estado.PENDIENTE;

    @Column(length = 500)
    private String motivoRechazo;

    @Column(length = 120)
    private String creadoPor;

    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = Estado.PENDIENTE;
        }
    }
}

package com.ecomarket.inventario.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA del pedido de reabastecimiento.
 * Ciclo de vida: pendiente -> aprobado / rechazado -> recibido.
 * Las validaciones de entrada se gestionan en pedidoreabastecimientorequestdto.
 */
@Entity
@Table(name = "pedidos_reabastecimiento")
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Entidad JPA que representa un pedido de reabastecimiento a proveedor")
public class PedidoReabastecimiento {

    @Schema(description = "Estados posibles de un pedido de reabastecimiento", allowableValues = {"PENDIENTE", "APROBADO", "RECHAZADO", "ENVIADO", "RECIBIDO"})
    public enum Estado {
        PENDIENTE, APROBADO, RECHAZADO, ENVIADO, RECIBIDO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del pedido", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    @Schema(description = "Producto solicitado")
    private Producto producto;

    @Column(nullable = false)
    @Schema(description = "Cantidad solicitada al proveedor", example = "100", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer cantidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Schema(description = "Estado del pedido", example = "PENDIENTE")
    private Estado estado = Estado.PENDIENTE;

    @Column(length = 500)
    @Schema(description = "Motivo del rechazo si aplica", example = "Stock suficiente", maxLength = 500)
    private String motivoRechazo;

    @Column(length = 120)
    @Schema(description = "Usuario que creo el pedido", example = "jefeBodega")
    private String creadoPor;

    @Schema(description = "Fecha de creacion del pedido", example = "2026-06-01T10:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = Estado.PENDIENTE;
        }
    }
}

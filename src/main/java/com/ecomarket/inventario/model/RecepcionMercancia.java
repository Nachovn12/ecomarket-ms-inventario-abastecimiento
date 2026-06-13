package com.ecomarket.inventario.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA de recepcion de mercancia.
 * Registra la recepcion fisica de un pedido de reabastecimiento.
 * Las validaciones de entrada se gestionan en recepcionmercanciarequestdto.
 */
@Entity
@Table(name = "recepciones_mercancia")
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Entidad JPA que registra la recepcion fisica de un pedido de reabastecimiento")
public class RecepcionMercancia {

    @Schema(description = "Estados posibles de una recepcion", allowableValues = {"CONFORME", "CON_DIFERENCIAS", "CON_DANOS"})
    public enum EstadoRecepcion {
        CONFORME, CON_DIFERENCIAS, CON_DANOS
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID de la recepcion", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @Schema(description = "Pedido de reabastecimiento asociado")
    private PedidoReabastecimiento pedido;

    @Column(nullable = false)
    @Schema(description = "Cantidad recibida en buen estado", example = "95", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer cantidadRecibida;

    @Column(nullable = false)
    @Schema(description = "Cantidad recibida con dano", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer cantidadDanada = 0;

    @Column(length = 500)
    @Schema(description = "Observaciones sobre diferencias", example = "Faltaron 5 unidades contra lo pedido", maxLength = 500)
    private String diferencias;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Schema(description = "Estado de la recepcion", example = "CONFORME", allowableValues = {"CONFORME", "CON_DIFERENCIAS", "CON_DANOS"})
    private EstadoRecepcion estado;

    @Column(length = 120)
    @Schema(description = "Usuario que registro la recepcion", example = "recepcionista1")
    private String registradoPor;

    @Schema(description = "Fecha y hora de la recepcion", example = "2026-06-08T14:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fechaRecepcion;

    @PrePersist
    public void prePersist() {
        this.fechaRecepcion = LocalDateTime.now();
        if (this.cantidadDanada == null) {
            this.cantidadDanada = 0;
        }
    }
}

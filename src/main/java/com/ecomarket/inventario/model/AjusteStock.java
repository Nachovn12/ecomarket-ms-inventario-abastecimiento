package com.ecomarket.inventario.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA de ajuste de stock.
 * Registra cada cambio manual de cantidad con su motivo y responsable.
 * Las validaciones de entrada se gestionan en ajustestockrequestdto.
 */
@Entity
@Table(name = "ajustes_stock")
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Entidad JPA que registra un ajuste manual de stock")
public class AjusteStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del ajuste", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    @Schema(description = "Producto al que se le aplica el ajuste")
    private Producto producto;

    @Column(nullable = false)
    @Schema(description = "Cantidad en stock antes del ajuste", example = "30", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer cantidadAnterior;

    @Column(nullable = false)
    @Schema(description = "Cantidad en stock despues del ajuste", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer cantidadNueva;

    @Column(nullable = false, length = 500)
    @Schema(description = "Motivo del ajuste", example = "Conteo fisico anual", maxLength = 500, requiredMode = Schema.RequiredMode.REQUIRED)
    private String motivo;

    @Column(length = 120)
    @Schema(description = "Usuario responsable del ajuste", example = "operador1")
    private String usuarioResponsable;

    @Schema(description = "Fecha y hora del ajuste", example = "2026-06-10T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fechaAjuste;

    @PrePersist
    public void prePersist() {
        this.fechaAjuste = LocalDateTime.now();
    }
}

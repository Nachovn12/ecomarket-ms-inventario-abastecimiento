package com.ecomarket.inventario.model;


import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Getter;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

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

    @NotNull(message = "El pedido es obligatorio")
    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoReabastecimiento pedido;

    @NotNull(message = "La cantidad recibida es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidadRecibida;

    private Integer cantidadDanada;

    private String diferencias;

    @Enumerated(EnumType.STRING)
    private EstadoRecepcion estado;

    private String registradoPor;

    private LocalDateTime fechaRecepcion;

    @PrePersist
    public void prePersist() {
        this.fechaRecepcion = LocalDateTime.now();
    }
}
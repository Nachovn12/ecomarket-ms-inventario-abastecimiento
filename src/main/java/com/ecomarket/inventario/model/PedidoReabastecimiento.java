package com.ecomarket.inventario.model;


import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Getter;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

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

    @NotNull(message = "El producto es obligatorio")
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    private String motivoRechazo;

    private String creadoPor;

    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = Estado.PENDIENTE;
    }
}
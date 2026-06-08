package com.ecomarket.inventario.model;


import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Getter;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ajustes_stock")
@Getter
@Setter
@NoArgsConstructor
public class AjusteStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El producto es obligatorio")
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @NotNull(message = "La cantidad nueva es obligatoria")
    private Integer cantidadAnterior;

    @NotNull(message = "La cantidad nueva es obligatoria")
    private Integer cantidadNueva;

    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;

    private String usuarioResponsable;

    private LocalDateTime fechaAjuste;

    @PrePersist
    public void prePersist() {
        this.fechaAjuste = LocalDateTime.now();
    }
}
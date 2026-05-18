package com.ecomarket.inventario.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ajustes_stock")
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getCantidadAnterior() { return cantidadAnterior; }
    public void setCantidadAnterior(Integer cantidadAnterior) { this.cantidadAnterior = cantidadAnterior; }

    public Integer getCantidadNueva() { return cantidadNueva; }
    public void setCantidadNueva(Integer cantidadNueva) { this.cantidadNueva = cantidadNueva; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getUsuarioResponsable() { return usuarioResponsable; }
    public void setUsuarioResponsable(String usuarioResponsable) { this.usuarioResponsable = usuarioResponsable; }

    public LocalDateTime getFechaAjuste() { return fechaAjuste; }
    public void setFechaAjuste(LocalDateTime fechaAjuste) { this.fechaAjuste = fechaAjuste; }
}
package com.ecomarket.inventario.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recepciones_mercancia")
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PedidoReabastecimiento getPedido() { return pedido; }
    public void setPedido(PedidoReabastecimiento pedido) { this.pedido = pedido; }

    public Integer getCantidadRecibida() { return cantidadRecibida; }
    public void setCantidadRecibida(Integer cantidadRecibida) { this.cantidadRecibida = cantidadRecibida; }

    public Integer getCantidadDanada() { return cantidadDanada; }
    public void setCantidadDanada(Integer cantidadDanada) { this.cantidadDanada = cantidadDanada; }

    public String getDiferencias() { return diferencias; }
    public void setDiferencias(String diferencias) { this.diferencias = diferencias; }

    public EstadoRecepcion getEstado() { return estado; }
    public void setEstado(EstadoRecepcion estado) { this.estado = estado; }

    public String getRegistradoPor() { return registradoPor; }
    public void setRegistradoPor(String registradoPor) { this.registradoPor = registradoPor; }

    public LocalDateTime getFechaRecepcion() { return fechaRecepcion; }
    public void setFechaRecepcion(LocalDateTime fechaRecepcion) { this.fechaRecepcion = fechaRecepcion; }
}
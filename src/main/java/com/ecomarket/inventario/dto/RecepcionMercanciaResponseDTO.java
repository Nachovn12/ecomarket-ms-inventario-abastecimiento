package com.ecomarket.inventario.dto;

import java.time.LocalDateTime;

public class RecepcionMercanciaResponseDTO {

    private Long id;
    private Long pedidoId;
    private String nombreProducto;
    private Integer cantidadRecibida;
    private Integer cantidadDanada;
    private String diferencias;
    private String estado;
    private String registradoPor;
    private LocalDateTime fechaRecepcion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public Integer getCantidadRecibida() { return cantidadRecibida; }
    public void setCantidadRecibida(Integer cantidadRecibida) { this.cantidadRecibida = cantidadRecibida; }

    public Integer getCantidadDanada() { return cantidadDanada; }
    public void setCantidadDanada(Integer cantidadDanada) { this.cantidadDanada = cantidadDanada; }

    public String getDiferencias() { return diferencias; }
    public void setDiferencias(String diferencias) { this.diferencias = diferencias; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getRegistradoPor() { return registradoPor; }
    public void setRegistradoPor(String registradoPor) { this.registradoPor = registradoPor; }

    public LocalDateTime getFechaRecepcion() { return fechaRecepcion; }
    public void setFechaRecepcion(LocalDateTime fechaRecepcion) { this.fechaRecepcion = fechaRecepcion; }
}
package com.ecomarket.inventario.dto;

import java.time.LocalDateTime;

public class AjusteStockResponseDTO {

    private Long id;
    private Long productoId;
    private String nombreProducto;
    private Integer cantidadAnterior;
    private Integer cantidadNueva;
    private String motivo;
    private String usuarioResponsable;
    private LocalDateTime fechaAjuste;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

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
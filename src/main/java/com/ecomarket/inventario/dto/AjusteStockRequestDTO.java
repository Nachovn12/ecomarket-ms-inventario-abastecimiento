package com.ecomarket.inventario.dto;

import jakarta.validation.constraints.*;

public class AjusteStockRequestDTO {

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La cantidad nueva es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidadNueva;

    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;

    @NotBlank(message = "El usuario responsable es obligatorio")
    private String usuarioResponsable;

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Integer getCantidadNueva() { return cantidadNueva; }
    public void setCantidadNueva(Integer cantidadNueva) { this.cantidadNueva = cantidadNueva; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getUsuarioResponsable() { return usuarioResponsable; }
    public void setUsuarioResponsable(String usuarioResponsable) { this.usuarioResponsable = usuarioResponsable; }
}
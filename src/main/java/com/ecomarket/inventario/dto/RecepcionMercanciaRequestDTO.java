package com.ecomarket.inventario.dto;

import jakarta.validation.constraints.*;

public class RecepcionMercanciaRequestDTO {

    @NotNull(message = "El ID del pedido es obligatorio")
    private Long pedidoId;

    @NotNull(message = "La cantidad recibida es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidadRecibida;

    private Integer cantidadDanada;

    private String diferencias;

    @NotBlank(message = "El usuario es obligatorio")
    private String registradoPor;

    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }

    public Integer getCantidadRecibida() { return cantidadRecibida; }
    public void setCantidadRecibida(Integer cantidadRecibida) { this.cantidadRecibida = cantidadRecibida; }

    public Integer getCantidadDanada() { return cantidadDanada; }
    public void setCantidadDanada(Integer cantidadDanada) { this.cantidadDanada = cantidadDanada; }

    public String getDiferencias() { return diferencias; }
    public void setDiferencias(String diferencias) { this.diferencias = diferencias; }

    public String getRegistradoPor() { return registradoPor; }
    public void setRegistradoPor(String registradoPor) { this.registradoPor = registradoPor; }
}
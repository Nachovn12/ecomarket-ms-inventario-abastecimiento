package com.ecomarket.inventario.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * DTO de salida para exponer datos de un Ajuste de Stock sin exponer la entidad JPA.
 */
@Getter
@Setter
public class AjusteStockResponseDTO {

    private Long id;
    private Long productoId;
    private String nombreProducto;
    private Integer cantidadAnterior;
    private Integer cantidadNueva;
    private String motivo;
    private String usuarioResponsable;
    private LocalDateTime fechaAjuste;
}

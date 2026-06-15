package com.ecomarket.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Datos de un proveedor del sistema")
public class ProveedorDTO {

    @Schema(description = "ID del proveedor", example = "1")
    private Long id;

    @Schema(description = "Nombre del proveedor", example = "EcoDistribuidora SpA")
    private String nombre;

    @Schema(description = "Persona de contacto", example = "Maria Lopez")
    private String contacto;

    @Schema(description = "Email de contacto", example = "ventas@ecodistribuidora.cl")
    private String email;

    @Schema(description = "Telefono de contacto", example = "+56 2 2345 6789")
    private String telefono;
}
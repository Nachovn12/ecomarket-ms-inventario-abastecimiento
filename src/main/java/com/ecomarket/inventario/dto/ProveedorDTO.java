package com.ecomarket.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}

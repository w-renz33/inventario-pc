package com.sistema.inventario.proveedor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProveedorDTO {

    private Long id;

    @NotBlank(message = "el nombre es obligarotio")
    private String nombre;

    @NotBlank(message = "el ruc es obligatorio")
    private String ruc;
    private String telefono;
    private String email;
    private String direccion;
    private String contactoNombre;
    private String contactoTelefono;
    private Boolean activo;
}

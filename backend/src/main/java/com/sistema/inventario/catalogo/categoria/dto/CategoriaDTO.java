package com.sistema.inventario.catalogo.categoria.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoriaDTO {

    private Long id;

    @NotBlank(message = "El nombre de categoria es obligatorio")
    private String nombre;

    private String descripcion;

}

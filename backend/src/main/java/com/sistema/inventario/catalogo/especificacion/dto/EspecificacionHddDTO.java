package com.sistema.inventario.catalogo.especificacion.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class EspecificacionHddDTO {

    private Long productoId;

    @NotNull(message = "La capacidad es obligatoria")
    @Positive(message = "La capacidad debe ser positiva")
    private Integer capacidadGb;

    private String formato;
    private String interfaz;
    private Integer rpm;
    private Integer cacheMb;
    private Integer velocidadTransferenciaMbps;
}
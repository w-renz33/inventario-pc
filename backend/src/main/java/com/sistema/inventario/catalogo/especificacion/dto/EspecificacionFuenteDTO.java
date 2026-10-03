package com.sistema.inventario.catalogo.especificacion.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class EspecificacionFuenteDTO {

    private Long productoId;

    @NotNull(message = "La potencia es obligatoria")
    @Positive(message = "La potencia debe ser positiva")
    private Integer potenciaW;

    private String certificacion80Plus;
    private String formato;
    private String modularidad;
    private Integer ventiladorMm;
    private String conectorAtx;
    private String conectorCpu;
    private String conectorPcie;
    private Integer conectorSata;
}
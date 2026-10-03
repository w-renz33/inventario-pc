package com.sistema.inventario.catalogo.especificacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EspecificacionRamDTO {

    private Long productoId;

    @NotNull(message = "La capacidad es obligatoria")
    @Positive(message = "La capacidad debe ser positiva")
    private Integer capacidadGb;

    @NotBlank(message = "El tipo de memoria es obligatorio")
    private String tipoMemoria;

    private Integer frecuenciaMhz;
    private String latencia;
    private BigDecimal voltaje;
    private String formato;
    private Boolean ecc;
}
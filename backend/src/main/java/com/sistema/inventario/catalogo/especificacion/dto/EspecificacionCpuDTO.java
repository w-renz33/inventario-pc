package com.sistema.inventario.catalogo.especificacion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EspecificacionCpuDTO {

    private Long productoId;

    @NotBlank(message = "El socket es obligatorio")
    private String socket;

    private String arquitectura;
    private Integer nucleos;
    private Integer hilos;
    private BigDecimal frecuenciaBaseGhz;
    private BigDecimal frecuenciaBoostGhz;
    private Integer cacheMb;
    private Integer consumoTdpW;
    private Boolean graficaIntegrada;
}
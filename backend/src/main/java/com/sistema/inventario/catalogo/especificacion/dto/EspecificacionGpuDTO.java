package com.sistema.inventario.catalogo.especificacion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EspecificacionGpuDTO {

    private Long productoId;

    @NotBlank(message = "El chip es obligatorio")
    private String chip;

    private String arquitectura;
    private Integer vramGb;
    private String tipoMemoria;
    private Integer busMemoriaBits;
    private Integer frecuenciaBaseMhz;
    private Integer frecuenciaBoostMhz;
    private Integer consumoTdpW;
    private String interfaz;
    private Integer hdmi;
    private Integer displayport;
}
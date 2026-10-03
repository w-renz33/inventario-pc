package com.sistema.inventario.catalogo.especificacion.dto;

import lombok.Data;

@Data
public class EspecificacionGabineteDTO {

    private Long productoId;

    private String formato;
    private String compatibilidadPlaca;
    private Integer longitudGpuMaxMm;
    private Integer alturaDisipadorMaxMm;
    private String fuenteSoportada;
    private Integer ventiladoresIncluidos;
    private Integer ventiladoresMax;
    private Integer radiadorMaxMm;
    private Integer bahias35;
    private Integer bahias25;
    private Integer puertosUsb;
    private Integer puertoAudio;
}

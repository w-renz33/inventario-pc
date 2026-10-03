package com.sistema.inventario.catalogo.especificacion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EspecificacionPlacaMadreDTO {

    private Long productoId;

    @NotBlank(message = "El socket es obligatorio")
    private String socket;

    private String chipset;
    private String formato;
    private String tipoMemoria;
    private Integer slotsRam;
    private Integer memoriaMaxGb;
    private Integer slotsM2;
    private Integer slotsPcie;
    private Integer puertosSata;
    private String puertoLan;
    private Boolean wifi;
    private Boolean bluetooth;
}
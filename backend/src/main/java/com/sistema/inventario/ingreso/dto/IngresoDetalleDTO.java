package com.sistema.inventario.ingreso.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IngresoDetalleDTO {

    private Long id;
    private Long productoId;
    private String productoNombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}

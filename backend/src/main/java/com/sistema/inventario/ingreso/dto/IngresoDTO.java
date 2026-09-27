package com.sistema.inventario.ingreso.dto;

import com.sistema.inventario.common.TipoDocumento;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.sistema.inventario.common.EstadoIngreso;

@Data
public class IngresoDTO {

    private Long id;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private LocalDate fecha;
    private Long proveedorId;
    private String proveedorNombre;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private String observacion;
    private com.sistema.inventario.common.EstadoIngreso estado;
    private List<IngresoDetalleDTO> detalles;
}

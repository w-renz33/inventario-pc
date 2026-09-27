package com.sistema.inventario.movimiento.dto;

import com.sistema.inventario.common.MotivoMovimiento;
import com.sistema.inventario.common.ReferenciaTipo;
import com.sistema.inventario.common.TipoMovimiento;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MovimientoStockDTO {

    private Long id;
    private Long productoId;
    private String productoNombre;
    private TipoMovimiento tipoMovimiento;
    /** Compat: tipo como texto (ENTRADA/SALIDA/AJUSTE). */
    private String tipo;
    private MotivoMovimiento motivo;
    private Integer cantidad;
    private Integer stockAnterior;
    private Integer stockNuevo;
    private BigDecimal costoUnitario;
    private ReferenciaTipo referenciaTipo;
    private Long referenciaId;
    private String usuarioUsername;
    private LocalDateTime fecha;
    private String observacion;
    /** Compat con campo anterior. */
    private String nota;
}

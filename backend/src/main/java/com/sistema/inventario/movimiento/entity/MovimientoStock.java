package com.sistema.inventario.movimiento.entity;

import com.sistema.inventario.common.MotivoMovimiento;
import com.sistema.inventario.common.ReferenciaTipo;
import com.sistema.inventario.common.TipoMovimiento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.sistema.inventario.auth.entity.Usuario;
import com.sistema.inventario.catalogo.producto.entity.Producto;

@Entity
@Table(name = "movimientos_stock",
        indexes = {
                @Index(name = "idx_mov_producto_fecha", columnList = "producto_id, fecha"),
                @Index(name = "idx_mov_fecha", columnList = "fecha"),
                @Index(name = "idx_mov_tipo", columnList = "tipo_movimiento"),
                @Index(name = "idx_mov_motivo", columnList = "motivo")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Check(constraints = "cantidad > 0 AND stock_anterior >= 0 AND stock_nuevo >= 0"
        + " AND ((tipo_movimiento = 'ENTRADA' AND stock_nuevo = stock_anterior + cantidad)"
        + " OR (tipo_movimiento = 'SALIDA' AND stock_nuevo = stock_anterior - cantidad)"
        + " OR (tipo_movimiento = 'AJUSTE'))"
        + " AND ((tipo_movimiento = 'ENTRADA' AND motivo IN ('COMPRA','DEVOLUCION','INVENTARIO_INICIAL'))"
        + " OR (tipo_movimiento = 'SALIDA' AND motivo IN ('VENTA','MERMA','DEVOLUCION'))"
        + " OR (tipo_movimiento = 'AJUSTE' AND motivo IN ('AJUSTE_INVENTARIO')))")
public class MovimientoStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 10)
    private TipoMovimiento tipoMovimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MotivoMovimiento motivo;

    @Column(nullable = false)
    private Integer cantidad; // siempre positiva

    @Column(name = "stock_anterior", nullable = false)
    private Integer stockAnterior;

    @Column(name = "stock_nuevo", nullable = false)
    private Integer stockNuevo;

    @Column(name = "costo_unitario", precision = 10, scale = 2)
    private BigDecimal costoUnitario;

    @Enumerated(EnumType.STRING)
    @Column(name = "referencia_tipo", length = 10)
    private ReferenciaTipo referenciaTipo;

    @Column(name = "referencia_id")
    private Long referenciaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Column(length = 255)
    private String observacion;
}

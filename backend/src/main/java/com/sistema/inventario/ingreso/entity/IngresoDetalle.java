package com.sistema.inventario.ingreso.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;
import com.sistema.inventario.catalogo.producto.entity.Producto;

@Entity
@Table(name = "ingreso_detalle",
        indexes = {
                @Index(name = "idx_detalle_ingreso", columnList = "ingreso_id"),
                @Index(name = "idx_detalle_producto", columnList = "producto_id")
        })
@Check(constraints = "cantidad > 0 AND precio_unitario >= 0 AND subtotal >= 0")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngresoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingreso_id", nullable = false)
    private Ingreso ingreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
}

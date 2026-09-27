package com.sistema.inventario.catalogo.producto.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.sistema.inventario.common.EstadoProducto;
import com.sistema.inventario.common.TipoComponente;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.sistema.inventario.catalogo.categoria.entity.Categoria;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    private String marca;

    private String modelo;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_componente", length = 20)
    private TipoComponente tipoComponente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "varchar(20) default 'ACTIVO'")
    private EstadoProducto estado = EstadoProducto.ACTIVO;

    @Column(name = "precio_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCompra = BigDecimal.ZERO;

    @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @Column(name = "stock_actual", nullable = false)
    private Integer stock;

    @Column(name = "stock_minimo")
    private Integer stockMinimo = 5;

    @Column(name = "codigo_barras")
    private String codigoBarras;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDate fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDate.now();
        fechaActualizacion = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDate.now();
    }
}

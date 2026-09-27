package com.sistema.inventario.ingreso.entity;

import com.sistema.inventario.common.EstadoIngreso;
import com.sistema.inventario.common.TipoDocumento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.sistema.inventario.auth.entity.Usuario;
import com.sistema.inventario.proveedor.entity.Proveedor;

@Entity
@Table(name = "ingresos",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_ingreso_doc",
                columnNames = {"tipo_documento", "numero_documento", "proveedor_id"}),
        indexes = {
                @Index(name = "idx_ingreso_fecha", columnList = "fecha"),
                @Index(name = "idx_ingreso_proveedor", columnList = "proveedor_id")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 15)
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento", nullable = false, length = 30)
    private String numeroDocumento;

    @Column(nullable = false)
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal igv = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(length = 255)
    private String observacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15, columnDefinition = "varchar(15) default 'REGISTRADO'")
    private EstadoIngreso estado = EstadoIngreso.REGISTRADO;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "ingreso", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<IngresoDetalle> detalles = new ArrayList<>();
}

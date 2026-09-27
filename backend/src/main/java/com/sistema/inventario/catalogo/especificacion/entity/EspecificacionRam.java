package com.sistema.inventario.catalogo.especificacion.entity;

import com.sistema.inventario.catalogo.producto.entity.Producto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Entity
@Table(name = "especificaciones_ram")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspecificacionRam {

    @Id
    private Long productoId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "producto_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Producto producto;

    @Column(name = "capacidad_gb", nullable = false)
    private Integer capacidadGb;

    @Column(name = "tipo_memoria", nullable = false, length = 10)
    private String tipoMemoria; // DDR4, DDR5

    @Column(name = "frecuencia_mhz")
    private Integer frecuenciaMhz;

    @Column(length = 10)
    private String latencia; // CL16

    @Column(precision = 4, scale = 2)
    private BigDecimal voltaje;

    @Column(length = 20)
    private String formato; // DIMM, SO-DIMM

    private Boolean ecc = false;
}

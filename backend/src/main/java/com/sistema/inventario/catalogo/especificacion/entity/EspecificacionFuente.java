package com.sistema.inventario.catalogo.especificacion.entity;

import com.sistema.inventario.catalogo.producto.entity.Producto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "especificaciones_fuente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspecificacionFuente {

    @Id
    private Long productoId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "producto_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Producto producto;

    @Column(name = "potencia_w", nullable = false)
    private Integer potenciaW;

    @Column(name = "certificacion_80_plus", length = 30)
    private String certificacion80Plus;

    @Column(length = 20)
    private String formato;

    @Column(length = 30)
    private String modularidad;

    @Column(name = "ventilador_mm")
    private Integer ventiladorMm;

    @Column(name = "conector_atx", length = 20)
    private String conectorAtx;

    @Column(name = "conector_cpu", length = 30)
    private String conectorCpu;

    @Column(name = "conector_pcie", length = 30)
    private String conectorPcie;

    @Column(name = "conector_sata")
    private Integer conectorSata;
}

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
@Table(name = "especificaciones_cpu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspecificacionCpu {

    @Id
    private Long productoId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "producto_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Producto producto;

    @Column(nullable = false, length = 30)
    private String socket;

    @Column(length = 50)
    private String arquitectura;

    private Integer nucleos;
    private Integer hilos;

    @Column(name = "frecuencia_base_ghz", precision = 4, scale = 2)
    private BigDecimal frecuenciaBaseGhz;

    @Column(name = "frecuencia_boost_ghz", precision = 4, scale = 2)
    private BigDecimal frecuenciaBoostGhz;

    @Column(name = "cache_mb")
    private Integer cacheMb;

    @Column(name = "consumo_tdp_w")
    private Integer consumoTdpW;

    @Column(name = "grafica_integrada")
    private Boolean graficaIntegrada = false;
}

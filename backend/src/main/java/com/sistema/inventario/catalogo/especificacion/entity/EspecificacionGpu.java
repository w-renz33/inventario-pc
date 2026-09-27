package com.sistema.inventario.catalogo.especificacion.entity;

import com.sistema.inventario.catalogo.producto.entity.Producto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "especificaciones_gpu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspecificacionGpu {

    @Id
    private Long productoId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "producto_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Producto producto;

    @Column(nullable = false, length = 60)
    private String chip;

    @Column(length = 60)
    private String arquitectura;

    @Column(name = "vram_gb")
    private Integer vramGb;

    @Column(name = "tipo_memoria", length = 20)
    private String tipoMemoria;

    @Column(name = "bus_memoria_bits")
    private Integer busMemoriaBits;

    @Column(name = "frecuencia_base_mhz")
    private Integer frecuenciaBaseMhz;

    @Column(name = "frecuencia_boost_mhz")
    private Integer frecuenciaBoostMhz;

    @Column(name = "consumo_tdp_w")
    private Integer consumoTdpW;

    @Column(length = 30)
    private String interfaz;

    private Integer hdmi = 0;
    private Integer displayport = 0;
}

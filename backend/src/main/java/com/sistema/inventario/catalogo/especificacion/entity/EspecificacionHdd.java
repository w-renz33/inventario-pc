package com.sistema.inventario.catalogo.especificacion.entity;

import com.sistema.inventario.catalogo.producto.entity.Producto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "especificaciones_hdd")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspecificacionHdd {

    @Id
    private Long productoId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "producto_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Producto producto;

    @Column(name = "capacidad_gb", nullable = false)
    private Integer capacidadGb;

    @Column(length = 20)
    private String formato;

    @Column(length = 30)
    private String interfaz;

    private Integer rpm;

    @Column(name = "cache_mb")
    private Integer cacheMb;

    @Column(name = "velocidad_transferencia_mbps")
    private Integer velocidadTransferenciaMbps;
}

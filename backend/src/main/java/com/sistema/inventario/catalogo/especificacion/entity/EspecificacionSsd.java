package com.sistema.inventario.catalogo.especificacion.entity;

import com.sistema.inventario.catalogo.producto.entity.Producto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "especificaciones_ssd")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspecificacionSsd {

    @Id
    private Long productoId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "producto_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Producto producto;

    @Column(name = "capacidad_gb", nullable = false)
    private Integer capacidadGb;

    @Column(length = 30)
    private String formato; // M.2 2280

    @Column(length = 40)
    private String interfaz; // NVMe PCIe 4.0

    @Column(name = "tipo_nand", length = 20)
    private String tipoNand; // TLC, QLC

    @Column(name = "velocidad_lectura_mbps")
    private Integer velocidadLecturaMbps;

    @Column(name = "velocidad_escritura_mbps")
    private Integer velocidadEscrituraMbps;

    private Integer tbw;
}

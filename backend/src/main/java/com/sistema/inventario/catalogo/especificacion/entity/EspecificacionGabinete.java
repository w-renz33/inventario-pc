package com.sistema.inventario.catalogo.especificacion.entity;

import com.sistema.inventario.catalogo.producto.entity.Producto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "especificaciones_gabinete")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspecificacionGabinete {

    @Id
    private Long productoId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "producto_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Producto producto;

    @Column(length = 30)
    private String formato; // Mid Tower

    @Column(name = "compatibilidad_placa", length = 100)
    private String compatibilidadPlaca;

    @Column(name = "longitud_gpu_max_mm")
    private Integer longitudGpuMaxMm;

    @Column(name = "altura_disipador_max_mm")
    private Integer alturaDisipadorMaxMm;

    @Column(name = "fuente_soportada", length = 30)
    private String fuenteSoportada;

    @Column(name = "ventiladores_incluidos")
    private Integer ventiladoresIncluidos;

    @Column(name = "ventiladores_max")
    private Integer ventiladoresMax;

    @Column(name = "radiador_max_mm")
    private Integer radiadorMaxMm;

    @Column(name = "bahias_35")
    private Integer bahias35;

    @Column(name = "bahias_25")
    private Integer bahias25;

    @Column(name = "puertos_usb")
    private Integer puertosUsb;

    @Column(name = "puerto_audio")
    private Integer puertoAudio = 0;
}

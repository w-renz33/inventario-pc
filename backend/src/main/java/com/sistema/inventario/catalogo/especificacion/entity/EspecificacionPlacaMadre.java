package com.sistema.inventario.catalogo.especificacion.entity;

import com.sistema.inventario.catalogo.producto.entity.Producto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "especificaciones_placa_madre")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspecificacionPlacaMadre {

    @Id
    private Long productoId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "producto_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Producto producto;

    @Column(nullable = false, length = 30)
    private String socket;

    @Column(length = 30)
    private String chipset;

    @Column(length = 20)
    private String formato; // ATX, mATX, ITX

    @Column(name = "tipo_memoria", length = 10)
    private String tipoMemoria;

    @Column(name = "slots_ram")
    private Integer slotsRam;

    @Column(name = "memoria_max_gb")
    private Integer memoriaMaxGb;

    @Column(name = "slots_m2")
    private Integer slotsM2;

    @Column(name = "slots_pcie")
    private Integer slotsPcie;

    @Column(name = "puertos_sata")
    private Integer puertosSata;

    @Column(name = "puerto_lan", length = 30)
    private String puertoLan;

    private Boolean wifi = false;
    private Boolean bluetooth = false;
}

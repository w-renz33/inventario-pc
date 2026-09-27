package com.sistema.inventario.proveedor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "proveedores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true, length = 20)
    private String ruc;

    private String telefono;
    private String email;
    private String direccion;

    @Column(name = "contacto_nombre")
    private String contactoNombre;

    @Column(name = "Contacto_telefono")
    private String contactoTelefono;

    private Boolean activo = true;

    @Column(name = "fecha_Registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

}

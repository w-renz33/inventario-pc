package com.sistema.inventario.catalogo.producto.dto;

import lombok.Data;

import java.math.BigDecimal;
import com.sistema.inventario.common.EstadoProducto;
import com.sistema.inventario.common.TipoComponente;

@Data
public class ProductResponseDTO {

    private Long id;

    private String nombre;

    private String descripcion;

    private String categoria;

    private BigDecimal precioVenta;

    private String codigoBarras;

    private String sku;
    private String marca;
    private String modelo;
    private com.sistema.inventario.common.TipoComponente tipoComponente;
    private com.sistema.inventario.common.EstadoProducto estado;
    private BigDecimal precioCompra;
}

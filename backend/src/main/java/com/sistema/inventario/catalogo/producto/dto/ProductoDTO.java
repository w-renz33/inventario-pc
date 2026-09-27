package com.sistema.inventario.catalogo.producto.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import com.sistema.inventario.common.EstadoProducto;
import com.sistema.inventario.common.TipoComponente;

@Data
public class ProductoDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @NotBlank(message = "La categoria es obligatoria")
    private String categoria;

    private Long categoriaId;

    @NotNull(message = "EL precio de venta es obligatorio")
    @Positive(message = "El precio de venta debe ser positivo")
    private BigDecimal precioVenta;

    private BigDecimal precioCompra;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    private Integer stockMinimo;
    private String codigoBarras;

    private String sku;
    private String marca;
    private String modelo;
    private com.sistema.inventario.common.TipoComponente tipoComponente;
    private com.sistema.inventario.common.EstadoProducto estado;
}

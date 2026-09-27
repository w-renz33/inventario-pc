package com.sistema.inventario.catalogo.producto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import com.sistema.inventario.common.EstadoProducto;
import com.sistema.inventario.common.TipoComponente;

@Data
public class ProductoUpdateDTO {

    @Schema(description = "Solo enviar campos que se necesita modificar", required = true)
    private String nombre;

    private String descripcion;

    private String categoria;

    private BigDecimal precioVenta;

    private BigDecimal precioCompra;

    private Integer stock;

    private Integer stockMinimo;

    private String codigoBarras;

    private String sku;
    private String marca;
    private String modelo;
    private com.sistema.inventario.common.TipoComponente tipoComponente;
    private com.sistema.inventario.common.EstadoProducto estado;
}

package com.sistema.inventario.catalogo.producto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import com.sistema.inventario.common.TipoComponente;

@Data
@Schema(description = "Datos para crear un producto")
public class ProductCreateDTO {

    @Schema(description = "Nombre del producto", required = true, example = "Laptop Gamer")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "Descripción del producto", required = true, example = "Tamaño, pulgadas o color etc")
    private String descripcion;

    @Schema(description = "Categoría del producto", required = true, example = "Televisores")
    @NotBlank(message = "La categoria es obligatoria")
    private String categoria;

    @NotNull(message = "EL precio de venta es obligatorio")
    @Positive(message = "El precio de venta debe ser positivo")
    @Schema(description = "Precio de venta del producto", required = true, example = "1299.99")
    private BigDecimal precioVenta;

    @Schema(description = "Precio de compra del producto", example = "999.99")
    private BigDecimal precioCompra;

    private String codigoBarras;

    private String sku;
    private String marca;
    private String modelo;
    private com.sistema.inventario.common.TipoComponente tipoComponente;
}

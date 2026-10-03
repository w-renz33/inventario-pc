package com.sistema.inventario.catalogo.especificacion.service;

import com.sistema.inventario.catalogo.producto.entity.Producto;
import com.sistema.inventario.catalogo.producto.repository.ProductoDao;
import com.sistema.inventario.common.TipoComponente;

public abstract class EspecificacionBaseService<E, D> {

    protected final ProductoDao productoDao;

    protected EspecificacionBaseService(ProductoDao productoDao) {
        this.productoDao = productoDao;
    }

    /**
     * Valida que el producto exista y sea del tipo esperado.
     */
    protected Producto validarProducto(Long productoId, TipoComponente esperado) {
        Producto producto = productoDao.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        if (producto.getTipoComponente() != esperado) {
            throw new RuntimeException(
                    "El producto es de tipo " + producto.getTipoComponente()
                            + ", se esperaba " + esperado);
        }
        return producto;
    }

    protected abstract D toDTO(E entidad);

    protected abstract E toEntity(D dto, Producto producto);



}

package com.sistema.inventario.movimiento.service;

import com.sistema.inventario.movimiento.dto.MovimientoStockDTO;
import com.sistema.inventario.movimiento.entity.MovimientoStock;
import com.sistema.inventario.catalogo.producto.entity.Producto;
import com.sistema.inventario.movimiento.repository.MovimientoStockDao;
import com.sistema.inventario.catalogo.producto.repository.ProductoDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovimientoStockService {

    private final MovimientoStockDao movimientoStockDao;

    private final ProductoDao productoDao;

    public MovimientoStockService(MovimientoStockDao movimientoStockDao,
                                  ProductoDao productoDao) {
        this.movimientoStockDao = movimientoStockDao;
        this.productoDao = productoDao;
    }

    public List<MovimientoStockDTO> findAll() {
        return movimientoStockDao.findAll().stream()
                .map(this::converToDTO)
                .collect(Collectors.toList());

    }

    public List<MovimientoStockDTO> findByProducto(Long productoId) {
        Producto producto = productoDao.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return movimientoStockDao.findByProductoOrderByFechaDesc(producto).stream()
                .map(this::converToDTO)
                .collect(Collectors.toList());

    }

    private MovimientoStockDTO converToDTO(MovimientoStock movimiento) {
        MovimientoStockDTO dto = new MovimientoStockDTO();
        dto.setId(movimiento.getId());
        dto.setProductoId(movimiento.getProducto().getId());
        dto.setProductoNombre(movimiento.getProducto().getNombre());
        dto.setTipoMovimiento(movimiento.getTipoMovimiento());
        dto.setTipo(movimiento.getTipoMovimiento() != null ? movimiento.getTipoMovimiento().name() : null);
        dto.setMotivo(movimiento.getMotivo());
        dto.setCantidad(movimiento.getCantidad());
        dto.setStockAnterior(movimiento.getStockAnterior());
        dto.setStockNuevo(movimiento.getStockNuevo());
        dto.setCostoUnitario(movimiento.getCostoUnitario());
        dto.setReferenciaTipo(movimiento.getReferenciaTipo());
        dto.setReferenciaId(movimiento.getReferenciaId());
        dto.setUsuarioUsername(movimiento.getUsuario() != null ?
                movimiento.getUsuario().getUsername() : "SISTEMA");
        dto.setFecha(movimiento.getFecha());
        dto.setObservacion(movimiento.getObservacion());
        dto.setNota(movimiento.getObservacion());

        return dto;
    }

}

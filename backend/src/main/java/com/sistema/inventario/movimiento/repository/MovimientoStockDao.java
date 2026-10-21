package com.sistema.inventario.movimiento.repository;

import com.sistema.inventario.catalogo.producto.entity.Producto;
import com.sistema.inventario.movimiento.entity.MovimientoStock;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

// Acceso a movimientos de stock con JPQL.
// Las lecturas cargan producto y usuario con JOIN FETCH para evitar N+1.
@Repository
public class MovimientoStockDao {

    @PersistenceContext
    private EntityManager em;

    public MovimientoStock save(MovimientoStock movimiento) {
        if (movimiento.getId() == null) {
            em.persist(movimiento);
            return movimiento;
        }
        return em.merge(movimiento);
    }

    public List<MovimientoStock> findAll() {
        return em.createQuery(
                        "SELECT m FROM MovimientoStock m " +
                                "LEFT JOIN FETCH m.producto " +
                                "LEFT JOIN FETCH m.usuario " +
                                "ORDER BY m.fecha DESC",
                        MovimientoStock.class)
                .getResultList();
    }

    public List<MovimientoStock> findByProductoOrderByFechaDesc(Producto producto) {
        return em.createQuery(
                        "SELECT m FROM MovimientoStock m " +
                                "JOIN FETCH m.producto " +
                                "LEFT JOIN FETCH m.usuario " +
                                "WHERE m.producto = :producto " +
                                "ORDER BY m.fecha DESC",
                        MovimientoStock.class)
                .setParameter("producto", producto)
                .getResultList();
    }

}

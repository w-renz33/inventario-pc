package com.sistema.inventario.movimiento.repository;

import com.sistema.inventario.catalogo.producto.entity.Producto;
import com.sistema.inventario.movimiento.entity.MovimientoStock;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DAO manual con EntityManager (Fase 1: menos magia).
 * Reemplaza a MovimientoStockRepository: cada query es JPQL explicito.
 * El JOIN FETCH evita el N+1 al leer producto/usuario en el DTO.
 */
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

    public List<MovimientoStock> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin) {
        return em.createQuery(
                        "SELECT m FROM MovimientoStock m " +
                                "LEFT JOIN FETCH m.producto " +
                                "WHERE m.fecha BETWEEN :inicio AND :fin " +
                                "ORDER BY m.fecha DESC",
                        MovimientoStock.class)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getResultList();
    }
}

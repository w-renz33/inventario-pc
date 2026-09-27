package com.sistema.inventario.catalogo.producto.repository;

import com.sistema.inventario.catalogo.producto.entity.Producto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * DAO manual con EntityManager (Fase 3: menos magia).
 * Reemplaza a ProductoRepository. Solo se portan los metodos con uso real
 * (findAll, findById, save, deleteById); los finders decorativos sin uso
 * (Containing, ByCategoria, StockLessThanEqual) no se replican para no
 * crear codigo muerto.
 */
@Repository
public class ProductoDao {

    @PersistenceContext
    private EntityManager em;

    public Producto save(Producto producto) {
        if (producto.getId() == null) {
            em.persist(producto);
            return producto;
        }
        return em.merge(producto);
    }

    public Optional<Producto> findById(Long id) {
        return Optional.ofNullable(em.find(Producto.class, id));
    }

    public List<Producto> findPage(int page, int size, Long categoriaId) {
        String jpql = "SELECT p FROM Producto p"
                + (categoriaId != null ? " WHERE p.categoria.id = :categoriaId" : "")
                + " ORDER BY p.id";
        var query = em.createQuery(jpql, Producto.class)
                .setFirstResult(page * size)
                .setMaxResults(size);
        if (categoriaId != null) {
            query.setParameter("categoriaId", categoriaId);
        }
        return query.getResultList();
    }

    public long count(Long categoriaId) {
        String jpql = "SELECT COUNT(p) FROM Producto p"
                + (categoriaId != null ? " WHERE p.categoria.id = :categoriaId" : "");
        var query = em.createQuery(jpql, Long.class);
        if (categoriaId != null) {
            query.setParameter("categoriaId", categoriaId);
        }
        return query.getSingleResult();
    }

    public void deleteById(Long id) {
        Producto producto = em.find(Producto.class, id);
        if (producto != null) {
            em.remove(producto);
        }
    }
}

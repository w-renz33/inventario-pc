package com.sistema.inventario.catalogo.producto.repository;

import com.sistema.inventario.catalogo.producto.entity.Producto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Acceso a productos con EntityManager.
// Incluye las operaciones usadas por el servicio y la búsqueda por nombre o SKU.
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

    public Optional<Producto> findBySku(String sku) {
        return em.createQuery("SELECT p FROM Producto p WHERE p.sku = :sku", Producto.class)
                .setParameter("sku", sku)
                .getResultList()
                .stream()
                .findFirst();
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

    // Busca por nombre o SKU. El SKU exacto se ordena primero para que una
    // consulta por código completo devuelva ese producto en la primera fila.
    public List<Producto> buscarPorNombreOSku(String texto, int limit) {
        String patron = patronLike(texto);
        return em.createQuery(
                        "SELECT p FROM Producto p"
                                + " WHERE LOWER(p.nombre) LIKE :patron ESCAPE '!'"
                                + " OR LOWER(p.sku) LIKE :patron ESCAPE '!'"
                                + " ORDER BY CASE WHEN LOWER(p.sku) = :exacto THEN 0 ELSE 1 END, p.nombre",
                        Producto.class)
                .setParameter("patron", patron)
                .setParameter("exacto", texto)
                .setMaxResults(limit)
                .getResultList();
    }

    private String patronLike(String texto) {
        String escapado = texto
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
        return "%" + escapado + "%";
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

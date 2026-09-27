package com.sistema.inventario.ingreso.repository;

import com.sistema.inventario.common.TipoDocumento;
import com.sistema.inventario.ingreso.entity.Ingreso;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * DAO manual con EntityManager (Fase 3: menos magia).
 * Reemplaza a IngresoRepository: CRUD + la busqueda por documento unico
 * (uk_ingreso_doc) escritos en JPQL explicito.
 * Los detalles se persisten por cascada JPA (CascadeType.ALL en Ingreso),
 * igual que antes: la cascada es JPA, no magia de Spring Data.
 */
@Repository
public class IngresoDao {

    @PersistenceContext
    private EntityManager em;

    public Ingreso save(Ingreso ingreso) {
        if (ingreso.getId() == null) {
            em.persist(ingreso);
            return ingreso;
        }
        return em.merge(ingreso);
    }

    public Optional<Ingreso> findById(Long id) {
        return Optional.ofNullable(em.find(Ingreso.class, id));
    }

    public List<Ingreso> findAll() {
        return em.createQuery("SELECT i FROM Ingreso i ORDER BY i.id DESC", Ingreso.class)
                .getResultList();
    }

    public Optional<Ingreso> findByTipoDocumentoAndNumeroDocumentoAndProveedorId(
            TipoDocumento tipoDocumento, String numeroDocumento, Long proveedorId) {
        List<Ingreso> result = em.createQuery(
                        "SELECT i FROM Ingreso i " +
                                "WHERE i.tipoDocumento = :tipo " +
                                "AND i.numeroDocumento = :numero " +
                                "AND i.proveedor.id = :proveedorId",
                        Ingreso.class)
                .setParameter("tipo", tipoDocumento)
                .setParameter("numero", numeroDocumento)
                .setParameter("proveedorId", proveedorId)
                .getResultList();
        return result.stream().findFirst();
    }
}

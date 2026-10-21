package com.sistema.inventario.auth.repository;

import com.sistema.inventario.auth.entity.RolPermiso;
import com.sistema.inventario.auth.entity.Usuario;
import com.sistema.inventario.auth.entity.UsuarioRol;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Consultas de lectura para autenticación.
// Resuelve usuario, roles y permisos con JOIN FETCH para evitar N+1.
@Repository
public class AuthDao {

    @PersistenceContext
    private EntityManager em;

    public Optional<Usuario> findUsuarioByUsername(String username) {
        List<Usuario> result = em.createQuery(
                        "SELECT u FROM Usuario u WHERE u.username = :username",
                        Usuario.class)
                .setParameter("username", username)
                .getResultList();
        return result.stream().findFirst();
    }

    public List<UsuarioRol> findRolesConRol(Long usuarioId) {
        return em.createQuery(
                        "SELECT ur FROM UsuarioRol ur " +
                                "JOIN FETCH ur.rol " +
                                "WHERE ur.usuario.id = :usuarioId",
                        UsuarioRol.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }

    public List<RolPermiso> findPermisosConPermiso(List<Long> rolIds) {
        if (rolIds == null || rolIds.isEmpty()) {
            return List.of();
        }
        return em.createQuery(
                        "SELECT rp FROM RolPermiso rp " +
                                "JOIN FETCH rp.permiso " +
                                "WHERE rp.rol.id IN :rolIds",
                        RolPermiso.class)
                .setParameter("rolIds", rolIds)
                .getResultList();
    }
}

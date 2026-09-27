package com.sistema.inventario.auth.repository;

import com.sistema.inventario.auth.entity.Rol;
import com.sistema.inventario.auth.entity.RolPermiso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolPermisoRepository extends JpaRepository<RolPermiso, Long> {
    List<RolPermiso> findByRol(Rol rol);
    List<RolPermiso> findByRolId(Long rolId);
    boolean existsByRolIdAndPermisoId(Long rolId, Long permisoId);
}

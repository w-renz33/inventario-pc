package com.sistema.inventario.auth.repository;

import com.sistema.inventario.auth.entity.Usuario;
import com.sistema.inventario.auth.entity.UsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {
    List<UsuarioRol> findByUsuario(Usuario usuario);
    List<UsuarioRol> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioIdAndRolId(Long usuarioId, Long rolId);
}

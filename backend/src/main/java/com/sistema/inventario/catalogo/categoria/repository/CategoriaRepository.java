package com.sistema.inventario.catalogo.categoria.repository;

import com.sistema.inventario.catalogo.categoria.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombre(String nombre);
}

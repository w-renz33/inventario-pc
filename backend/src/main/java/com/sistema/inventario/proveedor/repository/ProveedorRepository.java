package com.sistema.inventario.proveedor.repository;

import com.sistema.inventario.proveedor.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    Optional<Proveedor> findByRuc(String ruc);
    List<Proveedor> findByNombreContainingIgnoreCase(String nombre);
    List<Proveedor> findByActivoTrue();
}

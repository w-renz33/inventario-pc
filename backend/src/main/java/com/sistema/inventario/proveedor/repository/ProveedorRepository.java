package com.sistema.inventario.proveedor.repository;

import com.sistema.inventario.proveedor.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
}

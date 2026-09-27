package com.sistema.inventario.movimiento.controller;

import com.sistema.inventario.movimiento.dto.MovimientoStockDTO;
import com.sistema.inventario.movimiento.service.MovimientoStockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventario/movimientos")
public class MovimientoStockController {

    private final MovimientoStockService movimientoStockService;

    public MovimientoStockController(MovimientoStockService movimientoStockService) {
        this.movimientoStockService = movimientoStockService;
    }

    @GetMapping
    public ResponseEntity<List<MovimientoStockDTO>> getAll() {
        return ResponseEntity.ok(movimientoStockService.findAll());
    }

    @GetMapping("/productos/{productoId}")
    public ResponseEntity<List<MovimientoStockDTO>> getByProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(movimientoStockService.findByProducto(productoId));
    }
}

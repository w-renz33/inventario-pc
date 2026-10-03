package com.sistema.inventario.catalogo.especificacion.controller;

import com.sistema.inventario.catalogo.especificacion.dto.EspecificacionRamDTO;
import com.sistema.inventario.catalogo.especificacion.service.EspecificacionRamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario/productos/{productoId}/especificacion/ram")
public class EspecificacionRamController {

    private final EspecificacionRamService service;

    public EspecificacionRamController(EspecificacionRamService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<EspecificacionRamDTO> get(@PathVariable Long productoId) {
        return ResponseEntity.ok(service.obtener(productoId));
    }

    @PostMapping
    public ResponseEntity<EspecificacionRamDTO> crear(@PathVariable Long productoId,
                                                      @Valid @RequestBody EspecificacionRamDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(productoId, dto));
    }

    @PutMapping
    public ResponseEntity<EspecificacionRamDTO> actualizar(@PathVariable Long productoId,
                                                           @Valid @RequestBody EspecificacionRamDTO dto) {
        return ResponseEntity.ok(service.actualizar(productoId, dto));
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminar(@PathVariable Long productoId) {
        service.eliminar(productoId);
        return ResponseEntity.noContent().build();
    }
}

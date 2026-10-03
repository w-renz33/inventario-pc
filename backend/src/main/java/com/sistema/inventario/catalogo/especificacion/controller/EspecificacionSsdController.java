package com.sistema.inventario.catalogo.especificacion.controller;

import com.sistema.inventario.catalogo.especificacion.dto.EspecificacionSsdDTO;
import com.sistema.inventario.catalogo.especificacion.service.EspecificacionSsdService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario/productos/{productoId}/especificacion/ssd")
public class EspecificacionSsdController {

    private final EspecificacionSsdService service;

    public EspecificacionSsdController(EspecificacionSsdService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<EspecificacionSsdDTO> get(@PathVariable Long productoId) {
        return ResponseEntity.ok(service.obtener(productoId));
    }

    @PostMapping
    public ResponseEntity<EspecificacionSsdDTO> crear(@PathVariable Long productoId,
                                                      @Valid @RequestBody EspecificacionSsdDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(productoId, dto));
    }

    @PutMapping
    public ResponseEntity<EspecificacionSsdDTO> actualizar(@PathVariable Long productoId,
                                                           @Valid @RequestBody EspecificacionSsdDTO dto) {
        return ResponseEntity.ok(service.actualizar(productoId, dto));
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminar(@PathVariable Long productoId) {
        service.eliminar(productoId);
        return ResponseEntity.noContent().build();
    }
}

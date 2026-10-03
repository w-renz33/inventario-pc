package com.sistema.inventario.catalogo.especificacion.controller;

import com.sistema.inventario.catalogo.especificacion.dto.EspecificacionGabineteDTO;
import com.sistema.inventario.catalogo.especificacion.service.EspecificacionGabineteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario/productos/{productoId}/especificacion/gabinete")
public class EspecificacionGabineteController {

    private final EspecificacionGabineteService service;

    public EspecificacionGabineteController(EspecificacionGabineteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<EspecificacionGabineteDTO> get(@PathVariable Long productoId) {
        return ResponseEntity.ok(service.obtener(productoId));
    }

    @PostMapping
    public ResponseEntity<EspecificacionGabineteDTO> crear(@PathVariable Long productoId,
                                                           @Valid @RequestBody EspecificacionGabineteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(productoId, dto));
    }

    @PutMapping
    public ResponseEntity<EspecificacionGabineteDTO> actualizar(@PathVariable Long productoId,
                                                                @Valid @RequestBody EspecificacionGabineteDTO dto) {
        return ResponseEntity.ok(service.actualizar(productoId, dto));
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminar(@PathVariable Long productoId) {
        service.eliminar(productoId);
        return ResponseEntity.noContent().build();
    }
}
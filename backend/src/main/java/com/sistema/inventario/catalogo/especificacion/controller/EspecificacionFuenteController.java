package com.sistema.inventario.catalogo.especificacion.controller;

import com.sistema.inventario.catalogo.especificacion.dto.EspecificacionFuenteDTO;
import com.sistema.inventario.catalogo.especificacion.service.EspecificacionFuenteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario/productos/{productoId}/especificacion/fuente")
public class EspecificacionFuenteController {

    private final EspecificacionFuenteService service;

    public EspecificacionFuenteController(EspecificacionFuenteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<EspecificacionFuenteDTO> get(@PathVariable Long productoId) {
        return ResponseEntity.ok(service.obtener(productoId));
    }

    @PostMapping
    public ResponseEntity<EspecificacionFuenteDTO> crear(@PathVariable Long productoId,
                                                         @Valid @RequestBody EspecificacionFuenteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(productoId, dto));
    }

    @PutMapping
    public ResponseEntity<EspecificacionFuenteDTO> actualizar(@PathVariable Long productoId,
                                                              @Valid @RequestBody EspecificacionFuenteDTO dto) {
        return ResponseEntity.ok(service.actualizar(productoId, dto));
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminar(@PathVariable Long productoId) {
        service.eliminar(productoId);
        return ResponseEntity.noContent().build();
    }
}
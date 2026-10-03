package com.sistema.inventario.catalogo.especificacion.controller;

import com.sistema.inventario.catalogo.especificacion.dto.EspecificacionPlacaMadreDTO;
import com.sistema.inventario.catalogo.especificacion.service.EspecificacionPlacaMadreService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario/productos/{productoId}/especificacion/placa-madre")
public class EspecificacionPlacaMadreController {

    private final EspecificacionPlacaMadreService service;

    public EspecificacionPlacaMadreController(EspecificacionPlacaMadreService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<EspecificacionPlacaMadreDTO> get(@PathVariable Long productoId) {
        return ResponseEntity.ok(service.obtener(productoId));
    }

    @PostMapping
    public ResponseEntity<EspecificacionPlacaMadreDTO> crear(@PathVariable Long productoId,
                                                             @Valid @RequestBody EspecificacionPlacaMadreDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(productoId, dto));
    }

    @PutMapping
    public ResponseEntity<EspecificacionPlacaMadreDTO> actualizar(@PathVariable Long productoId,
                                                                  @Valid @RequestBody EspecificacionPlacaMadreDTO dto) {
        return ResponseEntity.ok(service.actualizar(productoId, dto));
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminar(@PathVariable Long productoId) {
        service.eliminar(productoId);
        return ResponseEntity.noContent().build();
    }
}

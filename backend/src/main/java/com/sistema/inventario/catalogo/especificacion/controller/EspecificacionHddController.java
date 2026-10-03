package com.sistema.inventario.catalogo.especificacion.controller;

import com.sistema.inventario.catalogo.especificacion.dto.EspecificacionHddDTO;
import com.sistema.inventario.catalogo.especificacion.service.EspecificacionHddService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario/productos/{productoId}/especificacion/hdd")
public class EspecificacionHddController {

    private final EspecificacionHddService service;

    public EspecificacionHddController(EspecificacionHddService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<EspecificacionHddDTO> get(@PathVariable Long productoId) {
        return ResponseEntity.ok(service.obtener(productoId));
    }

    @PostMapping
    public ResponseEntity<EspecificacionHddDTO> crear(@PathVariable Long productoId,
                                                      @Valid @RequestBody EspecificacionHddDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(productoId, dto));
    }

    @PutMapping
    public ResponseEntity<EspecificacionHddDTO> actualizar(@PathVariable Long productoId,
                                                           @Valid @RequestBody EspecificacionHddDTO dto) {
        return ResponseEntity.ok(service.actualizar(productoId, dto));
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminar(@PathVariable Long productoId) {
        service.eliminar(productoId);
        return ResponseEntity.noContent().build();
    }
}

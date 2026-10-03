package com.sistema.inventario.catalogo.especificacion.controller;

import com.sistema.inventario.catalogo.especificacion.dto.EspecificacionGpuDTO;
import com.sistema.inventario.catalogo.especificacion.service.EspecificacionGpuService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario/productos/{productoId}/especificacion/gpu")
public class EspecificacionGpuController {

    private final EspecificacionGpuService service;

    public EspecificacionGpuController(EspecificacionGpuService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<EspecificacionGpuDTO> get(@PathVariable Long productoId) {
        return ResponseEntity.ok(service.obtener(productoId));
    }

    @PostMapping
    public ResponseEntity<EspecificacionGpuDTO> crear(@PathVariable Long productoId,
                                                      @Valid @RequestBody EspecificacionGpuDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(productoId, dto));
    }

    @PutMapping
    public ResponseEntity<EspecificacionGpuDTO> actualizar(@PathVariable Long productoId,
                                                           @Valid @RequestBody EspecificacionGpuDTO dto) {
        return ResponseEntity.ok(service.actualizar(productoId, dto));
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminar(@PathVariable Long productoId) {
        service.eliminar(productoId);
        return ResponseEntity.noContent().build();
    }
}

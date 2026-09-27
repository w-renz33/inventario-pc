package com.sistema.inventario.ingreso.controller;

import com.sistema.inventario.ingreso.dto.IngresoDTO;
import com.sistema.inventario.ingreso.service.IngresoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingresos")
public class IngresoController {

    private final IngresoService ingresoService;

    public IngresoController(IngresoService ingresoService) {
        this.ingresoService = ingresoService;
    }

    @GetMapping
    public ResponseEntity<List<IngresoDTO>> getAll() {
        return ResponseEntity.ok(ingresoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngresoDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ingresoService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ingreso.registrar')")
    public ResponseEntity<IngresoDTO> registrar(@Valid @RequestBody IngresoDTO dto) {
        return ResponseEntity.ok(ingresoService.registrar(dto));
    }

    @PostMapping("/{id}/anular")
    @PreAuthorize("hasAuthority('ingreso.registrar')")
    public ResponseEntity<Void> anular(@PathVariable Long id) {
        ingresoService.anular(id);
        return ResponseEntity.ok().build();
    }
}

package com.sistema.inventario.catalogo.categoria.controller;

import com.sistema.inventario.catalogo.categoria.dto.CategoriaDTO;
import com.sistema.inventario.catalogo.categoria.entity.Categoria;
import com.sistema.inventario.catalogo.categoria.repository.CategoriaRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> findAll() {
        List<CategoriaDTO> categorias = categoriaRepository.findAll().stream()
                .map(this::converToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(categorias);
    }

    @PostMapping
    public ResponseEntity<CategoriaDTO> create(@Valid @RequestBody CategoriaDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        categoria = categoriaRepository.save(categoria);

        return ResponseEntity.ok(converToDTO(categoria));
    }

    private CategoriaDTO converToDTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());

        return dto;
    }
}

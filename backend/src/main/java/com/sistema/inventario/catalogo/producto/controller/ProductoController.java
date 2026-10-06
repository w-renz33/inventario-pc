package com.sistema.inventario.catalogo.producto.controller;

import com.sistema.inventario.catalogo.producto.dto.ProductCreateDTO;
import com.sistema.inventario.catalogo.producto.dto.ProductResponseDTO;
import com.sistema.inventario.catalogo.producto.dto.ProductoDTO;
import com.sistema.inventario.catalogo.producto.dto.ProductoUpdateDTO;
import com.sistema.inventario.catalogo.producto.service.ProductoService;
import com.sistema.inventario.common.Pagina;
import com.sistema.inventario.common.Paginacion;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Listado paginado con filtro opcional por categoria.
     * Ejemplo: GET /api/inventario/productos?page=0&size=10&categoriaId=1
     * Respuesta: { content:[...], page:0, size:10, totalElements:25, totalPages:3,
     *              first:true, last:false, hasNext:true, hasPrevious:false }
     * categoriaId ausente o vacio = todas las categorias.
     */
    @GetMapping("/productos")
    public ResponseEntity<Pagina<ProductoDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String categoriaId) {

        return ResponseEntity.ok(productoService.findPaged(
                page, size, Paginacion.parsearCategoriaId(categoriaId)));
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<ProductoDTO> getById(@PathVariable Long id) {

        return ResponseEntity.ok(productoService.findById(id));
    }

    @PostMapping("/productos")
    @PreAuthorize("hasAuthority('producto.crear')")
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductCreateDTO producto) {

        return ResponseEntity.ok(productoService.save(producto));
    }

    @PatchMapping("/productos/{id}")
    @PreAuthorize("hasAuthority('producto.crear')")
    public ResponseEntity<ProductoDTO> update(@PathVariable Long id, @Valid @RequestBody ProductoUpdateDTO producto) {

        return ResponseEntity.ok(productoService.update(id, producto));
    }

    /**
     * Baja logica: el producto pasa a DESCONTINUADO en vez de desaparecer de la
     * tabla. Es lo que permite conservar su historial de kardex, porque
     * movimientos_stock e ingreso_detalle lo referencian con FK restrictiva.
     */
    @DeleteMapping("/productos/{id}")
    @PreAuthorize("hasAnyAuthority('stock.ajustar', 'categoria.gestionar')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/productos/{id}/stock")
    @PreAuthorize("hasAuthority('stock.ajustar')")
    public ResponseEntity<ProductoDTO> ajustarStock(
            @PathVariable Long id,
            @RequestParam int cantidad,
            @RequestParam String motivo) {
        return ResponseEntity.ok(productoService.ajustarStock(id, cantidad, motivo));
    }

}

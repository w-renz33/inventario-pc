package com.sistema.inventario.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Reglas de paginado en un solo lugar, con nombres explicitos.
 * Uso: cada findPaged hace sanear -> DAO (offset/limit) -> Pagina.of(...).
 * Intencionalmente manual (academico): nada de Pageable de Spring Data.
 */
public final class Paginacion {

    public static final int PAGE_DEFAULT = 0;
    public static final int SIZE_MIN = 1;
    public static final int SIZE_MAX = 100;

    private Paginacion() {
    }

    public static int sanearPagina(int page) {
        return Math.max(page, PAGE_DEFAULT);
    }

    public static int sanearTamano(int size) {
        return Math.min(Math.max(size, SIZE_MIN), SIZE_MAX);
    }

    /**
     * categoriaId tolerante: null o vacio significa "todas las categorias".
     * Un valor no numerico devuelve 400 con mensaje claro en vez del
     * error criptico de conversion de Spring.
     */
    public static Long parsearCategoriaId(String categoriaId) {
        if (categoriaId == null || categoriaId.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(categoriaId.trim());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "categoriaId debe ser numérico");
        }
    }
}

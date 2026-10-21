package com.sistema.inventario.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

// Normaliza y valida los parámetros de paginación.
// Cada listado sanea página y tamaño, consulta el DAO con offset/limit y
// construye una Pagina.
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

    // categoriaId: null o vacío significa "todas las categorías".
    // Un valor no numérico responde 400 con un mensaje claro.
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

package com.sistema.inventario.common;

import java.util.List;

// Respuesta paginada reutilizable para los listados.
// Incluye las banderas first, last, hasNext y hasPrevious ya calculadas.
public record Pagina<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious
) {
    public static <T> Pagina<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        boolean first = page == 0;
        boolean last = page >= totalPages - 1;
        boolean hasNext = page < totalPages - 1;
        boolean hasPrevious = page > 0;
        return new Pagina<>(content, page, size, totalElements, totalPages,
                first, last, hasNext, hasPrevious);
    }
}

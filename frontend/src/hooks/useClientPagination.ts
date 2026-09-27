import { useEffect, useMemo, useState } from 'react';

/** Paginado CLIENTE para endpoints que aun devuelven List
 *  (ingresos, movimientos). Misma forma de uso que el de servidor
 *  para migrar sin tocar las paginas cuando el back pagine. */
export function useClientPagination<T>(items: T[], sizeInicial = 10) {
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(sizeInicial);

  const totalPages = Math.max(1, Math.ceil(items.length / size));
  const paginaSegura = Math.min(page, totalPages - 1);

  useEffect(() => {
    setPage(0);
  }, [items.length, size]);

  const content = useMemo(
    () => items.slice(paginaSegura * size, paginaSegura * size + size),
    [items, paginaSegura, size],
  );

  return {
    content,
    page: paginaSegura,
    size,
    totalElements: items.length,
    totalPages,
    first: paginaSegura === 0,
    last: paginaSegura >= totalPages - 1,
    hasNext: paginaSegura < totalPages - 1,
    hasPrevious: paginaSegura > 0,
    setPage,
    setSize,
  };
}

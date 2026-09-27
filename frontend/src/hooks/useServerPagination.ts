import { useCallback, useEffect, useState } from 'react';
import { listarProductos } from '../api/productos';
import type { Pagina, Producto } from '../api/types';

/** Paginado SERVIDOR: la verdad vive en el backend (Pagina<T>).
 *  Se renderiza lo que el servidor ecoa (page/size saneados), no lo pedido. */
interface Opciones {
  readonly sizeInicial?: number;
}

export function useServerPagination(opts: Opciones = {}) {
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(opts.sizeInicial ?? 10);
  const [categoriaId, setCategoriaId] = useState<number | undefined>(undefined);
  const [pagina, setPagina] = useState<Pagina<Producto> | null>(null);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const recargar = useCallback(async () => {
    setCargando(true);
    setError(null);
    try {
      setPagina(await listarProductos({ page, size, categoriaId }));
    } catch {
      setError('No se pudo cargar el catalogo.');
    } finally {
      setCargando(false);
    }
  }, [page, size, categoriaId]);

  useEffect(() => {
    void recargar();
  }, [recargar]);

  /** Cambiar de filtro resetea a la pagina 0 (el back devolveria content:[]). */
  const cambiarCategoria = useCallback((id: number | undefined) => {
    setCategoriaId(id);
    setPage(0);
  }, []);

  const cambiarSize = useCallback((nuevo: number) => {
    setSize(Math.min(Math.max(nuevo, 1), 100));
    setPage(0);
  }, []);

  return { pagina, cargando, error, page, size, categoriaId, setPage, cambiarSize, cambiarCategoria, recargar };
}

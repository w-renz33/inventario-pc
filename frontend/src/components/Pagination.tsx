interface PaginationProps {
  readonly page: number;
  readonly size: number;
  readonly totalElements: number;
  readonly totalPages: number;
  readonly hasNext: boolean;
  readonly hasPrevious: boolean;
  readonly nombreItems?: string;
  readonly onPage: (page: number) => void;
  readonly onSize?: (size: number) => void;
}

/** Paginador visual de Stitch cableado a Pagina<T> del backend:
 *  usa hasNext/hasPrevious ya calculados (sin aritmetica en JSX). */
export function Pagination({
  page, size, totalElements, totalPages, hasNext, hasPrevious,
  nombreItems = 'registros', onPage, onSize,
}: PaginationProps) {
  const desde = totalElements === 0 ? 0 : page * size + 1;
  const hasta = Math.min((page + 1) * size, totalElements);
  const numeros = paginasVisibles(page, totalPages);

  return (
    <div className="flex flex-wrap items-center gap-space-md px-space-sm py-space-sm text-body-md">
      <span className="tabular text-on-surface-variant">
        Mostrando {desde} a {hasta} de {totalElements} {nombreItems}
      </span>
      {onSize && (
        <label className="flex items-center gap-space-xs text-on-surface-variant">
          Filas:
          <select
            value={size}
            onChange={(e) => onSize(Number(e.target.value))}
            className="h-8 rounded border border-outline-variant bg-surface-container-lowest px-space-xs"
          >
            {[10, 20, 50].map((n) => (
              <option key={n} value={n}>{n}</option>
            ))}
          </select>
        </label>
      )}
      <div className="ml-auto flex items-center gap-space-xs">
        <button
          type="button"
          disabled={!hasPrevious}
          onClick={() => onPage(page - 1)}
          className="h-8 rounded border border-outline-variant px-space-sm disabled:opacity-40"
        >
          ‹ Anterior
        </button>
        {numeros.map((n) => (
          <button
            key={n}
            type="button"
            onClick={() => onPage(n)}
            className={`h-8 min-w-8 rounded px-space-sm tabular ${
              n === page ? 'bg-primary-container font-medium text-on-primary' : 'border border-outline-variant'
            }`}
          >
            {n + 1}
          </button>
        ))}
        <button
          type="button"
          disabled={!hasNext}
          onClick={() => onPage(page + 1)}
          className="h-8 rounded border border-outline-variant px-space-sm disabled:opacity-40"
        >
          Siguiente ›
        </button>
      </div>
    </div>
  );
}

/** Ventana de hasta 5 numeros alrededor de la pagina actual. */
function paginasVisibles(page: number, totalPages: number): number[] {
  const inicio = Math.max(0, Math.min(page - 2, totalPages - 5));
  const out: number[] = [];
  for (let i = inicio; i < Math.min(inicio + 5, totalPages); i++) out.push(i);
  return out;
}

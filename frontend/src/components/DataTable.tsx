import type { ReactNode } from 'react';

export type Alineacion = 'left' | 'center' | 'right';

export interface Columna {
  readonly titulo: string;
  readonly alinear?: Alineacion;
  readonly nowrap?: boolean;
}

interface DataTableProps {
  readonly columnas: readonly Columna[];
  readonly filas: readonly (readonly ReactNode[])[];
  readonly vacio?: string;
}

const ALINEAR: Record<Alineacion, string> = {
  left: 'text-left',
  center: 'text-center',
  right: 'text-right',
};

/** Tabla densa del ERP: header 32px uppercase, filas con borde sutil.
 *  La alineacion se define POR COLUMNA y se aplica igual al <th> y al
 *  <td>, asi header y celdas nunca se descuadran. Las columnas
 *  numericas (right) usan cifras tabulares. */
export function DataTable({ columnas, filas, vacio = 'Sin registros.' }: DataTableProps) {
  return (
    <div className="overflow-x-auto rounded border border-outline-variant bg-surface-container-lowest">
      <table className="w-full border-collapse text-left">
        <thead>
          <tr className="h-8 bg-surface-container-low">
            {columnas.map((c) => (
              <th
                key={c.titulo}
                className={`whitespace-nowrap px-space-sm text-label-sm uppercase text-on-surface-variant ${ALINEAR[c.alinear ?? 'left']}`}
              >
                {c.titulo}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {filas.length > 0 ? (
            filas.map((fila, i) => (
              <tr key={i} className="border-t border-outline-variant hover:bg-surface-container-low">
                {fila.map((celda, j) => {
                  const col = columnas[j] ?? { titulo: '' };
                  const al = col.alinear ?? 'left';
                  return (
                    <td
                      key={j}
                      className={`px-space-sm py-space-xs text-body-md ${ALINEAR[al]} ${col.nowrap ? 'whitespace-nowrap' : ''} ${al === 'right' ? 'tabular' : ''}`}
                    >
                      {celda}
                    </td>
                  );
                })}
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan={columnas.length} className="px-space-sm py-space-lg text-center text-body-md text-on-surface-variant">
                {vacio}
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

import type { TipoEspecificacion } from '../api/types-especificaciones';
import { CAMPOS_POR_TIPO } from '../api/especificaciones';

interface EspecificacionDetalleProps {
  readonly tipo: TipoEspecificacion;
  readonly spec: Record<string, unknown>;
}

/** Vista de solo lectura: recorre los campos declarados en CAMPOS_POR_TIPO
 *  y muestra el valor de la spec. Si no hay valor, muestra "—". */
export function EspecificacionDetalle({ tipo, spec }: EspecificacionDetalleProps) {
  const campos = CAMPOS_POR_TIPO[tipo];

  return (
    <dl className="grid grid-cols-1 gap-space-sm text-body-md md:grid-cols-2">
      {campos.map((c) => (
        <div key={c.key}>
          <dt className="text-label-md text-on-surface-variant">{c.etiqueta}</dt>
          <dd>{formatearValor(spec[c.key], c.tipo)}</dd>
        </div>
      ))}
    </dl>
  );
}

function formatearValor(valor: unknown, tipo: string): string {
  if (valor === null || valor === undefined || valor === '') return '—';
  if (tipo === 'boolean') return valor ? 'Si' : 'No';
  return String(valor);
}
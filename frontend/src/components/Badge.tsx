type Tono = 'info' | 'success' | 'warning' | 'danger' | 'neutral';

interface BadgeProps {
  readonly texto: string;
  readonly tono?: Tono;
}

const TONOS: Record<Tono, string> = {
  info: 'bg-secondary-fixed text-on-secondary-fixed border-secondary-fixed-dim',
  success: 'bg-[#ecfdf5] text-[#047857] border-[#a7f3d0]',
  warning: 'bg-[#fffbeb] text-[#b45309] border-[#fde68a]',
  danger: 'bg-[#fef2f2] text-[#b91c1c] border-[#fecaca]',
  neutral: 'bg-surface-container text-on-surface-variant border-outline-variant',
};

/** Token de estado compacto (h 20px, label-sm) segun designMd del proyecto. */
export function Badge({ texto, tono = 'neutral' }: BadgeProps) {
  return (
    <span className={`inline-block h-5 whitespace-nowrap rounded-sm border px-space-xs text-label-sm font-medium ${TONOS[tono]}`}>
      {texto}
    </span>
  );
}

/** Mapeos fijos del dominio a tonos (sin logica en las paginas). */
export function tonoEstadoProducto(estado: string | null): Tono {
  if (estado === 'ACTIVO') return 'info';
  if (estado === 'INACTIVO') return 'neutral';
  return 'warning';
}

export function tonoEstadoIngreso(estado: string | undefined): Tono {
  return estado === 'ANULADO' ? 'danger' : 'info';
}

export function tonoTipoMovimiento(tipo: string): Tono {
  if (tipo === 'ENTRADA') return 'success';
  if (tipo === 'SALIDA') return 'warning';
  return 'neutral';
}

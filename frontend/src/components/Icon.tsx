interface IconProps {
  readonly nombre: 'dashboard' | 'productos' | 'ingresos' | 'kardex' | 'proveedores' | 'salir';
  readonly className?: string;
}

/** Iconos stroke inline estilo Stitch (sin dependencias): 20px, trazo 1.8. */
export function Icon({ nombre, className = 'h-5 w-5' }: IconProps) {
  const comun = {
    fill: 'none',
    stroke: 'currentColor',
    strokeWidth: 1.8,
    strokeLinecap: 'round',
    strokeLinejoin: 'round',
  } as const;
  switch (nombre) {
    case 'dashboard':
      return (
        <svg viewBox="0 0 24 24" className={className} {...comun} aria-hidden="true">
          <rect x="3" y="3" width="7" height="9" rx="1" />
          <rect x="14" y="3" width="7" height="5" rx="1" />
          <rect x="14" y="12" width="7" height="9" rx="1" />
          <rect x="3" y="16" width="7" height="5" rx="1" />
        </svg>
      );
    case 'productos':
      return (
        <svg viewBox="0 0 24 24" className={className} {...comun} aria-hidden="true">
          <path d="M21 8.5v7a2 2 0 0 1-1 1.73l-6 3.5a2 2 0 0 1-2 0l-6-3.5A2 2 0 0 1 5 15.5v-7a2 2 0 0 1 1-1.73l6-3.5a2 2 0 0 1 2 0l6 3.5a2 2 0 0 1 1 1.73Z" />
          <path d="m5.3 7.3 6.7 3.9 6.7-3.9M12 22V11" />
        </svg>
      );
    case 'ingresos':
      return (
        <svg viewBox="0 0 24 24" className={className} {...comun} aria-hidden="true">
          <path d="M12 3v12m0 0 4-4m-4 4-4-4" />
          <path d="M4 17v2a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-2" />
        </svg>
      );
    case 'kardex':
      return (
        <svg viewBox="0 0 24 24" className={className} {...comun} aria-hidden="true">
          <path d="M8 3H7a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h9a2 2 0 0 0 2-2V8l-5-5Z" />
          <path d="M13 3v5h5M9 13h6M9 17h4" />
        </svg>
      );
    case 'proveedores':
      return (
        <svg viewBox="0 0 24 24" className={className} {...comun} aria-hidden="true">
          <path d="M1 8h13v9H1zM14 11h4l4 4v2h-8z" />
          <circle cx="6" cy="19" r="1.6" />
          <circle cx="18" cy="19" r="1.6" />
        </svg>
      );
    case 'salir':
      return (
        <svg viewBox="0 0 24 24" className={className} {...comun} aria-hidden="true">
          <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9" />
        </svg>
      );
  }
}

import { Link, useLocation } from 'react-router-dom';
import { Icon } from './Icon';

interface SidebarProps {
  readonly appNombre?: string;
}

const ITEMS = [
  { to: '/dashboard', etiqueta: 'Dashboard', icono: 'dashboard' },
  { to: '/productos', etiqueta: 'Productos', icono: 'productos' },
  { to: '/ingresos', etiqueta: 'Ingresos', icono: 'ingresos' },
  { to: '/kardex', etiqueta: 'Kardex', icono: 'kardex' },
  { to: '/proveedores', etiqueta: 'Proveedores', icono: 'proveedores' },
] as const;

/** Rail lateral oscuro 240px del diseño Stitch: iconos + barra de
 *  acento 3px en el item activo. */
export function Sidebar({ appNombre = 'SysInventario PC' }: SidebarProps) {
  const { pathname } = useLocation();
  return (
    <aside className="flex w-60 shrink-0 flex-col bg-tertiary text-on-tertiary dark:bg-inverse-surface dark:text-inverse-on-surface">
      <Link to="/" className="px-space-lg pb-space-md pt-space-lg">
        <p className="text-headline-sm font-semibold">{appNombre}</p>
        <p className="text-label-sm uppercase tracking-wider opacity-70">Académico</p>
      </Link>
      <nav className="flex flex-col gap-space-xs px-space-md">
        <p className="px-space-sm text-label-sm uppercase tracking-wider opacity-60">Inventario</p>
        {ITEMS.map((item) => {
          const activo = pathname === item.to || (item.to === '/dashboard' && pathname === '/');
          return (
            <Link
              key={item.to}
              to={item.to}
              className={`relative flex items-center gap-space-sm rounded px-space-sm py-space-sm text-body-md transition-colors ${
                activo ? 'bg-primary-container font-medium' : 'opacity-80 hover:bg-tertiary-container hover:opacity-100'
              }`}
            >
              <span
                aria-hidden="true"
                className={`absolute left-0 top-1/2 h-6 w-[3px] -translate-y-1/2 rounded-full bg-secondary-fixed-dim ${activo ? '' : 'hidden'}`}
              />
              <Icon nombre={item.icono} />
              {item.etiqueta}
            </Link>
          );
        })}
      </nav>
      <div className="mt-auto border-t border-on-tertiary/10 px-space-lg py-space-md">
        <p className="text-body-sm opacity-70">Campus Central ERP</p>
        <p className="text-body-sm opacity-50">v2.4.1-stable</p>
      </div>
    </aside>
  );
}

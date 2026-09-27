import { Link } from 'react-router-dom';
import { Sidebar } from '../components/Sidebar';
import { TopAppBar } from '../components/TopAppBar';
import { DataTable } from '../components/DataTable';
import { Badge, tonoEstadoIngreso } from '../components/Badge';
import { ErrorState } from '../components/States';
import { useDashboard } from '../hooks/useDashboard';

interface DashboardPageProps {
  readonly appNombre?: string;
}

/** Resumen operativo sin graficos: 4 cards + ultimos ingresos +
 *  stock critico + top movidos. Todo derivado en memoria de los
 *  endpoints existentes (ver useDashboard). */
export function DashboardPage({ appNombre = 'Panel general' }: DashboardPageProps) {
  const { resumen, cargando, error } = useDashboard();

  return (
    <div className="flex min-h-screen bg-surface dark:bg-inverse-surface">
      <Sidebar />
      <div className="flex min-w-0 flex-1 flex-col">
        <TopAppBar titulo={appNombre} subtitulo="Inventario / Resumen operativo" />

        <main className="flex flex-col gap-space-md p-margin">
          {error && <ErrorState mensaje={error} />}
          {resumen?.muestraParcial && (
            <p className="rounded border border-outline-variant bg-surface-container-low px-space-md py-space-sm text-body-md text-on-surface-variant">
              Muestra parcial: el catálogo supera 100 productos y el resumen se calcula sobre los primeros 100.
            </p>
          )}
          {cargando || !resumen ? (
            <p className="text-body-md text-on-surface-variant">Cargando resumen…</p>
          ) : (
            <>
              <section className="grid grid-cols-1 gap-space-md md:grid-cols-2 xl:grid-cols-4">
                <KpiCard
                  titulo="Valorizado de almacén"
                  valor={`S/ ${resumen.valorizado.toFixed(2)}`}
                  detalle={`${resumen.totalProductos} productos en catálogo`}
                />
                <KpiCard
                  titulo="Stock crítico"
                  valor={String(resumen.criticos.length)}
                  detalle="productos en mínimo o menos"
                  alerta={resumen.criticos.length > 0}
                  enlace={{ to: '/productos', texto: 'Ver catálogo' }}
                />
                <KpiCard
                  titulo="Ingresos registrados"
                  valor={`${resumen.ingresosDocs} docs`}
                  detalle={`S/ ${resumen.ingresosMonto.toFixed(2)} acumulado · ${resumen.ingresosPorTipo.map((t) => `${t.tipo} ${t.n}`).join(' · ') || '—'}`}
                  enlace={{ to: '/ingresos', texto: 'Ver ingresos' }}
                />
                <KpiCard
                  titulo="Movimientos (30 días)"
                  valor={`+${resumen.entradas30d} / −${resumen.salidas30d}`}
                  detalle="entradas / salidas en unidades"
                  enlace={{ to: '/kardex', texto: 'Ver kardex' }}
                />
              </section>

              <section className="grid grid-cols-1 gap-space-md xl:grid-cols-2">
                <div className="rounded border border-outline-variant bg-surface-container-lowest p-space-md">
                  <div className="mb-space-sm flex items-center justify-between">
                    <h2 className="text-headline-sm">Últimos ingresos</h2>
                    <Link to="/ingresos" className="text-body-md text-secondary hover:underline">Ver todos</Link>
                  </div>
                  <DataTable
                    columnas={[
                      { titulo: 'N° Doc', nowrap: true },
                      { titulo: 'Proveedor' },
                      { titulo: 'Total (S/)', alinear: 'right', nowrap: true },
                      { titulo: 'Estado', alinear: 'center', nowrap: true },
                    ]}
                    filas={resumen.ultimosIngresos.map((i) => [
                      <span className="font-code-tabular text-[0.8125rem] text-secondary">{i.numeroDocumento}</span>,
                      i.proveedorNombre ?? '—',
                      <>S/ {i.total ?? 0}</>,
                      <Badge texto={i.estado ?? '—'} tono={tonoEstadoIngreso(i.estado)} />,
                    ])}
                    vacio="Sin ingresos registrados."
                  />
                </div>

                <div className="rounded border border-outline-variant bg-surface-container-lowest p-space-md">
                  <div className="mb-space-sm flex items-center justify-between">
                    <h2 className="text-headline-sm">Stock crítico</h2>
                    <Link to="/productos" className="text-body-md text-secondary hover:underline">Ver catálogo</Link>
                  </div>
                  <DataTable
                    columnas={[
                      { titulo: 'SKU', nowrap: true },
                      { titulo: 'Producto' },
                      { titulo: 'Stock / Mín', alinear: 'right', nowrap: true },
                    ]}
                    filas={resumen.criticos.slice(0, 5).map((p) => [
                      <span className="font-code-tabular text-[0.8125rem] text-secondary">{p.sku || '—'}</span>,
                      p.nombre,
                      <span className="font-medium text-error">{p.stock} / {p.stockMinimo ?? '—'}</span>,
                    ])}
                    vacio="Sin productos en mínimo. Todo operativo."
                  />
                  {resumen.topMovidos.length > 0 && (
                    <p className="mt-space-sm text-body-sm text-on-surface-variant">
                      Más movidos: {resumen.topMovidos.map((t) => `${t.nombre} (${t.n})`).join(' · ')}
                    </p>
                  )}
                </div>
              </section>
            </>
          )}
        </main>
      </div>
    </div>
  );
}

interface KpiCardProps {
  readonly titulo: string;
  readonly valor: string;
  readonly detalle: string;
  readonly alerta?: boolean;
  readonly enlace?: { to: string; texto: string };
}

function KpiCard({ titulo, valor, detalle, alerta, enlace }: KpiCardProps) {
  return (
    <div className="rounded border border-outline-variant bg-surface-container-lowest p-space-md">
      <p className="text-label-sm uppercase text-on-surface-variant">{titulo}</p>
      <p className={`text-headline-lg tabular ${alerta ? 'text-error' : 'text-primary-container'}`}>{valor}</p>
      <p className="mt-space-xs text-body-sm text-on-surface-variant">{detalle}</p>
      {enlace && (
        <Link to={enlace.to} className="mt-space-xs inline-block text-body-md text-secondary hover:underline">
          {enlace.texto} →
        </Link>
      )}
    </div>
  );
}

import { useEffect, useMemo, useState } from 'react';
import type { ReactNode } from 'react';
import { Sidebar } from '../components/Sidebar';
import { TopAppBar } from '../components/TopAppBar';
import { Button } from '../components/Button';
import { DataTable } from '../components/DataTable';
import { Pagination } from '../components/Pagination';
import { Badge, tonoTipoMovimiento } from '../components/Badge';
import { ErrorState } from '../components/States';
import { useClientPagination } from '../hooks/useClientPagination';
import { listarProductosParaPicker } from '../api/productos';
import * as api from '../api/movimientos';
import { mensajeError } from '../api/client';
import type { Movimiento, Producto, TipoMovimiento } from '../api/types';

const TIPOS: TipoMovimiento[] = ['ENTRADA', 'SALIDA', 'AJUSTE'];

/** Screen 4 Stitch (podada): sin Exportar Excel/PDF ni Imprimir (sin endpoint),
 *  sin ubicación física/estantería (no existe en el dominio) ni tarjetas
 *  informativas (valuación/bloqueo/auditoría). Resumen calculado en memoria
 *  desde los movimientos cargados + stock actual del producto. */
export function KardexPage() {
  const [productos, setProductos] = useState<Producto[]>([]);
  const [modo, setModo] = useState<'producto' | 'todos'>('producto');
  const [productoId, setProductoId] = useState<number | ''>('');
  const [movimientos, setMovimientos] = useState<Movimiento[]>([]);
  const [cargando, setCargando] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Filtros en memoria sobre lo cargado (el back devuelve List por producto)
  const [fTipo, setFTipo] = useState('');
  const [fDesde, setFDesde] = useState('');
  const [fHasta, setFHasta] = useState('');

  useEffect(() => {
    listarProductosParaPicker().then(setProductos).catch(() => setProductos([]));
  }, []);

  useEffect(() => {
    if (modo === 'todos') {
      setCargando(true);
      setError(null);
      api
        .listarMovimientos()
        .then(setMovimientos)
        .catch((err) => setError(mensajeError(err, 'No se pudo cargar el kardex.')))
        .finally(() => setCargando(false));
      return;
    }
    if (productoId === '') {
      setMovimientos([]);
      return;
    }
    setCargando(true);
    setError(null);
    api
      .listarMovimientosPorProducto(productoId)
      .then(setMovimientos)
      .catch((err) => setError(mensajeError(err, 'No se pudo cargar el kardex.')))
      .finally(() => setCargando(false));
  }, [productoId, modo]);

  const producto = productos.find((p) => p.id === productoId) ?? null;

  const filtrados = useMemo(
    () =>
      movimientos.filter((m) => {
        if (fTipo && (m.tipoMovimiento ?? m.tipo) !== fTipo) return false;
        const dia = (m.fecha ?? '').slice(0, 10);
        if (fDesde && dia < fDesde) return false;
        if (fHasta && dia > fHasta) return false;
        return true;
      }),
    [movimientos, fTipo, fDesde, fHasta],
  );

  const pag = useClientPagination(filtrados, 10);

  const resumen = useMemo(() => {
    let entradas = 0;
    let salidas = 0;
    for (const m of filtrados) {
      const tipo = m.tipoMovimiento ?? (m.tipo as TipoMovimiento);
      if (tipo === 'ENTRADA') entradas += m.cantidad;
      else if (tipo === 'SALIDA') salidas += m.cantidad;
    }
    return { entradas, salidas };
  }, [filtrados]);

  return (
    <div className="flex min-h-screen bg-surface dark:bg-inverse-surface">
      <Sidebar />
      <div className="flex min-w-0 flex-1 flex-col">
        <TopAppBar titulo="Kardex de Inventario" subtitulo="Inventario / Trazabilidad de movimientos" />

        <main className="flex flex-col gap-space-md p-margin">
          {error && <ErrorState mensaje={error} />}

          <div className="flex flex-wrap items-end gap-space-sm rounded border border-outline-variant bg-surface-container-lowest p-space-md">
            <div className="flex gap-space-xs" role="tablist" aria-label="Alcance del kardex">
              <Button variante={modo === 'producto' ? 'primary' : 'secondary'} onClick={() => setModo('producto')}>
                Por producto
              </Button>
              <Button variante={modo === 'todos' ? 'primary' : 'secondary'} onClick={() => setModo('todos')}>
                Todos
              </Button>
            </div>
            <label className="flex min-w-64 flex-1 flex-col gap-space-xs" htmlFor="k-producto">
              <span className="text-label-md">Artículo / SKU</span>
              <select
                id="k-producto"
                className="h-8 rounded border border-outline-variant px-space-sm text-body-md disabled:opacity-50"
                value={productoId}
                disabled={modo === 'todos'}
                title={modo === 'todos' ? 'Desactiva el modo “Todos” para filtrar por producto' : undefined}
                onChange={(e) => setProductoId(e.target.value === '' ? '' : Number(e.target.value))}
              >
                <option value="">Seleccionar producto…</option>
                {productos.map((p) => (
                  <option key={p.id} value={p.id}>{p.sku ? `${p.sku} — ` : ''}{p.nombre}</option>
                ))}
              </select>
            </label>
            <label className="flex flex-col gap-space-xs" htmlFor="k-tipo">
              <span className="text-label-md">Tipo movimiento</span>
              <select id="k-tipo" className="h-8 rounded border border-outline-variant px-space-sm text-body-md" value={fTipo} onChange={(e) => setFTipo(e.target.value)}>
                <option value="">Todos los tipos</option>
                {TIPOS.map((t) => <option key={t} value={t}>{t}</option>)}
              </select>
            </label>
            <label className="flex flex-col gap-space-xs" htmlFor="k-desde">
              <span className="text-label-md">Fecha desde</span>
              <input id="k-desde" type="date" className="h-8 rounded border border-outline-variant px-space-sm text-body-md" value={fDesde} onChange={(e) => setFDesde(e.target.value)} />
            </label>
            <label className="flex flex-col gap-space-xs" htmlFor="k-hasta">
              <span className="text-label-md">Fecha hasta</span>
              <input id="k-hasta" type="date" className="h-8 rounded border border-outline-variant px-space-sm text-body-md" value={fHasta} onChange={(e) => setFHasta(e.target.value)} />
            </label>
            <Button
              variante="secondary"
              onClick={() => { setFTipo(''); setFDesde(''); setFHasta(''); }}
            >
              Limpiar
            </Button>
          </div>

          {(producto || modo === 'todos') && (
            <div className="grid grid-cols-1 gap-space-md md:grid-cols-4">
              {producto && (
                <ResumenCard titulo="Stock actual" valor={String(producto.stock)} detalle={`${producto.stockMinimo ?? '—'} mín.`} destacado />
              )}
              <ResumenCard titulo="Entradas (filtro)" valor={`+${resumen.entradas}`} detalle="unidades" />
              <ResumenCard titulo="Salidas (filtro)" valor={`−${resumen.salidas}`} detalle="unidades" />
              <ResumenCard titulo="Movimientos" valor={String(filtrados.length)} detalle="transacciones" />
            </div>
          )}

          {modo === 'producto' && productoId === '' ? (
            <p className="text-body-md text-on-surface-variant">Selecciona un producto para ver su kardex.</p>
          ) : cargando ? (
            <p className="text-body-md text-on-surface-variant">Cargando movimientos…</p>
          ) : (
            <>
              <DataTable
                columnas={modo === 'todos'
                  ? [
                      { titulo: 'Fecha y hora', nowrap: true },
                      { titulo: 'Producto' },
                      { titulo: 'Tipo', alinear: 'center', nowrap: true },
                      { titulo: 'Motivo' },
                      { titulo: 'Cant.', alinear: 'right', nowrap: true },
                      { titulo: 'Stock ant. → nuevo', alinear: 'right', nowrap: true },
                      { titulo: 'Costo unit.', alinear: 'right', nowrap: true },
                      { titulo: 'Doc. referencia', nowrap: true },
                      { titulo: 'Responsable' },
                      { titulo: 'Observación' },
                    ]
                  : [
                      { titulo: 'Fecha y hora', nowrap: true },
                      { titulo: 'Tipo', alinear: 'center', nowrap: true },
                      { titulo: 'Motivo' },
                      { titulo: 'Cant.', alinear: 'right', nowrap: true },
                      { titulo: 'Stock ant. → nuevo', alinear: 'right', nowrap: true },
                      { titulo: 'Costo unit.', alinear: 'right', nowrap: true },
                      { titulo: 'Doc. referencia', nowrap: true },
                      { titulo: 'Responsable' },
                      { titulo: 'Observación' },
                    ]}
                filas={pag.content.map((m) => {
                  const tipo = m.tipoMovimiento ?? (m.tipo as TipoMovimiento);
                  const celdas: ReactNode[] = [
                    m.fecha?.replace('T', ' ') ?? '—',
                    ...(modo === 'todos' ? [m.productoNombre] : []),
                    <Badge texto={tipo} tono={tonoTipoMovimiento(tipo)} />,
                    m.motivo,
                    <>{tipo === 'SALIDA' ? '−' : '+'}{m.cantidad}</>,
                    <>{m.stockAnterior} → <strong>{m.stockNuevo}</strong></>,
                    <>S/ {m.costoUnitario ?? 0}</>,
                    <span className="font-code-tabular text-[0.8125rem]">{m.referenciaTipo}{m.referenciaId ? `-${m.referenciaId}` : ''}</span>,
                    m.usuarioUsername,
                    <span className="block max-w-56 truncate text-on-surface-variant" title={m.observacion || m.nota || ''}>{m.observacion || m.nota || '—'}</span>,
                  ];
                  return celdas;
                })}
                vacio="Sin movimientos para este filtro."
              />
              <Pagination
                page={pag.page} size={pag.size} totalElements={pag.totalElements} totalPages={pag.totalPages}
                hasNext={pag.hasNext} hasPrevious={pag.hasPrevious}
                nombreItems="movimientos" onPage={pag.setPage}
              />
            </>
          )}
        </main>
      </div>
    </div>
  );
}

interface ResumenCardProps {
  readonly titulo: string;
  readonly valor: string;
  readonly detalle: string;
  readonly destacado?: boolean;
}

function ResumenCard({ titulo, valor, detalle, destacado }: ResumenCardProps) {
  return (
    <div className={`rounded border border-outline-variant p-space-md ${destacado ? 'bg-primary-container text-on-primary' : 'bg-surface-container-lowest'}`}>
      <p className={`text-label-sm uppercase ${destacado ? '' : 'text-on-surface-variant'}`}>{titulo}</p>
      <p className="text-headline-lg tabular">{valor}</p>
      <p className={`text-body-sm ${destacado ? '' : 'text-on-surface-variant'}`}>{detalle}</p>
    </div>
  );
}

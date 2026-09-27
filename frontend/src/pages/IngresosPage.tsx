import { useEffect, useMemo, useState } from 'react';
import type { FormEvent } from 'react';
import { Sidebar } from '../components/Sidebar';
import { TopAppBar } from '../components/TopAppBar';
import { Button } from '../components/Button';
import { DataTable } from '../components/DataTable';
import { Pagination } from '../components/Pagination';
import { Badge, tonoEstadoIngreso } from '../components/Badge';
import { Modal } from '../components/Modal';
import { Input, Select } from '../components/Fields';
import { ErrorState } from '../components/States';
import { useAuth } from '../auth/AuthContext';
import { useClientPagination } from '../hooks/useClientPagination';
import { listarProveedores } from '../api/catalogos';
import { listarProductosParaPicker } from '../api/productos';
import * as api from '../api/ingresos';
import { mensajeError } from '../api/client';
import { IGV_TASA, TIPOS_DOCUMENTO } from '../data/mockData';
import type { Ingreso, IngresoDetalle, Producto, Proveedor, TipoDocumento } from '../api/types';

interface FilaDetalle {
  productoId: number | '';
  cantidad: string;
  precioUnitario: string;
}

const FILA_VACIA: FilaDetalle = { productoId: '', cantidad: '1', precioUnitario: '' };

/** Screen 3 Stitch (podada): sin KPIs mensuales (sin endpoint agregado).
 *  Formulario en 2 pasos + historial con filtros en memoria (el back
 *  devuelve List: filtrar lo cargado es honesto, nada queda muerto). */
export function IngresosPage() {
  const { tienePermiso } = useAuth();
  const puedeRegistrar = tienePermiso('ingreso.registrar');

  const [ingresos, setIngresos] = useState<Ingreso[]>([]);
  const [proveedores, setProveedores] = useState<Proveedor[]>([]);
  const [productos, setProductos] = useState<Producto[]>([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [formAbierto, setFormAbierto] = useState(false);
  const [verId, setVerId] = useState<number | null>(null);
  const [verDetalle, setVerDetalle] = useState<Ingreso | null>(null);
  const [cargandoVer, setCargandoVer] = useState(false);
  const [anularId, setAnularId] = useState<number | null>(null);
  const [guardando, setGuardando] = useState(false);
  const [errorForm, setErrorForm] = useState<string | null>(null);

  // Filtros del historial (en memoria sobre lo cargado)
  const [fTexto, setFTexto] = useState('');
  const [fTipo, setFTipo] = useState('');
  const [fEstado, setFEstado] = useState('');

  // Paso 1: cabecera
  const [tipoDocumento, setTipoDocumento] = useState<TipoDocumento>('FACTURA');
  const [numeroDocumento, setNumeroDocumento] = useState('');
  const [fecha, setFecha] = useState(new Date().toISOString().slice(0, 10));
  const [proveedorId, setProveedorId] = useState('');
  const [observacion, setObservacion] = useState('');
  // Paso 2: detalle
  const [filas, setFilas] = useState<FilaDetalle[]>([{ ...FILA_VACIA }]);

  const cargar = async () => {
    setCargando(true);
    setError(null);
    try {
      const [ings, provs, prods] = await Promise.all([
        api.listarIngresos(), listarProveedores(), listarProductosParaPicker(),
      ]);
      setIngresos(ings);
      setProveedores(provs);
      setProductos(prods);
    } catch (err) {
      setError(mensajeError(err, 'No se pudo cargar los ingresos.'));
    } finally {
      setCargando(false);
    }
  };

  useEffect(() => {
    void cargar();
  }, []);

  const filtrados = useMemo(() => {
    const t = fTexto.trim().toLowerCase();
    return ingresos.filter((i) => {
      if (fTipo && i.tipoDocumento !== fTipo) return false;
      if (fEstado && i.estado !== fEstado) return false;
      if (t && !`${i.numeroDocumento} ${i.proveedorNombre ?? ''}`.toLowerCase().includes(t)) return false;
      return true;
    });
  }, [ingresos, fTexto, fTipo, fEstado]);

  const pag = useClientPagination(filtrados, 10);

  const nombreProducto = (id: number) => productos.find((p) => p.id === id)?.nombre ?? `#${id}`;

  const totales = useMemo(() => {
    const subtotal = filas.reduce((acc, f) => acc + Number(f.cantidad || 0) * Number(f.precioUnitario || 0), 0);
    const igv = subtotal * IGV_TASA;
    return { subtotal, igv, total: subtotal + igv };
  }, [filas]);

  const setFila = (idx: number, patch: Partial<FilaDetalle>) =>
    setFilas((fs) => fs.map((f, i) => (i === idx ? { ...f, ...patch } : f)));

  const guardar = async (e: FormEvent) => {
    e.preventDefault();
    const detalles: IngresoDetalle[] = filas
      .filter((f) => f.productoId !== '')
      .map((f) => ({
        productoId: Number(f.productoId),
        cantidad: Number(f.cantidad),
        precioUnitario: Number(f.precioUnitario),
      }));
    if (detalles.length === 0) {
      setErrorForm('Agrega al menos un producto al detalle.');
      return;
    }
    setGuardando(true);
    setErrorForm(null);
    try {
      await api.registrarIngreso({
        tipoDocumento, numeroDocumento: numeroDocumento.trim(), fecha,
        proveedorId: Number(proveedorId), observacion: observacion.trim() || undefined,
        detalles,
      });
      setFormAbierto(false);
      limpiarForm();
      await cargar();
    } catch (err) {
      setErrorForm(mensajeError(err, 'No se pudo registrar el ingreso.'));
    } finally {
      setGuardando(false);
    }
  };

  const limpiarForm = () => {
    setTipoDocumento('FACTURA');
    setNumeroDocumento('');
    setFecha(new Date().toISOString().slice(0, 10));
    setProveedorId('');
    setObservacion('');
    setFilas([{ ...FILA_VACIA }]);
    setErrorForm(null);
  };

  const confirmarAnular = async () => {
    if (anularId === null) return;
    setGuardando(true);
    try {
      await api.anularIngreso(anularId);
      setAnularId(null);
      await cargar();
    } catch (err) {
      setError(mensajeError(err, 'No se pudo anular el ingreso.'));
    } finally {
      setGuardando(false);
    }
  };

  const abrirVer = async (id: number | undefined) => {
    if (id === undefined) return;
    setVerId(id);
    setVerDetalle(null);
    setCargandoVer(true);
    try {
      setVerDetalle(await api.getIngreso(id));
    } catch (err) {
      setError(mensajeError(err, 'No se pudo cargar el ingreso.'));
      setVerId(null);
    } finally {
      setCargandoVer(false);
    }
  };

  const verIngreso = verDetalle;

  return (
    <div className="flex min-h-screen bg-surface dark:bg-inverse-surface">
      <Sidebar />
      <div className="flex min-w-0 flex-1 flex-col">
        <TopAppBar titulo="Registro y Control de Ingresos" subtitulo="Inventario / Ingresos de almacén" />

        <main className="flex flex-col gap-space-md p-margin">
          {error && <ErrorState mensaje={error} onReintentar={() => void cargar()} />}

          <div className="flex justify-end">
            <Button
              onClick={() => { limpiarForm(); setFormAbierto((v) => !v); }}
              disabled={!puedeRegistrar}
              title={puedeRegistrar ? undefined : 'Requiere permiso ingreso.registrar'}
            >
              {formAbierto ? 'Ocultar formulario' : '+ Registrar Nuevo Ingreso'}
            </Button>
          </div>

          {formAbierto && (
            <form onSubmit={(e) => void guardar(e)} className="flex flex-col gap-space-md rounded border border-outline-variant bg-surface-container-lowest p-space-lg">
              <h2 className="text-headline-sm">Formulario de ingreso de mercadería</h2>
              {errorForm && <p className="rounded bg-error-container/50 px-space-sm py-space-xs text-body-md text-on-error-container">{errorForm}</p>}

              <fieldset>
                <legend className="mb-space-sm text-label-md uppercase">Paso 1 · Cabecera del documento</legend>
                <div className="grid grid-cols-1 gap-space-md md:grid-cols-4">
                  <Select id="ing-tipo" etiqueta="Tipo de documento *" value={tipoDocumento} onChange={(e) => setTipoDocumento(e.target.value as TipoDocumento)} required>
                    {TIPOS_DOCUMENTO.map((t) => <option key={t} value={t}>{t}</option>)}
                  </Select>
                  <Input id="ing-num" etiqueta="N° de documento *" value={numeroDocumento} onChange={(e) => setNumeroDocumento(e.target.value)} required />
                  <Input id="ing-fecha" etiqueta="Fecha de emisión *" type="date" value={fecha} onChange={(e) => setFecha(e.target.value)} required />
                  <Select id="ing-prov" etiqueta="Proveedor *" value={proveedorId} onChange={(e) => setProveedorId(e.target.value)} required>
                    <option value="">Seleccionar…</option>
                    {proveedores.map((p) => <option key={p.id} value={p.id}>{p.nombre} (RUC {p.ruc})</option>)}
                  </Select>
                </div>
                <div className="mt-space-md">
                  <Input id="ing-obs" etiqueta="Observación / destino" value={observacion} onChange={(e) => setObservacion(e.target.value)} />
                </div>
              </fieldset>

              <fieldset>
                <legend className="mb-space-sm flex w-full items-center justify-between text-label-md uppercase">
                  Paso 2 · Detalle de productos
                  <Button variante="secondary" onClick={() => setFilas((fs) => [...fs, { ...FILA_VACIA }])}>
                    + Agregar producto
                  </Button>
                </legend>
                <DataTable
                  columnas={[
                    { titulo: 'Producto' },
                    { titulo: 'Cantidad', alinear: 'right', nowrap: true },
                    { titulo: 'Precio unit. (S/)', alinear: 'right', nowrap: true },
                    { titulo: 'Subtotal', alinear: 'right', nowrap: true },
                    { titulo: 'Acción', nowrap: true },
                  ]}
                  filas={filas.map((f, idx) => [
                    <select
                      className="h-8 w-full min-w-52 rounded border border-outline-variant px-space-xs text-body-md"
                      value={f.productoId}
                      onChange={(e) => setFila(idx, { productoId: e.target.value === '' ? '' : Number(e.target.value) })}
                      required
                    >
                      <option value="">Seleccionar…</option>
                      {productos.map((p) => <option key={p.id} value={p.id}>{p.nombre} · SKU {p.sku || '—'}</option>)}
                    </select>,
                    <input type="number" min="1" step="1" required className="h-8 w-24 rounded border border-outline-variant px-space-xs" value={f.cantidad} onChange={(e) => setFila(idx, { cantidad: e.target.value })} />,
                    <input type="number" min="0" step="0.01" required className="h-8 w-28 rounded border border-outline-variant px-space-xs" value={f.precioUnitario} onChange={(e) => setFila(idx, { precioUnitario: e.target.value })} />,
                    <>S/ {(Number(f.cantidad || 0) * Number(f.precioUnitario || 0)).toFixed(2)}</>,
                    <button type="button" className="text-body-md text-error hover:underline" onClick={() => setFilas((fs) => fs.filter((_, i) => i !== idx))}>Quitar</button>,
                  ])}
                />
                <div className="ml-auto mt-space-sm w-full max-w-80 text-body-md tabular">
                  <p className="flex justify-between"><span>Subtotal:</span><span>S/ {totales.subtotal.toFixed(2)}</span></p>
                  <p className="flex justify-between"><span>I.G.V. (18%):</span><span>S/ {totales.igv.toFixed(2)}</span></p>
                  <p className="flex justify-between font-semibold text-primary-container"><span>Total:</span><span>S/ {totales.total.toFixed(2)}</span></p>
                </div>
              </fieldset>

              <div className="flex justify-end gap-space-sm">
                <Button variante="secondary" onClick={() => setFormAbierto(false)}>Cancelar</Button>
                <Button type="submit" disabled={guardando}>{guardando ? 'Guardando…' : 'Guardar Ingreso'}</Button>
              </div>
            </form>
          )}

          <section className="rounded border border-outline-variant bg-surface-container-lowest p-space-md">
            <div className="mb-space-sm flex flex-wrap items-center gap-space-sm">
              <h2 className="text-headline-sm">Historial de ingresos registrados</h2>
              <span className="ml-auto flex flex-wrap gap-space-sm">
                <input
                  placeholder="Buscar por N° doc o proveedor…"
                  value={fTexto}
                  onChange={(e) => setFTexto(e.target.value)}
                  className="h-8 rounded border border-outline-variant px-space-sm text-body-md"
                />
                <select value={fTipo} onChange={(e) => setFTipo(e.target.value)} className="h-8 rounded border border-outline-variant px-space-xs text-body-md">
                  <option value="">Todos los tipos</option>
                  {TIPOS_DOCUMENTO.map((t) => <option key={t} value={t}>{t}</option>)}
                </select>
                <select value={fEstado} onChange={(e) => setFEstado(e.target.value)} className="h-8 rounded border border-outline-variant px-space-xs text-body-md">
                  <option value="">Todos los estados</option>
                  <option value="REGISTRADO">REGISTRADO</option>
                  <option value="ANULADO">ANULADO</option>
                </select>
              </span>
            </div>
            {cargando && ingresos.length === 0 ? (
              <p className="text-body-md text-on-surface-variant">Cargando historial…</p>
            ) : (
              <>
                <DataTable
                  columnas={[
                    { titulo: 'N° Doc', nowrap: true },
                    { titulo: 'Tipo', alinear: 'center', nowrap: true },
                    { titulo: 'Proveedor' },
                    { titulo: 'Fecha', alinear: 'right', nowrap: true },
                    { titulo: 'Total (S/)', alinear: 'right', nowrap: true },
                    { titulo: 'Estado', alinear: 'center', nowrap: true },
                    { titulo: 'Acciones', nowrap: true },
                  ]}
                  filas={pag.content.map((i) => [
                    <span className="font-code-tabular text-[0.8125rem] text-secondary">{i.numeroDocumento}</span>,
                    <Badge texto={i.tipoDocumento} tono="info" />,
                    i.proveedorNombre ?? `#${i.proveedorId}`,
                    i.fecha,
                    <>S/ {i.total ?? 0}</>,
                    <Badge texto={i.estado ?? '—'} tono={tonoEstadoIngreso(i.estado)} />,
                    <>
                      <button type="button" className="px-space-xs text-body-md text-secondary hover:underline" onClick={() => void abrirVer(i.id)}>Ver</button>
                      {i.estado === 'REGISTRADO' && (
                        <button type="button" className="px-space-xs text-body-md text-error hover:underline" onClick={() => setAnularId(i.id ?? null)}>Anular</button>
                      )}
                    </>,
                  ])}
                  vacio="Sin ingresos para este filtro."
                />
                <Pagination
                  page={pag.page} size={pag.size} totalElements={pag.totalElements} totalPages={pag.totalPages}
                  hasNext={pag.hasNext} hasPrevious={pag.hasPrevious}
                  nombreItems="ingresos" onPage={pag.setPage}
                />
              </>
            )}
          </section>
        </main>
      </div>

      {verId !== null && (
        <Modal titulo={verIngreso ? `Ingreso ${verIngreso.numeroDocumento}` : 'Detalle de ingreso'} onCerrar={() => { setVerId(null); setVerDetalle(null); }}>
          {cargandoVer || !verIngreso ? (
            <p className="text-body-md text-on-surface-variant">Cargando…</p>
          ) : (
            <>
          <dl className="mb-space-md grid grid-cols-2 gap-space-sm text-body-md">
            <div><dt className="text-label-md text-on-surface-variant">Tipo</dt><dd>{verIngreso.tipoDocumento}</dd></div>
            <div><dt className="text-label-md text-on-surface-variant">Fecha</dt><dd>{verIngreso.fecha}</dd></div>
            <div><dt className="text-label-md text-on-surface-variant">Proveedor</dt><dd>{verIngreso.proveedorNombre}</dd></div>
            <div><dt className="text-label-md text-on-surface-variant">Estado</dt><dd>{verIngreso.estado}</dd></div>
          </dl>
          <DataTable
            columnas={[
              { titulo: 'Producto' },
              { titulo: 'Cant.', alinear: 'right', nowrap: true },
              { titulo: 'P. Unit.', alinear: 'right', nowrap: true },
              { titulo: 'Subtotal', alinear: 'right', nowrap: true },
            ]}
            filas={(verIngreso.detalles ?? []).map((d) => [
              d.productoNombre ?? nombreProducto(d.productoId),
              d.cantidad,
              <>S/ {d.precioUnitario}</>,
              <>S/ {d.subtotal ?? 0}</>,
            ])}
          />
          <div className="ml-auto mt-space-sm w-full max-w-64 text-body-md tabular">
            <p className="flex justify-between"><span>Subtotal:</span><span>S/ {verIngreso.subtotal ?? 0}</span></p>
            <p className="flex justify-between"><span>IGV:</span><span>S/ {verIngreso.igv ?? 0}</span></p>
            <p className="flex justify-between font-semibold"><span>Total:</span><span>S/ {verIngreso.total ?? 0}</span></p>
          </div>
            </>
          )}
        </Modal>
      )}

      {anularId !== null && (
        <Modal titulo="Anular ingreso" textoAccion="Anular ingreso" cargandoAccion={guardando} onCerrar={() => setAnularId(null)} onAccion={() => void confirmarAnular()}>
          <p className="text-body-md">El ingreso se marca ANULADO y revierte el stock. Esta acción no se puede deshacer.</p>
        </Modal>
      )}
    </div>
  );
}

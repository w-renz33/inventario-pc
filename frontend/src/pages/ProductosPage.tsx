import { useEffect, useState } from 'react';
import { Sidebar } from '../components/Sidebar';
import { TopAppBar } from '../components/TopAppBar';
import { Button } from '../components/Button';
import { DataTable } from '../components/DataTable';
import { Pagination } from '../components/Pagination';
import { Badge, tonoEstadoProducto } from '../components/Badge';
import { Modal } from '../components/Modal';
import { ErrorState } from '../components/States';
import { useServerPagination } from '../hooks/useServerPagination';
import { listarCategorias, crearCategoria as crearCategoriaApi } from '../api/catalogos';
import { Input } from '../components/Fields';
import * as api from '../api/productos';
import { mensajeError } from '../api/client';
import type { Categoria, Producto } from '../api/types';
import { ProductoFormModal, formError } from './ProductoFormModal';
import { AjusteStockModal } from './AjusteStockModal';

type Dialogo =
  | { tipo: 'ninguno' }
  | { tipo: 'form'; producto?: Producto }
  | { tipo: 'ajuste'; producto: Producto }
  | { tipo: 'eliminar'; producto: Producto }
  | { tipo: 'ver'; id: number }
  | { tipo: 'categoria' };

/** Screen 2 Stitch (podada): sin KPIs (sin endpoint agregado), sin buscador
 *  por texto ni filtro de estado (sin params en el back), sin Reporte.
 *  Filtro "Tipo" del diseño = filtro por categoria (categoriaId). */
export function ProductosPage() {
  const pag = useServerPagination({ sizeInicial: 10 });
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [dialogo, setDialogo] = useState<Dialogo>({ tipo: 'ninguno' });
  const [guardando, setGuardando] = useState(false);
  const [errorForm, setErrorForm] = useState<string | null>(null);
  const [errorAccion, setErrorAccion] = useState<string | null>(null);
  const [detalle, setDetalle] = useState<Producto | null>(null);
  const [catNombre, setCatNombre] = useState('');
  const [catDesc, setCatDesc] = useState('');

  const cargarCategorias = async () => {
    try {
      setCategorias(await listarCategorias());
    } catch {
      setCategorias([]);
    }
  };

  useEffect(() => {
    void cargarCategorias();
  }, []);

  const cerrar = () => {
    setDialogo({ tipo: 'ninguno' });
    setErrorForm(null);
  };

  const guardarForm = async (dto: Parameters<typeof api.crearProducto>[0] | Parameters<typeof api.actualizarProducto>[1]) => {
    setGuardando(true);
    setErrorForm(null);
    try {
      if (dialogo.tipo === 'form' && dialogo.producto) {
        await api.actualizarProducto(dialogo.producto.id, dto);
      } else {
        await api.crearProducto(dto as Parameters<typeof api.crearProducto>[0]);
      }
      cerrar();
      await pag.recargar();
    } catch (err) {
      setErrorForm(formError(err));
    } finally {
      setGuardando(false);
    }
  };

  const aplicarAjuste = async (cantidad: number, motivo: string) => {
    if (dialogo.tipo !== 'ajuste') return;
    setGuardando(true);
    setErrorForm(null);
    try {
      await api.ajustarStock(dialogo.producto.id, cantidad, motivo);
      cerrar();
      await pag.recargar();
    } catch (err) {
      setErrorForm(mensajeError(err, 'No se pudo ajustar el stock.'));
    } finally {
      setGuardando(false);
    }
  };

  const confirmarEliminar = async () => {    if (dialogo.tipo !== 'eliminar') return;
    setGuardando(true);
    setErrorAccion(null);
    try {
      await api.eliminarProducto(dialogo.producto.id);
      cerrar();
      await pag.recargar();
    } catch (err) {
      setErrorAccion(mensajeError(err, 'No se pudo eliminar el producto.'));
    } finally {
      setGuardando(false);
    }
  };

  const abrirVer = async (id: number) => {
    setDialogo({ tipo: 'ver', id });
    setDetalle(null);
    try {
      setDetalle(await api.getProducto(id));
    } catch (err) {
      setErrorAccion(mensajeError(err, 'No se pudo cargar el producto.'));
      setDialogo({ tipo: 'ninguno' });
    }
  };

  const crearCategoria = async () => {
    if (catNombre.trim() === '') return;
    setGuardando(true);
    setErrorForm(null);
    try {
      await crearCategoriaApi(catNombre.trim(), catDesc.trim() || undefined);
      setCatNombre('');
      setCatDesc('');
      setDialogo({ tipo: 'ninguno' });
      await cargarCategorias();
    } catch (err) {
      setErrorForm(mensajeError(err, 'No se pudo crear la categoría.'));
    } finally {
      setGuardando(false);
    }
  };

  const filas = pag.pagina?.content ?? [];

  return (
    <div className="flex min-h-screen bg-surface dark:bg-inverse-surface">
      <Sidebar />
      <div className="flex min-w-0 flex-1 flex-col">
        <TopAppBar titulo="Gestión de Productos" subtitulo="Inventario / Catálogo" />

        <main className="flex flex-col gap-space-md p-margin">
          {pag.error && <ErrorState mensaje={pag.error} onReintentar={() => void pag.recargar()} />}
          {errorAccion && <ErrorState mensaje={errorAccion} />}

          <div className="flex flex-wrap items-end gap-space-sm rounded border border-outline-variant bg-surface-container-lowest p-space-md">
            <label className="flex flex-col gap-space-xs" htmlFor="filtro-categoria">
              <span className="text-label-md">Categoria</span>
              <select
                id="filtro-categoria"
                className="h-8 rounded border border-outline-variant bg-surface-container-lowest px-space-sm text-body-md"
                value={pag.categoriaId ?? ''}
                onChange={(e) => pag.cambiarCategoria(e.target.value === '' ? undefined : Number(e.target.value))}
              >
                <option value="">Todas</option>
                {categorias.map((c) => (
                  <option key={c.id} value={c.id}>{c.nombre}</option>
                ))}
              </select>
            </label>
            <Button variante="secondary" onClick={() => pag.cambiarCategoria(undefined)}>Limpiar</Button>
            <Button variante="secondary" onClick={() => { setCatNombre(''); setCatDesc(''); setErrorForm(null); setDialogo({ tipo: 'categoria' }); }}>+ Categoría</Button>
            <span className="ml-auto" />
            <Button onClick={() => setDialogo({ tipo: 'form' })}>+ Nuevo Producto</Button>
          </div>

          {pag.cargando && filas.length === 0 ? (
            <p className="text-body-md text-on-surface-variant">Cargando catalogo…</p>
          ) : (
            <>
              <DataTable
                columnas={[
                  { titulo: 'SKU', nowrap: true },
                  { titulo: 'Nombre' },
                  { titulo: 'Marca' },
                  { titulo: 'Modelo' },
                  { titulo: 'Tipo', alinear: 'center', nowrap: true },
                  { titulo: 'P. Compra / Venta', alinear: 'right', nowrap: true },
                  { titulo: 'Stock / Mín', alinear: 'right', nowrap: true },
                  { titulo: 'Estado', alinear: 'center', nowrap: true },
                  { titulo: 'Acción', nowrap: true },
                ]}
                filas={filas.map((p) => [
                  <span className="font-code-tabular text-[0.8125rem] text-secondary">{p.sku || '—'}</span>,
                  <span className="font-medium">{p.nombre}</span>,
                  p.marca || '—',
                  <span className="text-on-surface-variant">{p.modelo || '—'}</span>,
                  <Badge texto={p.tipoComponente ?? '—'} tono="info" />,
                  <>S/ {p.precioCompra ?? 0} / <strong>S/ {p.precioVenta}</strong></>,
                  <span className={p.stockMinimo != null && p.stock <= p.stockMinimo ? 'font-medium text-error' : ''}>
                    {p.stock} / {p.stockMinimo ?? '—'}
                  </span>,
                  <Badge texto={p.estado ?? '—'} tono={tonoEstadoProducto(p.estado)} />,
                  <>
                    <button type="button" className="px-space-xs text-body-md text-secondary hover:underline" onClick={() => void abrirVer(p.id)}>Ver</button>
                    <button type="button" className="px-space-xs text-body-md text-secondary hover:underline" onClick={() => setDialogo({ tipo: 'form', producto: p })}>Editar</button>
                    <button type="button" className="px-space-xs text-body-md text-secondary hover:underline" onClick={() => setDialogo({ tipo: 'ajuste', producto: p })}>Ajustar</button>
                    <button type="button" className="px-space-xs text-body-md text-error hover:underline" onClick={() => setDialogo({ tipo: 'eliminar', producto: p })}>Eliminar</button>
                  </>,
                ])}
                vacio="Sin productos para este filtro."
              />
              {pag.pagina && (
                <Pagination
                  page={pag.pagina.page}
                  size={pag.pagina.size}
                  totalElements={pag.pagina.totalElements}
                  totalPages={pag.pagina.totalPages}
                  hasNext={pag.pagina.hasNext}
                  hasPrevious={pag.pagina.hasPrevious}
                  nombreItems="productos"
                  onPage={pag.setPage}
                  onSize={pag.cambiarSize}
                />
              )}
            </>
          )}
        </main>
      </div>

      {dialogo.tipo === 'form' && (
        <ProductoFormModal
          titulo={dialogo.producto ? `Editar — ${dialogo.producto.nombre}` : 'Nuevo producto'}
          inicial={dialogo.producto ?? {}}
          categoriasExistentes={categorias.map((c) => c.nombre)}
          guardando={guardando}
          error={errorForm}
          onGuardar={(dto) => void guardarForm(dto)}
          onCerrar={cerrar}
        />
      )}
      {dialogo.tipo === 'ajuste' && (
        <AjusteStockModal
          producto={dialogo.producto}
          guardando={guardando}
          error={errorForm}
          onAjustar={(c, m) => void aplicarAjuste(c, m)}
          onCerrar={cerrar}
        />
      )}
      {dialogo.tipo === 'ver' && (
        <Modal titulo="Detalle de producto" onCerrar={() => setDialogo({ tipo: 'ninguno' })}>
          {!detalle ? (
            <p className="text-body-md text-on-surface-variant">Cargando…</p>
          ) : (
            <dl className="grid grid-cols-1 gap-space-sm text-body-md md:grid-cols-2">
              <DetalleCampo etiqueta="Nombre" valor={detalle.nombre} />
              <DetalleCampo etiqueta="SKU" valor={detalle.sku || '—'} />
              <DetalleCampo etiqueta="Categoría" valor={detalle.categoria} />
              <DetalleCampo etiqueta="Tipo" valor={detalle.tipoComponente ?? '—'} />
              <DetalleCampo etiqueta="Marca / Modelo" valor={`${detalle.marca || '—'} / ${detalle.modelo || '—'}`} />
              <DetalleCampo etiqueta="Código de barras" valor={detalle.codigoBarras || '—'} />
              <DetalleCampo etiqueta="Precio compra / venta" valor={`S/ ${detalle.precioCompra ?? 0} / S/ ${detalle.precioVenta}`} />
              <DetalleCampo etiqueta="Stock / mínimo" valor={`${detalle.stock} / ${detalle.stockMinimo ?? '—'}`} />
              <DetalleCampo etiqueta="Estado" valor={detalle.estado ?? '—'} />
              <DetalleCampo etiqueta="Descripción" valor={detalle.descripcion || '—'} />
            </dl>
          )}
        </Modal>
      )}
      {dialogo.tipo === 'categoria' && (
        <Modal titulo="Nueva categoría" textoAccion="Crear" cargandoAccion={guardando} onCerrar={() => setDialogo({ tipo: 'ninguno' })} onAccion={() => void crearCategoria()}>
          {errorForm && <p className="mb-space-md rounded bg-error-container/50 px-space-sm py-space-xs text-body-md text-on-error-container">{errorForm}</p>}
          <div className="flex flex-col gap-space-md">
            <Input id="cat-nombre" etiqueta="Nombre *" placeholder="ej. CPU, MONITORES…" value={catNombre} onChange={(e) => setCatNombre(e.target.value)} required />
            <Input id="cat-desc" etiqueta="Descripción" value={catDesc} onChange={(e) => setCatDesc(e.target.value)} />
          </div>
        </Modal>
      )}
      {dialogo.tipo === 'eliminar' && (
        <Modal titulo="Eliminar producto" textoAccion="Eliminar" cargandoAccion={guardando} onCerrar={cerrar} onAccion={() => void confirmarEliminar()}>
          <p className="text-body-md">
            ¿Eliminar <strong>{dialogo.producto.nombre}</strong> (SKU {dialogo.producto.sku || '—'})? El backend registra un
            movimiento de SALIDA por el stock restante.
          </p>
        </Modal>
      )}
    </div>
  );
}
interface DetalleCampoProps {
  readonly etiqueta: string;
  readonly valor: string;
}

function DetalleCampo({ etiqueta, valor }: DetalleCampoProps) {
  return (
    <div>
      <dt className="text-label-md text-on-surface-variant">{etiqueta}</dt>
      <dd>{valor}</dd>
    </div>
  );
}

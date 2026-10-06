import { useEffect, useMemo, useState } from 'react';
import type { FormEvent } from 'react';
import { Sidebar } from '../components/Sidebar';
import { TopAppBar } from '../components/TopAppBar';
import { Button } from '../components/Button';
import { DataTable } from '../components/DataTable';
import { Pagination } from '../components/Pagination';
import { Badge } from '../components/Badge';
import { AccionBtn } from '../components/AccionBtn';
import { Modal } from '../components/Modal';
import { Input } from '../components/Fields';
import { ErrorState } from '../components/States';
import { useClientPagination } from '../hooks/useClientPagination';
import * as api from '../api/proveedores';
import { listarProveedores } from '../api/catalogos';
import { mensajeError } from '../api/client';
import type { Proveedor } from '../api/types';

const VACIO: Proveedor = {
  nombre: '', ruc: '', telefono: '', email: '',
  direccion: '', contactoNombre: '', contactoTelefono: '', activo: true,
};

type Dialogo =
  | { tipo: 'ninguno' }
  | { tipo: 'form'; proveedor?: Proveedor }
  | { tipo: 'ver'; id: number }
  | { tipo: 'eliminar'; proveedor: Proveedor };

/** Pantalla fuera del set Stitch (el diseño no la trae): mismo sistema
 *  visual, CRUD completo de /api/proveedores. */
export function ProveedoresPage() {
  const [items, setItems] = useState<Proveedor[]>([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [fTexto, setFTexto] = useState('');
  const [dialogo, setDialogo] = useState<Dialogo>({ tipo: 'ninguno' });
  const [guardando, setGuardando] = useState(false);
  const [errorForm, setErrorForm] = useState<string | null>(null);
  const [detalle, setDetalle] = useState<Proveedor | null>(null);

  const cargar = async () => {
    setCargando(true);
    setError(null);
    try {
      setItems(await listarProveedores());
    } catch (err) {
      setError(mensajeError(err, 'No se pudo cargar los proveedores.'));
    } finally {
      setCargando(false);
    }
  };

  useEffect(() => {
    void cargar();
  }, []);

  const filtrados = useMemo(() => {
    const t = fTexto.trim().toLowerCase();
    if (!t) return items;
    return items.filter((p) =>
      `${p.nombre} ${p.ruc} ${p.email ?? ''}`.toLowerCase().includes(t),
    );
  }, [items, fTexto]);

  const pag = useClientPagination(filtrados, 10);

  const abrirVer = async (id: number) => {
    setDialogo({ tipo: 'ver', id });
    setDetalle(null);
    try {
      setDetalle(await api.getProveedor(id));
    } catch (err) {
      setError(mensajeError(err, 'No se pudo cargar el proveedor.'));
      setDialogo({ tipo: 'ninguno' });
    }
  };

  const guardar = async (dto: Proveedor) => {
    setGuardando(true);
    setErrorForm(null);
    try {
      if (dialogo.tipo === 'form' && dialogo.proveedor?.id !== undefined) {
        await api.actualizarProveedor(dialogo.proveedor.id, dto);
      } else {
        await api.crearProveedor(dto);
      }
      setDialogo({ tipo: 'ninguno' });
      await cargar();
    } catch (err) {
      setErrorForm(mensajeError(err, 'No se pudo guardar el proveedor.'));
    } finally {
      setGuardando(false);
    }
  };

  const confirmarEliminar = async () => {
    if (dialogo.tipo !== 'eliminar' || dialogo.proveedor.id === undefined) return;
    setGuardando(true);
    try {
      await api.eliminarProveedor(dialogo.proveedor.id);
      setDialogo({ tipo: 'ninguno' });
      await cargar();
    } catch (err) {
      setError(mensajeError(err, 'No se pudo eliminar el proveedor.'));
    } finally {
      setGuardando(false);
    }
  };

  return (
    <div className="flex min-h-screen bg-surface dark:bg-inverse-surface">
      <Sidebar />
      <div className="flex min-w-0 flex-1 flex-col">
        <TopAppBar titulo="Gestión de Proveedores" subtitulo="Catálogo / Proveedores" />

        <main className="flex flex-col gap-space-md p-margin">
          {error && <ErrorState mensaje={error} onReintentar={() => void cargar()} />}

          <div className="flex flex-wrap items-end gap-space-sm rounded border border-outline-variant bg-surface-container-lowest p-space-md">
            <input
              placeholder="Buscar por nombre, RUC o email…"
              value={fTexto}
              onChange={(e) => setFTexto(e.target.value)}
              className="h-8 min-w-64 flex-1 rounded border border-outline-variant px-space-sm text-body-md"
            />
            <span className="ml-auto" />
            <Button onClick={() => { setErrorForm(null); setDialogo({ tipo: 'form' }); }}>+ Nuevo Proveedor</Button>
          </div>

          {cargando && items.length === 0 ? (
            <p className="text-body-md text-on-surface-variant">Cargando proveedores…</p>
          ) : (
            <>
              <DataTable
                columnas={[
                  { titulo: 'Nombre' },
                  { titulo: 'RUC', nowrap: true },
                  { titulo: 'Teléfono', nowrap: true },
                  { titulo: 'Email' },
                  { titulo: 'Contacto' },
                  { titulo: 'Estado', alinear: 'center', nowrap: true },
                  { titulo: 'Acciones', nowrap: true },
                ]}
                filas={pag.content.map((p) => [
                  <span className="font-medium">{p.nombre}</span>,
                  <span className="font-code-tabular text-[0.8125rem]">{p.ruc}</span>,
                  <span className="tabular">{p.telefono || '—'}</span>,
                  p.email || '—',
                  p.contactoNombre || '—',
                  <Badge texto={p.activo ? 'ACTIVO' : 'INACTIVO'} tono={p.activo ? 'success' : 'neutral'} />,
                  <span className="flex items-center gap-space-xs">
                    <AccionBtn variante="primaria" onClick={() => void abrirVer(p.id!)}>
                      Ver
                    </AccionBtn>
                    <AccionBtn variante="neutra" onClick={() => { setErrorForm(null); setDialogo({ tipo: 'form', proveedor: p }); }}>
                      Editar
                    </AccionBtn>
                    <AccionBtn variante="destructiva" onClick={() => setDialogo({ tipo: 'eliminar', proveedor: p })}>
                      Eliminar
                    </AccionBtn>
                  </span>,
                ])}
                vacio="Sin proveedores."
              />
              <Pagination
                page={pag.page} size={pag.size} totalElements={pag.totalElements} totalPages={pag.totalPages}
                hasNext={pag.hasNext} hasPrevious={pag.hasPrevious}
                nombreItems="proveedores" onPage={pag.setPage}
              />
            </>
          )}
        </main>
      </div>

      {dialogo.tipo === 'form' && (
        <ProveedorFormModal
          titulo={dialogo.proveedor ? `Editar — ${dialogo.proveedor.nombre}` : 'Nuevo proveedor'}
          inicial={dialogo.proveedor ?? VACIO}
          guardando={guardando}
          error={errorForm}
          onGuardar={(dto) => void guardar(dto)}
          onCerrar={() => setDialogo({ tipo: 'ninguno' })}
        />
      )}

      {dialogo.tipo === 'ver' && (
        <Modal titulo="Detalle de proveedor" onCerrar={() => setDialogo({ tipo: 'ninguno' })}>
          {!detalle ? (
            <p className="text-body-md text-on-surface-variant">Cargando…</p>
          ) : (
            <dl className="grid grid-cols-1 gap-space-sm text-body-md md:grid-cols-2">
              <Campo etiqueta="Nombre" valor={detalle.nombre} />
              <Campo etiqueta="RUC" valor={detalle.ruc} />
              <Campo etiqueta="Teléfono" valor={detalle.telefono || '—'} />
              <Campo etiqueta="Email" valor={detalle.email || '—'} />
              <Campo etiqueta="Dirección" valor={detalle.direccion || '—'} />
              <Campo etiqueta="Contacto" valor={detalle.contactoNombre || '—'} />
              <Campo etiqueta="Tel. contacto" valor={detalle.contactoTelefono || '—'} />
              <Campo etiqueta="Estado" valor={detalle.activo ? 'ACTIVO' : 'INACTIVO'} />
            </dl>
          )}
        </Modal>
      )}

      {dialogo.tipo === 'eliminar' && (
        <Modal titulo="Eliminar proveedor" textoAccion="Eliminar" cargandoAccion={guardando} onCerrar={() => setDialogo({ tipo: 'ninguno' })} onAccion={() => void confirmarEliminar()}>
          <p className="text-body-md">¿Eliminar <strong>{dialogo.proveedor.nombre}</strong> (RUC {dialogo.proveedor.ruc})?</p>
        </Modal>
      )}
    </div>
  );
}

interface CampoProps {
  readonly etiqueta: string;
  readonly valor: string;
}

function Campo({ etiqueta, valor }: CampoProps) {
  return (
    <div>
      <dt className="text-label-md text-on-surface-variant">{etiqueta}</dt>
      <dd>{valor}</dd>
    </div>
  );
}

interface FormModalProps {
  readonly titulo: string;
  readonly inicial: Proveedor;
  readonly guardando: boolean;
  readonly error: string | null;
  readonly onGuardar: (dto: Proveedor) => void;
  readonly onCerrar: () => void;
}

function ProveedorFormModal({ titulo, inicial, guardando, error, onGuardar, onCerrar }: FormModalProps) {
  const [form, setForm] = useState<Proveedor>({ ...inicial });

  const set = (k: keyof Proveedor) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm((f) => ({ ...f, [k]: e.target.value }));

  const submit = (e: FormEvent) => {
    e.preventDefault();
    onGuardar({ ...form, nombre: form.nombre.trim(), ruc: form.ruc.trim() });
  };

  return (
    <Modal titulo={titulo} textoAccion="Guardar" cargandoAccion={guardando} onCerrar={onCerrar} onAccion={() => document.getElementById('prov-form-submit')?.click()}>
      {error && <p className="mb-space-md rounded bg-error-container/50 px-space-sm py-space-xs text-body-md text-on-error-container">{error}</p>}
      <form onSubmit={submit} className="grid grid-cols-1 gap-space-md md:grid-cols-2">
        <Input id="pv-nombre" etiqueta="Nombre *" value={form.nombre} onChange={set('nombre')} required />
        <Input id="pv-ruc" etiqueta="RUC *" value={form.ruc} onChange={set('ruc')} required />
        <Input id="pv-tel" etiqueta="Teléfono" value={form.telefono ?? ''} onChange={set('telefono')} />
        <Input id="pv-email" etiqueta="Email" type="email" value={form.email ?? ''} onChange={set('email')} />
        <Input id="pv-dir" etiqueta="Dirección" value={form.direccion ?? ''} onChange={set('direccion')} />
        <Input id="pv-cn" etiqueta="Nombre de contacto" value={form.contactoNombre ?? ''} onChange={set('contactoNombre')} />
        <Input id="pv-ct" etiqueta="Teléfono de contacto" value={form.contactoTelefono ?? ''} onChange={set('contactoTelefono')} />
        <label className="flex items-center gap-space-sm text-body-md" htmlFor="pv-activo">
          <input
            id="pv-activo"
            type="checkbox"
            checked={form.activo ?? true}
            onChange={(e) => setForm((f) => ({ ...f, activo: e.target.checked }))}
            className="h-4 w-4"
          />
          Activo
        </label>
        <button id="prov-form-submit" type="submit" className="hidden">Guardar</button>
      </form>
    </Modal>
  );
}

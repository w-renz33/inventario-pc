import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import type { Producto, ProductoCreate, TipoComponente } from '../api/types';
import { TIPOS_COMPONENTE } from '../data/mockData';
import { Modal } from '../components/Modal';
import { Input, Select } from '../components/Fields';
import { mensajeError } from '../api/client';

interface ProductoFormModalProps {
  readonly titulo: string;
  readonly inicial: Partial<Producto>;
  readonly categoriasExistentes: readonly string[];
  readonly guardando: boolean;
  readonly error: string | null;
  readonly onGuardar: (dto: ProductoCreate & { stock?: number; stockMinimo?: number; estado?: string }) => void;
  readonly onCerrar: () => void;
}

export function ProductoFormModal({
  titulo, inicial, categoriasExistentes, guardando, error, onGuardar, onCerrar,
}: ProductoFormModalProps) {
  const [form, setForm] = useState({
    nombre: inicial.nombre ?? '',
    descripcion: inicial.descripcion ?? '',
    categoria: inicial.categoria ?? '',
    precioVenta: inicial.precioVenta?.toString() ?? '',
    precioCompra: inicial.precioCompra?.toString() ?? '',
    codigoBarras: inicial.codigoBarras ?? '',
    sku: inicial.sku ?? '',
    marca: inicial.marca ?? '',
    modelo: inicial.modelo ?? '',
    tipoComponente: (inicial.tipoComponente ?? '') as string,
    stock: inicial.stock?.toString() ?? '',
    stockMinimo: inicial.stockMinimo?.toString() ?? '',
    estado: inicial.estado ?? 'ACTIVO',
  });
  const esEdicion = inicial.id !== undefined;

  useEffect(() => {
    setForm({
      nombre: inicial.nombre ?? '',
      descripcion: inicial.descripcion ?? '',
      categoria: inicial.categoria ?? '',
      precioVenta: inicial.precioVenta?.toString() ?? '',
      precioCompra: inicial.precioCompra?.toString() ?? '',
      codigoBarras: inicial.codigoBarras ?? '',
      sku: inicial.sku ?? '',
      marca: inicial.marca ?? '',
      modelo: inicial.modelo ?? '',
      tipoComponente: (inicial.tipoComponente ?? '') as string,
      stock: inicial.stock?.toString() ?? '',
      stockMinimo: inicial.stockMinimo?.toString() ?? '',
      estado: inicial.estado ?? 'ACTIVO',
    });
  }, [inicial]);

  const set = (k: keyof typeof form) => (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>,
  ) => setForm((f) => ({ ...f, [k]: e.target.value }));

  // Advertencia no bloqueante: si la categoria coincide con un TipoComponente
  // conocido, el tipo elegido deberia coincidir. Si no, se avisa pero se permite.
  const categoriaNorm = form.categoria.trim().toUpperCase().replace(/\s+/g, '_');
  const tipoSugerido = (TIPOS_COMPONENTE as readonly string[]).includes(categoriaNorm)
    ? (categoriaNorm as TipoComponente)
    : null;
  const desajuste =
    tipoSugerido !== null &&
    form.tipoComponente !== '' &&
    form.tipoComponente !== tipoSugerido;

  const submit = (e: FormEvent) => {
    e.preventDefault();
    onGuardar({
      nombre: form.nombre.trim(),
      descripcion: form.descripcion.trim() || undefined,
      categoria: form.categoria.trim(),
      precioVenta: Number(form.precioVenta),
      precioCompra: form.precioCompra === '' ? undefined : Number(form.precioCompra),
      codigoBarras: form.codigoBarras.trim() || undefined,
      sku: form.sku.trim() || undefined,
      marca: form.marca.trim() || undefined,
      modelo: form.modelo.trim() || undefined,
      tipoComponente: (form.tipoComponente || undefined) as TipoComponente | undefined,
      ...(esEdicion
        ? {
            stock: form.stock === '' ? undefined : Number(form.stock),
            stockMinimo: form.stockMinimo === '' ? undefined : Number(form.stockMinimo),
            estado: form.estado,
          }
        : {}),
    });
  };

  return (
    <Modal titulo={titulo} textoAccion="Guardar" cargandoAccion={guardando} onCerrar={onCerrar} onAccion={() => document.getElementById('producto-form-submit')?.click()}>
      {error && <p className="mb-space-md rounded bg-error-container/50 px-space-sm py-space-xs text-body-md text-on-error-container">{error}</p>}
      <form onSubmit={submit} className="grid grid-cols-1 gap-space-md md:grid-cols-2">
        <Input id="pf-nombre" etiqueta="Nombre *" value={form.nombre} onChange={set('nombre')} required />
        <div>
          <label className="flex flex-col gap-space-xs" htmlFor="pf-categoria">
            <span className="text-label-md">Categoria *</span>
            <input
              id="pf-categoria"
              list="pf-categorias"
              className="h-8 w-full rounded border border-outline-variant px-space-sm text-body-md focus:border-primary-container focus:outline-none"
              value={form.categoria}
              onChange={set('categoria')}
              required
            />
          </label>
          <datalist id="pf-categorias">
            {categoriasExistentes.map((c) => (
              <option key={c} value={c} />
            ))}
          </datalist>
        </div>
        <Input id="pf-pventa" etiqueta="Precio venta *" type="number" min="0" step="0.01" value={form.precioVenta} onChange={set('precioVenta')} required />
        <Input id="pf-pcompra" etiqueta="Precio compra" type="number" min="0" step="0.01" value={form.precioCompra} onChange={set('precioCompra')} />
        <Input id="pf-sku" etiqueta="SKU (unico)" value={form.sku} onChange={set('sku')} />
        <Input id="pf-marca" etiqueta="Marca" value={form.marca} onChange={set('marca')} />
        <Input id="pf-modelo" etiqueta="Modelo" value={form.modelo} onChange={set('modelo')} />
        <Input id="pf-barras" etiqueta="Codigo de barras" value={form.codigoBarras} onChange={set('codigoBarras')} />
        <Select id="pf-tipo" etiqueta="Tipo de componente" value={form.tipoComponente} onChange={set('tipoComponente')}>
          <option value="">—</option>
          {TIPOS_COMPONENTE.map((t) => (
            <option key={t} value={t}>{t}</option>
          ))}
        </Select>
        <Input id="pf-desc" etiqueta="Descripcion" value={form.descripcion} onChange={set('descripcion')} />

        {desajuste && (
          <p className="col-span-full rounded border border-[#fde68a] bg-[#fffbeb] px-space-sm py-space-xs text-body-sm text-[#b45309]">
            La categoria <strong>{form.categoria}</strong> sugiere tipo <strong>{tipoSugerido}</strong>,
            pero elegiste <strong>{form.tipoComponente}</strong>. Verifica que sea correcto.
          </p>
        )}

        {esEdicion && (
          <>
            <Input id="pf-stock" etiqueta="Stock" type="number" min="0" step="1" value={form.stock} onChange={set('stock')} />
            <Input id="pf-smin" etiqueta="Stock minimo" type="number" min="0" step="1" value={form.stockMinimo} onChange={set('stockMinimo')} />
            <Select id="pf-estado" etiqueta="Estado" value={form.estado} onChange={set('estado')}>
              <option value="ACTIVO">ACTIVO</option>
              <option value="INACTIVO">INACTIVO</option>
              <option value="DESCONTINUADO">DESCONTINUADO</option>
            </Select>
          </>
        )}
        <button id="producto-form-submit" type="submit" className="hidden">Guardar</button>
      </form>
    </Modal>
  );
}

export function formError(err: unknown): string {
  return mensajeError(err, 'No se pudo guardar el producto.');
}
import { useEffect, useRef, useState } from 'react';
import { Modal } from '../components/Modal';
import { Input } from '../components/Fields';
import { DataTable } from '../components/DataTable';
import { Badge, tonoEstadoProducto } from '../components/Badge';
import { AccionBtn } from '../components/AccionBtn';
import { buscarProductos } from '../api/productos';
import { mensajeError } from '../api/client';
import type { Producto } from '../api/types';

interface BuscarProductoModalProps {
  readonly onCerrar: () => void;
}

const MIN_CARACTERES = 2;

/** Consulta puntual de un producto por nombre o SKU. Vive aparte del listado
 *  paginado para no alterar el filtro por categoria de la tabla. */
export function BuscarProductoModal({ onCerrar }: BuscarProductoModalProps) {
  const [q, setQ] = useState('');
  const [resultados, setResultados] = useState<Producto[]>([]);
  const [seleccionado, setSeleccionado] = useState<Producto | null>(null);
  const [buscando, setBuscando] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const peticionRef = useRef(0);

  useEffect(() => {
    const texto = q.trim();
    if (texto.length < MIN_CARACTERES) {
      setResultados([]);
      setSeleccionado(null);
      setError(null);
      setBuscando(false);
      return;
    }

    const id = ++peticionRef.current;
    setBuscando(true);
    setError(null);
    const temporizador = setTimeout(() => {
      buscarProductos(texto)
        .then((data) => {
          if (id !== peticionRef.current) return;
          setResultados(data);
          setSeleccionado(data.length === 1 ? data[0] : null);
        })
        .catch((err) => {
          if (id !== peticionRef.current) return;
          setResultados([]);
          setSeleccionado(null);
          setError(mensajeError(err, 'No se pudo buscar productos.'));
        })
        .finally(() => {
          if (id === peticionRef.current) setBuscando(false);
        });
    }, 300);

    return () => clearTimeout(temporizador);
  }, [q]);

  const verResultados = () => setSeleccionado(null);

  return (
    <Modal titulo="Buscar producto" onCerrar={onCerrar}>
      <div className="flex flex-col gap-space-md">
        <Input
          id="buscar-producto"
          etiqueta="Nombre o SKU"
          placeholder="Ej. CPU-I3-12100F o teclado…"
          value={q}
          onChange={(e) => setQ(e.target.value)}
          autoFocus
        />

        {q.trim().length < MIN_CARACTERES && (
          <p className="text-body-md text-on-surface-variant">
            Escribe al menos {MIN_CARACTERES} caracteres para buscar.
          </p>
        )}

        {buscando && (
          <p className="text-body-md text-on-surface-variant">Buscando…</p>
        )}

        {!buscando && error && (
          <p className="rounded bg-error-container/50 px-space-sm py-space-xs text-body-md text-on-error-container">
            {error}
          </p>
        )}

        {!buscando && !error && q.trim().length >= MIN_CARACTERES && resultados.length === 0 && (
          <p className="text-body-md text-on-surface-variant">
            No se encontró ningún producto con ese nombre o SKU.
          </p>
        )}

        {!buscando && !error && seleccionado && (
          <div className="flex flex-col gap-space-md">
            <DetalleProducto producto={seleccionado} />
            {resultados.length > 1 && (
              <div className="flex justify-end border-t border-outline-variant pt-space-md">
                <AccionBtn variante="neutra" onClick={verResultados}>
                  Volver a resultados
                </AccionBtn>
              </div>
            )}
          </div>
        )}

        {!buscando && !error && !seleccionado && resultados.length > 0 && (
          <div className="flex flex-col gap-space-xs">
            <p className="text-label-md text-on-surface-variant">
              {resultados.length} {resultados.length === 1 ? 'resultado' : 'resultados'}
            </p>
            <DataTable
              columnas={[
                { titulo: 'SKU', nowrap: true },
                { titulo: 'Nombre' },
                { titulo: 'Marca / Modelo' },
                { titulo: 'Stock / Min', alinear: 'right', nowrap: true },
                { titulo: 'Estado', alinear: 'center', nowrap: true },
                { titulo: 'Acción', nowrap: true },
              ]}
              filas={resultados.map((p) => [
                <span className="font-code-tabular text-[0.8125rem] text-secondary">{p.sku || '—'}</span>,
                <span className="font-medium">{p.nombre}</span>,
                <span className="text-on-surface-variant">
                  {p.marca || '—'}
                  {p.modelo ? ` / ${p.modelo}` : ''}
                </span>,
                <span className={p.stockMinimo != null && p.stock <= p.stockMinimo ? 'font-medium text-error' : ''}>
                  {p.stock} / {p.stockMinimo ?? '—'}
                </span>,
                <Badge texto={p.estado ?? '—'} tono={tonoEstadoProducto(p.estado)} />,
                <AccionBtn variante="primaria" onClick={() => setSeleccionado(p)}>
                  Ver
                </AccionBtn>,
              ])}
              vacio="Sin resultados."
            />
          </div>
        )}
      </div>
    </Modal>
  );
}

function DetalleProducto({ producto }: { readonly producto: Producto }) {
  return (
    <>
      <div className="flex items-center gap-space-sm">
        <span className="font-code-tabular text-body-md text-secondary">{producto.sku || '—'}</span>
        <Badge texto={producto.tipoComponente ?? '—'} tono="info" />
        <Badge texto={producto.estado ?? '—'} tono={tonoEstadoProducto(producto.estado)} />
      </div>
      <dl className="grid grid-cols-1 gap-space-sm text-body-md md:grid-cols-2">
        <DetalleCampo etiqueta="Nombre" valor={producto.nombre} />
        <DetalleCampo etiqueta="Categoria" valor={producto.categoria} />
        <DetalleCampo etiqueta="Marca / Modelo" valor={`${producto.marca || '—'} / ${producto.modelo || '—'}`} />
        <DetalleCampo etiqueta="Codigo de barras" valor={producto.codigoBarras || '—'} />
        <DetalleCampo etiqueta="Precio compra / venta" valor={`S/ ${producto.precioCompra ?? 0} / S/ ${producto.precioVenta}`} />
        <DetalleCampo etiqueta="Stock / minimo" valor={`${producto.stock} / ${producto.stockMinimo ?? '—'}`} />
        <DetalleCampo etiqueta="Descripcion" valor={producto.descripcion || '—'} />
      </dl>
    </>
  );
}

function DetalleCampo({ etiqueta, valor }: { readonly etiqueta: string; readonly valor: string }) {
  return (
    <div>
      <dt className="text-label-md text-on-surface-variant">{etiqueta}</dt>
      <dd>{valor}</dd>
    </div>
  );
}

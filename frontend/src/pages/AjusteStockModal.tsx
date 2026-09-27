import { useState } from 'react';
import type { FormEvent } from 'react';
import type { Producto } from '../api/types';
import { Modal } from '../components/Modal';
import { Input } from '../components/Fields';

interface AjusteStockModalProps {
  readonly producto: Producto;
  readonly guardando: boolean;
  readonly error: string | null;
  readonly onAjustar: (cantidad: number, motivo: string) => void;
  readonly onCerrar: () => void;
}

/** PATCH /productos/{id}/stock?cantidad=&motivo= : cantidad puede ser
 *  positiva (entrada) o negativa (salida). */
export function AjusteStockModal({ producto, guardando, error, onAjustar, onCerrar }: AjusteStockModalProps) {
  const [cantidad, setCantidad] = useState('');
  const [motivo, setMotivo] = useState('');

  const submit = (e: FormEvent) => {
    e.preventDefault();
    onAjustar(Number(cantidad), motivo.trim());
  };

  return (
    <Modal titulo={`Ajustar stock — ${producto.nombre}`} textoAccion="Aplicar ajuste" cargandoAccion={guardando} onCerrar={onCerrar} onAccion={() => document.getElementById('ajuste-form-submit')?.click()}>
      {error && <p className="mb-space-md rounded bg-error-container/50 px-space-sm py-space-xs text-body-md text-on-error-container">{error}</p>}
      <p className="mb-space-md text-body-md text-on-surface-variant tabular">
        Stock actual: <strong className="text-on-surface">{producto.stock}</strong> · usa +N para entrada, −N para salida.
      </p>
      <form onSubmit={submit} className="flex flex-col gap-space-md">
        <Input id="aj-cantidad" etiqueta="Cantidad (+/-) *" type="number" step="1" value={cantidad} onChange={(e) => setCantidad(e.target.value)} required />
        <Input id="aj-motivo" etiqueta="Motivo *" placeholder="ej. conteo fisico, merma, correccion" value={motivo} onChange={(e) => setMotivo(e.target.value)} required />
        <button id="ajuste-form-submit" type="submit" className="hidden">Aplicar</button>
      </form>
    </Modal>
  );
}

import { useEffect, useState } from 'react';
import type { Producto, TipoEspecificacion } from '../api/types';
import { ETIQUETAS_TIPO } from '../api/especificaciones';
import {
  crearEspecificacion,
  actualizarEspecificacion,
  getEspecificacion,
} from '../api/especificaciones';
import { Modal } from '../components/Modal';
import { Button } from '../components/Button';
import { EspecificacionDetalle } from '../components/EspecificacionDetalle';
import { EspecificacionForm } from '../components/EspecificacionForm';
import { mensajeError } from '../api/client';

interface EspecificacionModalProps {
  readonly producto: Producto;
  readonly onCerrar: () => void;
  readonly onGuardado: () => void;
}

type Modo = 'cargando' | 'ver' | 'editar';

/** Modal unico que adapta ver/editar/crear segun el estado de la spec.
 *  - GET al montar: 404 -> modo 'editar' (crear); 200 -> modo 'ver'.
 *  - Boton "Editar" -> modo 'editar' con datos precargados.
 *  - Guardar -> POST si no existia, PUT si existia. */
export function EspecificacionModal({ producto, onCerrar, onGuardado }: EspecificacionModalProps) {
  const tipo = producto.tipoComponente as TipoEspecificacion | null;
  const [modo, setModo] = useState<Modo>('cargando');
  const [spec, setSpec] = useState<Record<string, unknown> | null>(null);
  const [existia, setExistia] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [guardando, setGuardando] = useState(false);

  useEffect(() => {
    if (!tipo) {
      setModo('editar');
      setSpec({});
      setExistia(false);
      return;
    }
    let cancelado = false;
    (async () => {
      try {
        const data = await getEspecificacion<Record<string, unknown>>(producto.id, tipo);
        if (cancelado) return;
        setSpec(data);
        setExistia(true);
        setModo('ver');
      } catch {
        if (cancelado) return;
        setSpec({});
        setExistia(false);
        setModo('editar');
      }
    })();
    return () => { cancelado = true; };
  }, [producto.id, tipo]);

  const guardar = async (datos: Record<string, unknown>) => {
    if (!tipo) return;
    setGuardando(true);
    setError(null);
    try {
      const payload = { productoId: producto.id, ...datos };
      if (existia) {
        await actualizarEspecificacion(producto.id, tipo, payload);
      } else {
        await crearEspecificacion(producto.id, tipo, payload);
      }
      onGuardado();
    } catch (err) {
      setError(mensajeError(err, 'No se pudo guardar la especificacion.'));
    } finally {
      setGuardando(false);
    }
  };

  const titulo = tipo
    ? `Especificacion — ${ETIQUETAS_TIPO[tipo]} — ${producto.nombre}`
    : 'Especificacion';

  if (!tipo) {
    return (
      <Modal titulo="Especificacion" onCerrar={onCerrar}>
        <p className="text-body-md text-on-surface-variant">
          Este producto no tiene un tipo de componente asignado. Edita el producto
          y asignale un tipo (CPU, GPU, RAM, etc.) para poder registrar su especificacion.
        </p>
      </Modal>
    );
  }

  return (
    <Modal titulo={titulo} onCerrar={onCerrar}>
      {modo === 'cargando' && (
        <p className="text-body-md text-on-surface-variant">Cargando especificacion…</p>
      )}

      {modo === 'ver' && spec && (
        <div className="flex flex-col gap-space-md">
          <EspecificacionDetalle tipo={tipo} spec={spec} />
          <div className="flex justify-end gap-space-sm border-t border-outline-variant pt-space-md">
            <Button variante="secondary" onClick={onCerrar}>Cerrar</Button>
            <Button onClick={() => setModo('editar')}>Editar</Button>
          </div>
        </div>
      )}

      {modo === 'editar' && (
        <EspecificacionForm
          tipo={tipo}
          inicial={spec ?? {}}
          guardando={guardando}
          error={error}
          onGuardar={(d) => void guardar(d)}
          onCancelar={onCerrar}
        />
      )}
    </Modal>
  );
}
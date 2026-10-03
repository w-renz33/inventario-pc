import { useState } from 'react';
import type { FormEvent } from 'react';
import type { TipoEspecificacion } from '../api/types-especificaciones';
import { CAMPOS_POR_TIPO } from '../api/especificaciones';
import { Input } from './Fields';
import { Button } from './Button';

interface EspecificacionFormProps {
  readonly tipo: TipoEspecificacion;
  /** Datos iniciales (si es edicion) o {} (si es creacion). */
  readonly inicial: Record<string, unknown>;
  readonly guardando: boolean;
  readonly error: string | null;
  readonly onGuardar: (datos: Record<string, unknown>) => void;
  readonly onCancelar: () => void;
}

/** Formulario dinamico: renderiza los campos declarados en CAMPOS_POR_TIPO
 *  segun el tipo de componente. Todo con useState plano (sin RHF). */
export function EspecificacionForm({
  tipo, inicial, guardando, error, onGuardar, onCancelar,
}: EspecificacionFormProps) {
  const campos = CAMPOS_POR_TIPO[tipo];
  const [valores, setValores] = useState<Record<string, string>>(() => {
    const out: Record<string, string> = {};
    for (const c of campos) {
      const v = inicial[c.key];
      out[c.key] = v === null || v === undefined ? '' : String(v);
    }
    return out;
  });
  const [errorLocal, setErrorLocal] = useState<string | null>(null);

  const set = (key: string, value: string) =>
    setValores((v) => ({ ...v, [key]: value }));

  const submit = (e: FormEvent) => {
    e.preventDefault();
    setErrorLocal(null);

    const datos: Record<string, unknown> = {};
    for (const c of campos) {
      const raw = valores[c.key] ?? '';
      if (c.requerido && raw.trim() === '') {
        setErrorLocal(`El campo "${c.etiqueta}" es obligatorio.`);
        return;
      }
      if (c.tipo === 'boolean') {
        datos[c.key] = raw === 'true';
      } else if (c.tipo === 'number') {
        datos[c.key] = raw === '' ? null : Number.parseInt(raw, 10);
      } else if (c.tipo === 'decimal') {
        datos[c.key] = raw === '' ? null : Number.parseFloat(raw);
      } else {
        datos[c.key] = raw.trim() === '' ? null : raw.trim();
      }
    }
    onGuardar(datos);
  };

  const mensaje = errorLocal ?? error;

  return (
    <form onSubmit={submit} className="flex flex-col gap-space-md">
      {mensaje && (
        <p className="rounded bg-error-container/50 px-space-sm py-space-xs text-body-md text-on-error-container">
          {mensaje}
        </p>
      )}

      <div className="grid grid-cols-1 gap-space-md md:grid-cols-2">
        {campos.map((c) => (
          <CampoDinamico
            key={c.key}
            campo={c}
            valor={valores[c.key] ?? ''}
            onChange={(v) => set(c.key, v)}
          />
        ))}
      </div>

      <div className="flex justify-end gap-space-sm border-t border-outline-variant pt-space-md">
        <Button variante="secondary" onClick={onCancelar}>Cancelar</Button>
        <Button type="submit" disabled={guardando}>
          {guardando ? 'Guardando…' : 'Guardar especificacion'}
        </Button>
      </div>
    </form>
  );
}

interface CampoDinamicoProps {
  readonly campo: { key: string; etiqueta: string; tipo: string; requerido?: boolean; placeholder?: string };
  readonly valor: string;
  readonly onChange: (v: string) => void;
}

function CampoDinamico({ campo, valor, onChange }: CampoDinamicoProps) {
  const id = `spec-${campo.key}`;
  const etiqueta = campo.requerido ? `${campo.etiqueta} *` : campo.etiqueta;

  if (campo.tipo === 'boolean') {
    return (
      <label className="flex items-center gap-space-sm self-end pb-space-sm text-body-md" htmlFor={id}>
        <input
          id={id}
          type="checkbox"
          checked={valor === 'true'}
          onChange={(e) => onChange(e.target.checked ? 'true' : 'false')}
          className="h-4 w-4"
        />
        {etiqueta}
      </label>
    );
  }

  return (
    <Input
      id={id}
      etiqueta={etiqueta}
      type={campo.tipo === 'text' ? 'text' : 'number'}
      step={campo.tipo === 'decimal' ? '0.01' : campo.tipo === 'number' ? '1' : undefined}
      placeholder={campo.placeholder}
      value={valor}
      onChange={(e) => onChange(e.target.value)}
      required={campo.requerido}
    />
  );
}
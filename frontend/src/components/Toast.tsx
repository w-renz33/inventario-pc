import { createContext, useCallback, useContext, useEffect, useMemo, useRef, useState } from 'react';
import type { ReactNode } from 'react';
import { Icon } from './Icon';

type TipoToast = 'success' | 'error' | 'info';

interface Aviso {
  id: number;
  tipo: TipoToast;
  mensaje: string;
  saliendo?: boolean;
}

interface ToastContextValue {
  exito: (mensaje: string) => void;
  info: (mensaje: string) => void;
  error: (mensaje: string) => void;
  cerrar: (id: number) => void;
}

const ToastContext = createContext<ToastContextValue | null>(null);

const DURACION: Record<TipoToast, number> = {
  success: 3500,
  info: 3500,
  error: 6000,
};

const MAX_AVISOS = 3;
const SALIDA_MS = 160;

const ESTILOS: Record<TipoToast, { contenedor: string; icono: 'check' | 'info' | 'alerta' }> = {
  success: {
    contenedor: 'border-[#a7f3d0] bg-[#ecfdf5] text-[#047857]',
    icono: 'check',
  },
  info: {
    contenedor: 'border-secondary-fixed-dim bg-secondary-fixed text-on-secondary-fixed',
    icono: 'info',
  },
  error: {
    contenedor: 'border-error/30 bg-error-container text-on-error-container',
    icono: 'alerta',
  },
};

interface ToastProviderProps {
  readonly children: ReactNode;
}

/** Avisos efimeros abajo a la derecha. El provider va por encima del Router
 *  para que un toast sobreviva a la navegacion (login, logout, recargas). */
export function ToastProvider({ children }: ToastProviderProps) {
  const [avisos, setAvisos] = useState<Aviso[]>([]);
  const siguienteId = useRef(1);
  const timers = useRef(new Map<number, ReturnType<typeof setTimeout>>());
  const vencimientos = useRef(new Map<number, number>());
  const restantes = useRef(new Map<number, number>());
  const saliendo = useRef(new Set<number>());

  const limpiarTimer = useCallback((id: number) => {
    const timer = timers.current.get(id);
    if (timer !== undefined) {
      clearTimeout(timer);
      timers.current.delete(id);
    }
  }, []);

  const quitar = useCallback((id: number) => {
    setAvisos((prev) => prev.filter((a) => a.id !== id));
    timers.current.delete(id);
    vencimientos.current.delete(id);
    restantes.current.delete(id);
    saliendo.current.delete(id);
  }, []);

  const iniciarSalida = useCallback((id: number) => {
    if (saliendo.current.has(id)) return;
    saliendo.current.add(id);
    limpiarTimer(id);
    setAvisos((prev) => prev.map((a) => (a.id === id ? { ...a, saliendo: true } : a)));
    const timer = setTimeout(() => quitar(id), SALIDA_MS);
    timers.current.set(id, timer);
  }, [limpiarTimer, quitar]);

  const programar = useCallback((id: number, ms: number) => {
    limpiarTimer(id);
    vencimientos.current.set(id, Date.now() + ms);
    const timer = setTimeout(() => iniciarSalida(id), ms);
    timers.current.set(id, timer);
  }, [iniciarSalida, limpiarTimer]);

  const pausar = useCallback((id: number) => {
    if (saliendo.current.has(id)) return;
    const vence = vencimientos.current.get(id);
    if (vence === undefined) return;
    restantes.current.set(id, Math.max(0, vence - Date.now()));
    limpiarTimer(id);
    vencimientos.current.delete(id);
  }, [limpiarTimer]);

  const reanudar = useCallback((id: number, tipo: TipoToast) => {
    if (timers.current.has(id) || saliendo.current.has(id)) return;
    const ms = restantes.current.get(id) ?? DURACION[tipo];
    restantes.current.delete(id);
    programar(id, ms);
  }, [programar]);

  const mostrar = useCallback((mensaje: string, tipo: TipoToast) => {
    const id = siguienteId.current++;
    setAvisos((prev) => [...prev, { id, tipo, mensaje }].slice(-MAX_AVISOS));
    programar(id, DURACION[tipo]);
  }, [programar]);

  const exito = useCallback((mensaje: string) => mostrar(mensaje, 'success'), [mostrar]);
  const info = useCallback((mensaje: string) => mostrar(mensaje, 'info'), [mostrar]);
  const error = useCallback((mensaje: string) => mostrar(mensaje, 'error'), [mostrar]);
  const cerrar = useCallback((id: number) => iniciarSalida(id), [iniciarSalida]);

  const valor = useMemo<ToastContextValue>(
    () => ({ exito, info, error, cerrar }),
    [exito, info, error, cerrar],
  );

  useEffect(() => {
    const mapa = timers.current;
    return () => {
      mapa.forEach((timer) => clearTimeout(timer));
      mapa.clear();
    };
  }, []);

  return (
    <ToastContext.Provider value={valor}>
      {children}
      <div
        className="pointer-events-none fixed inset-x-0 top-20 z-[100] flex flex-col items-center gap-space-sm p-space-md"
        aria-live="polite"
        aria-atomic="false"
      >
        {avisos.map((aviso) => {
          const estilo = ESTILOS[aviso.tipo];
          return (
            <div
              key={aviso.id}
              role={aviso.tipo === 'error' ? 'alert' : undefined}
              onMouseEnter={() => pausar(aviso.id)}
              onMouseLeave={() => reanudar(aviso.id, aviso.tipo)}
              onFocusCapture={() => pausar(aviso.id)}
              onBlurCapture={(e) => {
                const destino = e.relatedTarget;
                if (!(destino instanceof Node) || !e.currentTarget.contains(destino)) {
                  reanudar(aviso.id, aviso.tipo);
                }
              }}
              className={`pointer-events-auto flex w-full max-w-[calc(100vw-2rem)] items-start gap-space-sm rounded border px-space-md py-space-sm shadow-lg motion-reduce:animate-none sm:w-96 ${
                aviso.saliendo ? 'animate-toast-out' : 'animate-toast-in'
              } ${estilo.contenedor}`}
            >
              <Icon nombre={estilo.icono} className="mt-0.5 h-4 w-4 shrink-0" />
              <p className="flex-1 text-body-md">{aviso.mensaje}</p>
              <button
                type="button"
                onClick={() => cerrar(aviso.id)}
                aria-label="Cerrar notificación"
                className="shrink-0 rounded p-space-xs opacity-70 transition-opacity hover:opacity-100 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-1 focus-visible:outline-current"
              >
                <Icon nombre="cerrar" className="h-4 w-4" />
              </button>
            </div>
          );
        })}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast(): ToastContextValue {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error('useToast debe usarse dentro de <ToastProvider>');
  return ctx;
}

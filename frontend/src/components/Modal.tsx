import type { ReactNode } from 'react';
import { Button } from './Button';

interface ModalProps {
  readonly titulo: string;
  readonly children: ReactNode;
  readonly textoAccion?: string;
  readonly onAccion?: () => void;
  readonly cargandoAccion?: boolean;
  readonly onCerrar: () => void;
}

/** Dialogo centrado con header sticky y footer de acciones. */
export function Modal({ titulo, children, textoAccion, onAccion, cargandoAccion, onCerrar }: ModalProps) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-tertiary/40 p-space-md" role="dialog" aria-modal="true">
      <div className="max-h-[calc(100vh-160px)] w-full max-w-[640px] overflow-y-auto rounded-lg border border-outline-variant bg-surface-container-lowest shadow-xl">
        <div className="sticky top-0 border-b border-outline-variant bg-surface-container-lowest px-space-lg py-space-md">
          <h2 className="text-headline-sm">{titulo}</h2>
        </div>
        <div className="px-space-lg py-space-md">{children}</div>
        <div className="flex justify-end gap-space-sm border-t border-outline-variant px-space-lg py-space-md">
          <Button variante="secondary" onClick={onCerrar}>Cancelar</Button>
          {textoAccion && onAccion && (
            <Button onClick={onAccion} disabled={cargandoAccion}>
              {cargandoAccion ? 'Guardando…' : textoAccion}
            </Button>
          )}
        </div>
      </div>
    </div>
  );
}

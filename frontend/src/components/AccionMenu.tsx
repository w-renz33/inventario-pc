import { useEffect, useRef, useState } from 'react';

export interface AccionMenuItem {
  readonly etiqueta: string;
  readonly onSelect: () => void;
  readonly variante?: 'neutra' | 'destructiva';
  readonly title?: string;
}

interface AccionMenuProps {
  /** Etiqueta accesible del boton, p.ej. "Acciones de Procesador Intel Core i3". */
  readonly ariaLabel: string;
  readonly items: readonly AccionMenuItem[];
  /** Texto del gatillo. Por defecto "⋯". */
  readonly etiqueta?: string;
}

/**
 * Menu de acciones desplegable para las celdas de tabla donde no caben todos
 * los botones en linea.
 *
 * <p>Comportamiento de accesibilidad:
 * - el gatillo expone aria-haspopup/aria-expanded;
 * - Escape cierra y devuelve el foco al gatillo;
 * - clic fuera cierra sin robar el foco;
 * - las flechas arriba/abajo mueven el foco entre items y Enter activa;
 * - al elegir un item el menu se cierra y el foco vuelve al gatillo.
 */
export function AccionMenu({ ariaLabel, items, etiqueta = '⋯' }: AccionMenuProps) {
  const [abierto, setAbierto] = useState(false);
  const contenedorRef = useRef<HTMLDivElement>(null);
  const gatilloRef = useRef<HTMLButtonElement>(null);
  const itemRefs = useRef<(HTMLButtonElement | null)[]>([]);

  // Clic fuera y Escape. Se escucha en la fase de captura para ganar al click
  // que abrio el menu.
  useEffect(() => {
    if (!abierto) return;

    const alClicFuera = (e: MouseEvent) => {
      if (!contenedorRef.current?.contains(e.target as Node)) {
        setAbierto(false);
      }
    };
    const alEscape = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        setAbierto(false);
        gatilloRef.current?.focus();
      }
    };

    document.addEventListener('mousedown', alClicFuera);
    document.addEventListener('keydown', alEscape);
    return () => {
      document.removeEventListener('mousedown', alClicFuera);
      document.removeEventListener('keydown', alEscape);
    };
  }, [abierto]);

  // Al abrir, el foco entra al primer item.
  useEffect(() => {
    if (abierto) {
      itemRefs.current[0]?.focus();
    }
  }, [abierto]);

  const moverFoco = (e: React.KeyboardEvent, indice: number) => {
    if (e.key !== 'ArrowDown' && e.key !== 'ArrowUp') return;
    e.preventDefault();
    const siguiente = e.key === 'ArrowDown' ? indice + 1 : indice - 1;
    // El foco cicla: del ultimo al primero y del primero al ultimo.
    const destino = (siguiente + items.length) % items.length;
    itemRefs.current[destino]?.focus();
  };

  if (items.length === 0) return null;

  return (
    <div className="relative inline-block" ref={contenedorRef}>
      <button
        ref={gatilloRef}
        type="button"
        aria-haspopup="menu"
        aria-expanded={abierto}
        aria-label={ariaLabel}
        onClick={() => setAbierto((v) => !v)}
        onKeyDown={(e) => {
          if (e.key === 'ArrowDown') {
            e.preventDefault();
            setAbierto(true);
          }
        }}
        className="inline-flex h-7 min-w-7 items-center justify-center rounded px-space-xs text-body-md text-on-surface-variant transition-colors hover:bg-surface-container-low focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-1 focus-visible:outline-primary"
      >
        {etiqueta}
      </button>

      {abierto && (
        <div
          role="menu"
          aria-label={ariaLabel}
          className="absolute right-0 top-full z-20 mt-space-xs w-max min-w-44 rounded-lg border border-outline-variant bg-surface-container-lowest py-space-xs shadow-lg"
        >
          {items.map((item, i) => (
            <button
              key={item.etiqueta}
              ref={(el) => {
                itemRefs.current[i] = el;
              }}
              type="button"
              role="menuitem"
              title={item.title}
              onClick={() => {
                setAbierto(false);
                item.onSelect();
              }}
              onKeyDown={(e) => moverFoco(e, i)}
              className={`block w-full px-space-md py-space-xs text-left text-body-md transition-colors focus:outline-none focus-visible:bg-surface-container-low ${
                item.variante === 'destructiva'
                  ? 'text-error hover:bg-error-container/50'
                  : 'text-on-surface hover:bg-surface-container-low'
              }`}
            >
              {item.etiqueta}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

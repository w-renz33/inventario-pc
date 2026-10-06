import type { ButtonHTMLAttributes } from 'react';

/**
 * Boton de accion dentro de una fila de tabla.
 *
 * <p>Es mas chico que <Button> (h-7 contra h-8) porque conviven hasta cinco
 * en la misma celda: a tamano de formulario se verian desproporcionados y
 * harian la fila mas alta que el resto de la tabla.
 *
 * <p>La jerarquia se apoya en el relleno, no solo en el color del texto:
 * - primaria: la accion que se espera usar, con fondo.
 * - neutra: acciones secundarias, con borde y fondo solo al pasar el mouse.
 * - destructiva: dar de baja, anular, eliminar. Fondo rojo suave.
 */
type Variante = 'primaria' | 'neutra' | 'destructiva';

interface AccionBtnProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  readonly variante?: Variante;
}

const ESTILOS: Record<Variante, string> = {
  primaria: 'bg-secondary-fixed text-on-secondary-fixed hover:bg-secondary-fixed-dim',
  neutra:
    'border border-outline-variant bg-surface-container-lowest text-on-surface hover:bg-surface-container-low',
  destructiva: 'bg-error-container text-on-error-container hover:bg-error-container/70',
};

/** Enlace de foco visible, coherente con el de Button. */
const FOCO = 'focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-1 focus-visible:outline-primary';

export function AccionBtn({ variante = 'neutra', className = '', type, ...rest }: AccionBtnProps) {
  return (
    <button
      // Por defecto type="button": dentro de un <form> un button sin type
      // actua como submit y cerraria el formulario al pulsar.
      type={type ?? 'button'}
      className={`inline-flex h-7 items-center rounded px-space-sm text-body-sm font-medium whitespace-nowrap transition-colors disabled:cursor-not-allowed disabled:opacity-50 ${ESTILOS[variante]} ${FOCO} ${className}`}
      {...rest}
    />
  );
}

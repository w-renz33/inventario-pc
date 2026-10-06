import type { ButtonHTMLAttributes } from 'react';

type Variante = 'primary' | 'secondary' | 'danger' | 'ghost';

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  readonly variante?: Variante;
}

const ESTILOS: Record<Variante, string> = {
  primary: 'bg-primary-container text-on-primary hover:bg-primary',
  secondary: 'border border-outline-variant bg-surface-container-lowest text-on-surface hover:bg-surface-container-low',
  danger: 'border border-error/40 text-error hover:bg-error-container',
  ghost: 'text-primary-container hover:bg-secondary-fixed',
};

/** Boton 32px del design system (primario #1e40af). */
export function Button({ variante = 'primary', className = '', type, ...rest }: ButtonProps) {
  return (
    <button
      // type por defecto "button": un <button> sin type dentro de un <form>
      // se vuelve submit y cerraria el formulario al pulsarlo.
      type={type ?? 'button'}
      className={`h-8 rounded px-space-md text-body-md font-medium transition-colors focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-1 focus-visible:outline-primary disabled:cursor-not-allowed disabled:opacity-50 ${ESTILOS[variante]} ${className}`}
      {...rest}
    />
  );
}

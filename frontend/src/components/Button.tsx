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
export function Button({ variante = 'primary', className = '', ...rest }: ButtonProps) {
  return (
    <button
      type="button"
      className={`h-8 rounded px-space-md text-body-md font-medium transition-colors disabled:cursor-not-allowed disabled:opacity-50 ${ESTILOS[variante]} ${className}`}
      {...rest}
    />
  );
}

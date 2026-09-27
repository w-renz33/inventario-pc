import type { InputHTMLAttributes, SelectHTMLAttributes } from 'react';

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  readonly etiqueta: string;
}

const CAMPO =
  'h-8 w-full rounded border border-outline-variant bg-surface-container-lowest px-space-sm text-body-md text-on-surface placeholder:text-on-surface-variant/50 focus:border-primary-container focus:outline-none focus:ring-1 focus:ring-primary-container';

/** Campo 32px con etiqueta (borde #cbd5e1, foco #1e40af). */
export function Input({ etiqueta, id, ...rest }: InputProps) {
  return (
    <label className="flex flex-col gap-space-xs" htmlFor={id}>
      <span className="text-label-md text-on-surface">{etiqueta}</span>
      <input id={id} className={CAMPO} {...rest} />
    </label>
  );
}

interface SelectProps extends SelectHTMLAttributes<HTMLSelectElement> {
  readonly etiqueta: string;
}

export function Select({ etiqueta, id, children, ...rest }: SelectProps) {
  return (
    <label className="flex flex-col gap-space-xs" htmlFor={id}>
      <span className="text-label-md text-on-surface">{etiqueta}</span>
      <select id={id} className={CAMPO} {...rest}>
        {children}
      </select>
    </label>
  );
}

import { Button } from './Button';

interface EmptyStateProps {
  readonly mensaje: string;
}

export function EmptyState({ mensaje }: EmptyStateProps) {
  return <p className="py-space-xl text-center text-body-md text-on-surface-variant">{mensaje}</p>;
}

interface ErrorStateProps {
  readonly mensaje: string;
  readonly onReintentar?: () => void;
}

/** Banner de error con reintento (401/403/500 del backend). */
export function ErrorState({ mensaje, onReintentar }: ErrorStateProps) {
  return (
    <div className="flex items-center gap-space-md rounded border border-error/30 bg-error-container/40 px-space-md py-space-sm text-body-md text-on-error-container">
      <span className="flex-1">{mensaje}</span>
      {onReintentar && (
        <Button variante="secondary" onClick={onReintentar}>Reintentar</Button>
      )}
    </div>
  );
}

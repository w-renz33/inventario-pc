import { useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { mensajeError } from '../api/client';
import { Icon } from './Icon';
import { useState } from 'react';

interface TopAppBarProps {
  readonly titulo: string;
  readonly subtitulo?: string;
}

/** Barra superior: titulo, chip de usuario+rol y Salir. */
export function TopAppBar({ titulo, subtitulo }: TopAppBarProps) {
  const { user, salir } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);

  const onSalir = async () => {
    try {
      await salir();
      navigate('/login');
    } catch (err) {
      setError(mensajeError(err, 'No se pudo cerrar sesion.'));
    }
  };

  return (
    <header className="flex items-center gap-space-md border-b border-outline-variant bg-surface-container-lowest px-margin py-space-md dark:border-inverse-surface dark:bg-inverse-surface">
      <div className="min-w-0">
        <h1 className="truncate text-headline-md">{titulo}</h1>
        {subtitulo && <p className="text-body-sm text-on-surface-variant">{subtitulo}</p>}
      </div>
      <div className="ml-auto flex items-center gap-space-sm">
        {error && <span className="text-body-sm text-error">{error}</span>}
        {user && (
          <span className="rounded bg-secondary-fixed px-space-sm py-space-xs text-label-sm text-on-secondary-fixed">
            {user.username} · {(user.roles[0] ?? '—').toUpperCase()}
          </span>
        )}
        <button
          type="button"
          onClick={() => void onSalir()}
          className="flex items-center gap-space-xs rounded border border-error/40 px-space-md py-space-sm text-body-md text-error hover:bg-error-container"
        >
          <Icon nombre="salir" className="h-4 w-4" />
          Salir
        </button>
      </div>
    </header>
  );
}

import { useState } from 'react';
import type { FormEvent } from 'react';
import { Navigate, useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { mensajeError } from '../api/client';
import { Button } from '../components/Button';
import { Input } from '../components/Fields';

interface LoginPageProps {
  readonly appNombre?: string;
}

/** Screen 1 Stitch: card centrada. Podado: sin "recordar sesion"
 *  (la sesion la maneja el servidor) y sin "olvido su contrasena"
 *  (no hay endpoint de recovery). */
export function LoginPage({ appNombre = 'CiberTecnologias SAC' }: LoginPageProps) {
  const { user, entrar } = useAuth();
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(
    params.get('expirada') ? 'Sesion expirada. Ingresa nuevamente.' : null,
  );
  const [cargando, setCargando] = useState(false);

  if (user) return <Navigate to="/dashboard" replace />;

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setCargando(true);
    try {
      await entrar(username.trim(), password);
      navigate('/dashboard');
    } catch (err) {
      setError(mensajeError(err, 'Credenciales incorrectas o sesion expirada.'));
    } finally {
      setCargando(false);
    }
  };

  return (
    <main className="flex min-h-screen items-center justify-center bg-surface-container-low p-space-md dark:bg-inverse-surface">
      <div className="w-full max-w-[480px]">
        <div className="rounded-xl border border-outline-variant bg-surface-container-lowest shadow-xl">
          <div className="h-1 rounded-t-xl bg-primary-container" />
          <div className="px-space-xl py-space-lg">
            <h1 className="text-center text-headline-lg text-primary-container">{appNombre} <span className="text-body-sm font-normal">ERP</span></h1>
            <p className="mt-space-xs text-center text-body-sm text-on-surface-variant">
              Gestión y Control de Inventario
            </p>

            {error && (
              <div className="mt-space-md rounded border border-error/30 bg-error-container/50 px-space-md py-space-sm text-body-md text-on-error-container" role="alert">
                <p className="font-medium">Acceso denegado</p>
                <p>{error}</p>
              </div>
            )}

            <form onSubmit={(e) => void onSubmit(e)} className="mt-space-md flex flex-col gap-space-md">
              <Input
                id="username"
                etiqueta="Usuario institucional"
                placeholder="ej. admin"
                autoComplete="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
              <Input
                id="password"
                etiqueta="Contrasena"
                type="password"
                autoComplete="current-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
              <Button type="submit" disabled={cargando} className="h-10 w-full">
                {cargando ? 'Ingresando…' : 'Iniciar Sesion'}
              </Button>
            </form>

          </div>
        </div>
        <p className="mt-space-sm text-center text-body-sm text-on-surface-variant">
          © 2026 CiberTecnologias SAC. Todos los derechos reservados.
        </p>
      </div>
    </main>
  );
}

import { Navigate } from 'react-router-dom';
import type { ReactNode } from 'react';
import { useAuth } from './AuthContext';

interface ProtectedRouteProps {
  readonly children: ReactNode;
}

/** Guarda: sin sesion (/api/auth/me 401) -> /login. */
export function ProtectedRoute({ children }: ProtectedRouteProps) {
  const { user, cargando } = useAuth();
  if (cargando) return <p className="p-margin text-body-md">Verificando sesion…</p>;
  if (!user) return <Navigate to="/login" replace />;
  return <>{children}</>;
}

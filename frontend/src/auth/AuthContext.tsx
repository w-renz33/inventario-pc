import { createContext, useCallback, useContext, useEffect, useState } from 'react';
import type { ReactNode } from 'react';
import * as authApi from '../api/auth';
import type { LoginResponse } from '../api/types';

interface AuthContextValue {
  user: LoginResponse | null;
  cargando: boolean;
  entrar: (username: string, password: string) => Promise<void>;
  salir: () => Promise<void>;
  tienePermiso: (permiso: string) => boolean;
}

const AuthContext = createContext<AuthContextValue | null>(null);

interface AuthProviderProps {
  readonly children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<LoginResponse | null>(null);
  const [cargando, setCargando] = useState(true);

  useEffect(() => {
    authApi
      .me()
      .then(setUser)
      .catch(() => setUser(null))
      .finally(() => setCargando(false));
  }, []);

  const entrar = useCallback(async (username: string, password: string) => {
    setUser(await authApi.login(username, password));
  }, []);

  const salir = useCallback(async () => {
    try {
      await authApi.logout();
    } finally {
      setUser(null);
    }
  }, []);

  const tienePermiso = useCallback(
    (permiso: string) => user?.permisos.includes(permiso) ?? false,
    [user],
  );

  return (
    <AuthContext.Provider value={{ user, cargando, entrar, salir, tienePermiso }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth debe usarse dentro de <AuthProvider>');
  return ctx;
}

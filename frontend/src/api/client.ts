import axios from 'axios';

/** Cliente unico. Auth por SESIONES (JSESSIONID HttpOnly):
 *  withCredentials:true es obligatorio o el backend responde 401. */
export const api = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' },
});

/** Sesion expirada/invalida -> volver al login.
 *  El back responde 401 o 403 sin sesion; el 403 tambien significa
 *  "sin permiso", asi que solo redirige en /me o 401. */
api.interceptors.response.use(
  (res) => res,
  (err) => {
    const url = err.config?.url ?? '';
    const sinSesion =
      err.response?.status === 401 || (err.response?.status === 403 && url.includes('/api/auth/me'));
    if (sinSesion && window.location.pathname !== '/login') {
      window.location.href = '/login?expirada=1';
    }
    return Promise.reject(err);
  },
);

/** Mensaje legible para errores de negocio (hoy llegan como 500). */
export function mensajeError(err: unknown, fallback: string): string {
  if (axios.isAxiosError(err)) {
    const data = err.response?.data as { message?: string; mensaje?: string } | string | undefined;
    if (typeof data === 'string' && data.length > 0) return data;
    if (data && typeof data === 'object') return data.message ?? data.mensaje ?? fallback;
    if (err.response?.status === 403) return 'Sin permiso para esta accion.';
    if (err.response?.status === 400) return 'Datos invalidos. Revisa el formulario.';
  }
  return fallback;
}

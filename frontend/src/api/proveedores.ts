import { api } from './client';
import type { Proveedor } from './types';

export async function getProveedor(id: number): Promise<Proveedor> {
  const { data } = await api.get<Proveedor>(`/api/proveedores/${id}`);
  return data;
}

export async function crearProveedor(dto: Proveedor): Promise<Proveedor> {
  const { data } = await api.post<Proveedor>('/api/proveedores', dto);
  return data;
}

/** PUT reemplaza el objeto completo: mandar todos los campos. */
export async function actualizarProveedor(id: number, dto: Proveedor): Promise<Proveedor> {
  const { data } = await api.put<Proveedor>(`/api/proveedores/${id}`, dto);
  return data;
}

export async function eliminarProveedor(id: number): Promise<void> {
  await api.delete(`/api/proveedores/${id}`);
}

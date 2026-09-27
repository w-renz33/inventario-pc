import { api } from './client';
import type { Categoria, Proveedor } from './types';

export async function listarCategorias(): Promise<Categoria[]> {
  const { data } = await api.get<Categoria[]>('/api/categorias');
  return data;
}

export async function crearCategoria(nombre: string, descripcion?: string): Promise<Categoria> {
  const { data } = await api.post<Categoria>('/api/categorias', { nombre, descripcion });
  return data;
}

export async function listarProveedores(): Promise<Proveedor[]> {
  const { data } = await api.get<Proveedor[]>('/api/proveedores');
  return data;
}

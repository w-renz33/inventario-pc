import { api } from './client';
import type { Pagina, Producto, ProductoCreate } from './types';

export interface ProductoFiltro {
  page: number;
  size: number;
  /** Solo se envia si hay seleccion ("Todas" = omitir el param). */
  categoriaId?: number;
}

export async function listarProductos(f: ProductoFiltro): Promise<Pagina<Producto>> {
  const params: Record<string, number> = { page: f.page, size: f.size };
  if (f.categoriaId !== undefined) params.categoriaId = f.categoriaId;
  const { data } = await api.get<Pagina<Producto>>('/api/inventario/productos', { params });
  return data;
}

/** Para pickers (kardex, detalle de ingreso): el back topa size a 100. */
export async function listarProductosParaPicker(): Promise<Producto[]> {
  const pagina = await listarProductos({ page: 0, size: 100 });
  return pagina.content;
}

export async function getProducto(id: number): Promise<Producto> {
  const { data } = await api.get<Producto>(`/api/inventario/productos/${id}`);
  return data;
}

export async function crearProducto(dto: ProductoCreate): Promise<Producto> {
  const { data } = await api.post<Producto>('/api/inventario/productos', dto);
  return data;
}

export async function actualizarProducto(id: number, dto: Partial<Producto>): Promise<Producto> {
  const { data } = await api.patch<Producto>(`/api/inventario/productos/${id}`, dto);
  return data;
}

export async function eliminarProducto(id: number): Promise<void> {
  await api.delete(`/api/inventario/productos/${id}`);
}

export async function ajustarStock(id: number, cantidad: number, motivo: string): Promise<Producto> {
  const { data } = await api.patch<Producto>(`/api/inventario/productos/${id}/stock`, null, {
    params: { cantidad, motivo },
  });
  return data;
}

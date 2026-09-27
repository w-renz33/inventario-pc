import { api } from './client';
import type { Movimiento } from './types';

export async function listarMovimientos(): Promise<Movimiento[]> {
  const { data } = await api.get<Movimiento[]>('/api/inventario/movimientos');
  return data;
}

export async function listarMovimientosPorProducto(productoId: number): Promise<Movimiento[]> {
  const { data } = await api.get<Movimiento[]>(`/api/inventario/movimientos/productos/${productoId}`);
  return data;
}

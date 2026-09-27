import { api } from './client';
import type { Ingreso } from './types';

export async function listarIngresos(): Promise<Ingreso[]> {
  const { data } = await api.get<Ingreso[]>('/api/ingresos');
  return data;
}

export async function getIngreso(id: number): Promise<Ingreso> {
  const { data } = await api.get<Ingreso>(`/api/ingresos/${id}`);
  return data;
}

export async function registrarIngreso(dto: Ingreso): Promise<Ingreso> {
  const { data } = await api.post<Ingreso>('/api/ingresos', dto);
  return data;
}

export async function anularIngreso(id: number): Promise<void> {
  await api.post(`/api/ingresos/${id}/anular`);
}

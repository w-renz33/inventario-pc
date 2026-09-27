import { useEffect, useState } from 'react';
import { listarProductos } from '../api/productos';
import { listarIngresos } from '../api/ingresos';
import { listarMovimientos } from '../api/movimientos';
import type { Ingreso, Producto } from '../api/types';

export interface ResumenDashboard {
  totalProductos: number;
  muestraParcial: boolean;
  valorizado: number;
  criticos: Producto[];
  ingresosDocs: number;
  ingresosMonto: number;
  ingresosPorTipo: { tipo: string; n: number }[];
  ultimosIngresos: Ingreso[];
  entradas30d: number;
  salidas30d: number;
  topMovidos: { nombre: string; n: number }[];
}

const DIAS = 30;

/** Agregados calculados en memoria desde los endpoints existentes.
 *  Exactos mientras totalElements <= 100 (tope de size del back);
 *  si no, muestraParcial=true y la UI lo avisa en vez de mentir. */
export function useDashboard() {
  const [resumen, setResumen] = useState<ResumenDashboard | null>(null);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      try {
        const [pag, ings, movs] = await Promise.all([
          listarProductos({ page: 0, size: 100 }),
          listarIngresos(),
          listarMovimientos(),
        ]);
        const prods = pag.content;
        const valorizado = prods.reduce((a, p) => a + (p.stock ?? 0) * (p.precioCompra ?? 0), 0);
        const criticos = prods
          .filter((p) => p.stockMinimo != null && (p.stock ?? 0) <= p.stockMinimo)
          .sort((a, b) => (a.stock ?? 0) - (b.stock ?? 0));

        const ingresosMonto = ings.reduce((a, i) => a + (i.total ?? 0), 0);
        const porTipo = new Map<string, number>();
        for (const i of ings) porTipo.set(i.tipoDocumento, (porTipo.get(i.tipoDocumento) ?? 0) + 1);
        const ultimosIngresos = [...ings]
          .sort((a, b) => (b.fecha ?? '').localeCompare(a.fecha ?? ''))
          .slice(0, 5);

        const corte = new Date();
        corte.setDate(corte.getDate() - DIAS);
        let entradas30d = 0;
        let salidas30d = 0;
        const porProducto = new Map<string, number>();
        for (const m of movs) {
          porProducto.set(m.productoNombre, (porProducto.get(m.productoNombre) ?? 0) + 1);
          if ((m.fecha ?? '') < corte.toISOString()) continue;
          const tipo = m.tipoMovimiento ?? m.tipo;
          if (tipo === 'ENTRADA') entradas30d += m.cantidad;
          else if (tipo === 'SALIDA') salidas30d += m.cantidad;
        }
        const topMovidos = [...porProducto.entries()]
          .map(([nombre, n]) => ({ nombre, n }))
          .sort((a, b) => b.n - a.n)
          .slice(0, 5);

        setResumen({
          totalProductos: pag.totalElements,
          muestraParcial: prods.length < pag.totalElements,
          valorizado,
          criticos,
          ingresosDocs: ings.length,
          ingresosMonto,
          ingresosPorTipo: [...porTipo.entries()].map(([tipo, n]) => ({ tipo, n })),
          ultimosIngresos,
          entradas30d,
          salidas30d,
          topMovidos,
        });
      } catch {
        setError('No se pudo cargar el resumen.');
      } finally {
        setCargando(false);
      }
    })();
  }, []);

  return { resumen, cargando, error };
}

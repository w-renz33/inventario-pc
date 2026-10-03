import { api } from './client';
import type {
  EspecificacionCpu,
  EspecificacionGpu,
  EspecificacionRam,
  EspecificacionSsd,
  EspecificacionHdd,
  EspecificacionPlacaMadre,
  EspecificacionFuente,
  EspecificacionGabinete,
  TipoEspecificacion,
} from './types-especificaciones';

/** Ruta base por tipo: el backend expone un controller por tipo.
 *  El front usa este mapa para no hardcodear la URL en cada llamada. */
const RUTA: Record<TipoEspecificacion, string> = {
  CPU: 'cpu',
  GPU: 'gpu',
  RAM: 'ram',
  SSD: 'ssd',
  HDD: 'hdd',
  PLACA_MADRE: 'placa-madre',
  FUENTE: 'fuente',
  GABINETE: 'gabinete',
};

function base(productoId: number, tipo: TipoEspecificacion): string {
  return `/api/inventario/productos/${productoId}/especificacion/${RUTA[tipo]}`;
}

/** GET generico: devuelve el DTO del tipo pedido.
 *  Lanza 404 si el producto no tiene spec de ese tipo. */
export async function getEspecificacion<T>(
  productoId: number,
  tipo: TipoEspecificacion,
): Promise<T> {
  const { data } = await api.get<T>(base(productoId, tipo));
  return data;
}

/** POST: crear. Falla 500 si ya existe (RuntimeException del back). */
export async function crearEspecificacion<T>(
  productoId: number,
  tipo: TipoEspecificacion,
  dto: T,
): Promise<T> {
  const { data } = await api.post<T>(base(productoId, tipo), dto);
  return data;
}

/** PUT: actualizar. Falla 500 si no existe. */
export async function actualizarEspecificacion<T>(
  productoId: number,
  tipo: TipoEspecificacion,
  dto: T,
): Promise<T> {
  const { data } = await api.put<T>(base(productoId, tipo), dto);
  return data;
}

/** DELETE: eliminar la spec del producto. */
export async function eliminarEspecificacion(
  productoId: number,
  tipo: TipoEspecificacion,
): Promise<void> {
  await api.delete(base(productoId, tipo));
}

/* --------------------------------------------------------------------------
 * Envoltorios tipados por tipo. No son estrictamente necesarios (el front
 * puede usar las funciones genericas), pero dan autocompletado exacto
 * cuando se conoce el tipo en tiempo de compilacion.
 * ------------------------------------------------------------------------ */

export const cpuApi = {
  get: (id: number) => getEspecificacion<EspecificacionCpu>(id, 'CPU'),
  crear: (id: number, dto: EspecificacionCpu) => crearEspecificacion(id, 'CPU', dto),
  actualizar: (id: number, dto: EspecificacionCpu) => actualizarEspecificacion(id, 'CPU', dto),
  eliminar: (id: number) => eliminarEspecificacion(id, 'CPU'),
};

export const gpuApi = {
  get: (id: number) => getEspecificacion<EspecificacionGpu>(id, 'GPU'),
  crear: (id: number, dto: EspecificacionGpu) => crearEspecificacion(id, 'GPU', dto),
  actualizar: (id: number, dto: EspecificacionGpu) => actualizarEspecificacion(id, 'GPU', dto),
  eliminar: (id: number) => eliminarEspecificacion(id, 'GPU'),
};

export const ramApi = {
  get: (id: number) => getEspecificacion<EspecificacionRam>(id, 'RAM'),
  crear: (id: number, dto: EspecificacionRam) => crearEspecificacion(id, 'RAM', dto),
  actualizar: (id: number, dto: EspecificacionRam) => actualizarEspecificacion(id, 'RAM', dto),
  eliminar: (id: number) => eliminarEspecificacion(id, 'RAM'),
};

export const ssdApi = {
  get: (id: number) => getEspecificacion<EspecificacionSsd>(id, 'SSD'),
  crear: (id: number, dto: EspecificacionSsd) => crearEspecificacion(id, 'SSD', dto),
  actualizar: (id: number, dto: EspecificacionSsd) => actualizarEspecificacion(id, 'SSD', dto),
  eliminar: (id: number) => eliminarEspecificacion(id, 'SSD'),
};

export const hddApi = {
  get: (id: number) => getEspecificacion<EspecificacionHdd>(id, 'HDD'),
  crear: (id: number, dto: EspecificacionHdd) => crearEspecificacion(id, 'HDD', dto),
  actualizar: (id: number, dto: EspecificacionHdd) => actualizarEspecificacion(id, 'HDD', dto),
  eliminar: (id: number) => eliminarEspecificacion(id, 'HDD'),
};

export const placaMadreApi = {
  get: (id: number) => getEspecificacion<EspecificacionPlacaMadre>(id, 'PLACA_MADRE'),
  crear: (id: number, dto: EspecificacionPlacaMadre) => crearEspecificacion(id, 'PLACA_MADRE', dto),
  actualizar: (id: number, dto: EspecificacionPlacaMadre) => actualizarEspecificacion(id, 'PLACA_MADRE', dto),
  eliminar: (id: number) => eliminarEspecificacion(id, 'PLACA_MADRE'),
};

export const fuenteApi = {
  get: (id: number) => getEspecificacion<EspecificacionFuente>(id, 'FUENTE'),
  crear: (id: number, dto: EspecificacionFuente) => crearEspecificacion(id, 'FUENTE', dto),
  actualizar: (id: number, dto: EspecificacionFuente) => actualizarEspecificacion(id, 'FUENTE', dto),
  eliminar: (id: number) => eliminarEspecificacion(id, 'FUENTE'),
};

export const gabineteApi = {
  get: (id: number) => getEspecificacion<EspecificacionGabinete>(id, 'GABINETE'),
  crear: (id: number, dto: EspecificacionGabinete) => crearEspecificacion(id, 'GABINETE', dto),
  actualizar: (id: number, dto: EspecificacionGabinete) => actualizarEspecificacion(id, 'GABINETE', dto),
  eliminar: (id: number) => eliminarEspecificacion(id, 'GABINETE'),
};

/** Descriptor de campos por tipo: lo usa EspecificacionForm para renderizar
 *  el formulario dinamico. Cada campo declara tipo de input y etiqueta. */
export type TipoCampoForm = 'text' | 'number' | 'decimal' | 'boolean';

export interface CampoForm {
  readonly key: string;
  readonly etiqueta: string;
  readonly tipo: TipoCampoForm;
  readonly requerido?: boolean;
  readonly placeholder?: string;
}

export const CAMPOS_POR_TIPO: Record<TipoEspecificacion, readonly CampoForm[]> = {
  CPU: [
    { key: 'socket', etiqueta: 'Socket', tipo: 'text', requerido: true, placeholder: 'LGA1700' },
    { key: 'arquitectura', etiqueta: 'Arquitectura', tipo: 'text', placeholder: 'Raptor Lake' },
    { key: 'nucleos', etiqueta: 'Nucleos', tipo: 'number' },
    { key: 'hilos', etiqueta: 'Hilos', tipo: 'number' },
    { key: 'frecuenciaBaseGhz', etiqueta: 'Frecuencia base (GHz)', tipo: 'decimal' },
    { key: 'frecuenciaBoostGhz', etiqueta: 'Frecuencia boost (GHz)', tipo: 'decimal' },
    { key: 'cacheMb', etiqueta: 'Cache (MB)', tipo: 'number' },
    { key: 'consumoTdpW', etiqueta: 'TDP (W)', tipo: 'number' },
    { key: 'graficaIntegrada', etiqueta: 'Grafica integrada', tipo: 'boolean' },
  ],
  GPU: [
    { key: 'chip', etiqueta: 'Chip', tipo: 'text', requerido: true, placeholder: 'AD104' },
    { key: 'arquitectura', etiqueta: 'Arquitectura', tipo: 'text' },
    { key: 'vramGb', etiqueta: 'VRAM (GB)', tipo: 'number' },
    { key: 'tipoMemoria', etiqueta: 'Tipo de memoria', tipo: 'text', placeholder: 'GDDR6X' },
    { key: 'busMemoriaBits', etiqueta: 'Bus de memoria (bits)', tipo: 'number' },
    { key: 'frecuenciaBaseMhz', etiqueta: 'Frecuencia base (MHz)', tipo: 'number' },
    { key: 'frecuenciaBoostMhz', etiqueta: 'Frecuencia boost (MHz)', tipo: 'number' },
    { key: 'consumoTdpW', etiqueta: 'TDP (W)', tipo: 'number' },
    { key: 'interfaz', etiqueta: 'Interfaz', tipo: 'text', placeholder: 'PCIe 4.0 x16' },
    { key: 'hdmi', etiqueta: 'Puertos HDMI', tipo: 'number' },
    { key: 'displayport', etiqueta: 'Puertos DisplayPort', tipo: 'number' },
  ],
  RAM: [
    { key: 'capacidadGb', etiqueta: 'Capacidad (GB)', tipo: 'number', requerido: true },
    { key: 'tipoMemoria', etiqueta: 'Tipo de memoria', tipo: 'text', requerido: true, placeholder: 'DDR5' },
    { key: 'frecuenciaMhz', etiqueta: 'Frecuencia (MHz)', tipo: 'number' },
    { key: 'latencia', etiqueta: 'Latencia', tipo: 'text', placeholder: 'CL16' },
    { key: 'voltaje', etiqueta: 'Voltaje (V)', tipo: 'decimal' },
    { key: 'formato', etiqueta: 'Formato', tipo: 'text', placeholder: 'DIMM' },
    { key: 'ecc', etiqueta: 'ECC', tipo: 'boolean' },
  ],
  SSD: [
    { key: 'capacidadGb', etiqueta: 'Capacidad (GB)', tipo: 'number', requerido: true },
    { key: 'formato', etiqueta: 'Formato', tipo: 'text', placeholder: 'M.2 2280' },
    { key: 'interfaz', etiqueta: 'Interfaz', tipo: 'text', placeholder: 'NVMe PCIe 4.0' },
    { key: 'tipoNand', etiqueta: 'Tipo NAND', tipo: 'text', placeholder: 'TLC' },
    { key: 'velocidadLecturaMbps', etiqueta: 'Lectura (MB/s)', tipo: 'number' },
    { key: 'velocidadEscrituraMbps', etiqueta: 'Escritura (MB/s)', tipo: 'number' },
    { key: 'tbw', etiqueta: 'TBW', tipo: 'number' },
  ],
  HDD: [
    { key: 'capacidadGb', etiqueta: 'Capacidad (GB)', tipo: 'number', requerido: true },
    { key: 'formato', etiqueta: 'Formato', tipo: 'text', placeholder: '3.5"' },
    { key: 'interfaz', etiqueta: 'Interfaz', tipo: 'text', placeholder: 'SATA III' },
    { key: 'rpm', etiqueta: 'RPM', tipo: 'number' },
    { key: 'cacheMb', etiqueta: 'Cache (MB)', tipo: 'number' },
    { key: 'velocidadTransferenciaMbps', etiqueta: 'Transferencia (MB/s)', tipo: 'number' },
  ],
  PLACA_MADRE: [
    { key: 'socket', etiqueta: 'Socket', tipo: 'text', requerido: true, placeholder: 'AM5' },
    { key: 'chipset', etiqueta: 'Chipset', tipo: 'text', placeholder: 'B650' },
    { key: 'formato', etiqueta: 'Formato', tipo: 'text', placeholder: 'ATX' },
    { key: 'tipoMemoria', etiqueta: 'Tipo de memoria', tipo: 'text', placeholder: 'DDR5' },
    { key: 'slotsRam', etiqueta: 'Slots RAM', tipo: 'number' },
    { key: 'memoriaMaxGb', etiqueta: 'Memoria max (GB)', tipo: 'number' },
    { key: 'slotsM2', etiqueta: 'Slots M.2', tipo: 'number' },
    { key: 'slotsPcie', etiqueta: 'Slots PCIe', tipo: 'number' },
    { key: 'puertosSata', etiqueta: 'Puertos SATA', tipo: 'number' },
    { key: 'puertoLan', etiqueta: 'Puerto LAN', tipo: 'text', placeholder: '2.5 GbE' },
    { key: 'wifi', etiqueta: 'WiFi', tipo: 'boolean' },
    { key: 'bluetooth', etiqueta: 'Bluetooth', tipo: 'boolean' },
  ],
  FUENTE: [
    { key: 'potenciaW', etiqueta: 'Potencia (W)', tipo: 'number', requerido: true },
    { key: 'certificacion80Plus', etiqueta: 'Certificacion 80 Plus', tipo: 'text', placeholder: 'Gold' },
    { key: 'formato', etiqueta: 'Formato', tipo: 'text', placeholder: 'ATX' },
    { key: 'modularidad', etiqueta: 'Modularidad', tipo: 'text', placeholder: 'Full modular' },
    { key: 'ventiladorMm', etiqueta: 'Ventilador (mm)', tipo: 'number' },
    { key: 'conectorAtx', etiqueta: 'Conector ATX', tipo: 'text', placeholder: '24 pines' },
    { key: 'conectorCpu', etiqueta: 'Conector CPU', tipo: 'text', placeholder: '8+8 pines' },
    { key: 'conectorPcie', etiqueta: 'Conector PCIe', tipo: 'text', placeholder: '3x 8 pines' },
    { key: 'conectorSata', etiqueta: 'Conectores SATA', tipo: 'number' },
  ],
  GABINETE: [
    { key: 'formato', etiqueta: 'Formato', tipo: 'text', placeholder: 'Mid Tower' },
    { key: 'compatibilidadPlaca', etiqueta: 'Compatibilidad placa', tipo: 'text', placeholder: 'ATX, mATX, ITX' },
    { key: 'longitudGpuMaxMm', etiqueta: 'Longitud GPU max (mm)', tipo: 'number' },
    { key: 'alturaDisipadorMaxMm', etiqueta: 'Altura disipador max (mm)', tipo: 'number' },
    { key: 'fuenteSoportada', etiqueta: 'Fuente soportada', tipo: 'text', placeholder: 'ATX' },
    { key: 'ventiladoresIncluidos', etiqueta: 'Ventiladores incluidos', tipo: 'number' },
    { key: 'ventiladoresMax', etiqueta: 'Ventiladores max', tipo: 'number' },
    { key: 'radiadorMaxMm', etiqueta: 'Radiador max (mm)', tipo: 'number' },
    { key: 'bahias35', etiqueta: 'Bahias 3.5"', tipo: 'number' },
    { key: 'bahias25', etiqueta: 'Bahias 2.5"', tipo: 'number' },
    { key: 'puertosUsb', etiqueta: 'Puertos USB', tipo: 'number' },
    { key: 'puertoAudio', etiqueta: 'Puerto audio', tipo: 'number' },
  ],
};

/** Etiqueta legible por tipo (para el titulo del modal y badges). */
export const ETIQUETAS_TIPO: Record<TipoEspecificacion, string> = {
  CPU: 'Procesador (CPU)',
  GPU: 'Tarjeta grafica (GPU)',
  RAM: 'Memoria RAM',
  SSD: 'Unidad SSD',
  HDD: 'Disco duro (HDD)',
  PLACA_MADRE: 'Placa madre',
  FUENTE: 'Fuente de poder',
  GABINETE: 'Gabinete',
};
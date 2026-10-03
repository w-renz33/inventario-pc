/** Espejo 1:1 de los DTOs de especificacion del backend.
 *  Cada tipo de componente tiene su propia forma (no hay herencia en el back). */

export type TipoEspecificacion =
  | 'CPU'
  | 'GPU'
  | 'RAM'
  | 'SSD'
  | 'HDD'
  | 'PLACA_MADRE'
  | 'FUENTE'
  | 'GABINETE';

export interface EspecificacionCpu {
  productoId: number;
  socket: string;
  arquitectura: string | null;
  nucleos: number | null;
  hilos: number | null;
  frecuenciaBaseGhz: number | null;
  frecuenciaBoostGhz: number | null;
  cacheMb: number | null;
  consumoTdpW: number | null;
  graficaIntegrada: boolean | null;
}

export interface EspecificacionGpu {
  productoId: number;
  chip: string;
  arquitectura: string | null;
  vramGb: number | null;
  tipoMemoria: string | null;
  busMemoriaBits: number | null;
  frecuenciaBaseMhz: number | null;
  frecuenciaBoostMhz: number | null;
  consumoTdpW: number | null;
  interfaz: string | null;
  hdmi: number | null;
  displayport: number | null;
}

export interface EspecificacionRam {
  productoId: number;
  capacidadGb: number;
  tipoMemoria: string;
  frecuenciaMhz: number | null;
  latencia: string | null;
  voltaje: number | null;
  formato: string | null;
  ecc: boolean | null;
}

export interface EspecificacionSsd {
  productoId: number;
  capacidadGb: number;
  formato: string | null;
  interfaz: string | null;
  tipoNand: string | null;
  velocidadLecturaMbps: number | null;
  velocidadEscrituraMbps: number | null;
  tbw: number | null;
}

export interface EspecificacionHdd {
  productoId: number;
  capacidadGb: number;
  formato: string | null;
  interfaz: string | null;
  rpm: number | null;
  cacheMb: number | null;
  velocidadTransferenciaMbps: number | null;
}

export interface EspecificacionPlacaMadre {
  productoId: number;
  socket: string;
  chipset: string | null;
  formato: string | null;
  tipoMemoria: string | null;
  slotsRam: number | null;
  memoriaMaxGb: number | null;
  slotsM2: number | null;
  slotsPcie: number | null;
  puertosSata: number | null;
  puertoLan: string | null;
  wifi: boolean | null;
  bluetooth: boolean | null;
}

export interface EspecificacionFuente {
  productoId: number;
  potenciaW: number;
  certificacion80Plus: string | null;
  formato: string | null;
  modularidad: string | null;
  ventiladorMm: number | null;
  conectorAtx: string | null;
  conectorCpu: string | null;
  conectorPcie: string | null;
  conectorSata: number | null;
}

export interface EspecificacionGabinete {
  productoId: number;
  formato: string | null;
  compatibilidadPlaca: string | null;
  longitudGpuMaxMm: number | null;
  alturaDisipadorMaxMm: number | null;
  fuenteSoportada: string | null;
  ventiladoresIncluidos: number | null;
  ventiladoresMax: number | null;
  radiadorMaxMm: number | null;
  bahias35: number | null;
  bahias25: number | null;
  puertosUsb: number | null;
  puertoAudio: number | null;
}

/** Union discriminada por tipoComponente: el front puede hacer switch
 *  y TS estrecha el tipo automaticamente. */
export type Especificacion =
  | { tipo: 'CPU'; data: EspecificacionCpu }
  | { tipo: 'GPU'; data: EspecificacionGpu }
  | { tipo: 'RAM'; data: EspecificacionRam }
  | { tipo: 'SSD'; data: EspecificacionSsd }
  | { tipo: 'HDD'; data: EspecificacionHdd }
  | { tipo: 'PLACA_MADRE'; data: EspecificacionPlacaMadre }
  | { tipo: 'FUENTE'; data: EspecificacionFuente }
  | { tipo: 'GABINETE'; data: EspecificacionGabinete };
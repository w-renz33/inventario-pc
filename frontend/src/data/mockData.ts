import type { TipoComponente, TipoDocumento } from '../api/types';

/** Listas fijas del dominio (enums del backend). Sin hardcodeo en componentes. */
export const TIPOS_COMPONENTE: TipoComponente[] = [
  'CPU', 'GPU', 'RAM', 'SSD', 'HDD', 'PLACA_MADRE', 'FUENTE', 'GABINETE',
];

export const TIPOS_DOCUMENTO: TipoDocumento[] = [
  'FACTURA', 'BOLETA', 'GUIA', 'NOTA_CREDITO', 'OTRO',
];

/** IGV peruano aplicado por el backend al registrar ingresos. */
export const IGV_TASA = 0.18;

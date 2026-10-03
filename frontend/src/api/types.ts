/** Espejo 1:1 de los DTOs del backend (com.sistema.inventario.*.dto).
 * Si el backend cambia un campo, TypeScript avisa en compilacion. */
export type {
  TipoEspecificacion,
  EspecificacionCpu,
  EspecificacionGpu,
  EspecificacionRam,
  EspecificacionSsd,
  EspecificacionHdd,
  EspecificacionPlacaMadre,
  EspecificacionFuente,
  EspecificacionGabinete,
  Especificacion,
} from './types-especificaciones';



export type TipoComponente =
  | 'CPU' | 'GPU' | 'RAM' | 'SSD' | 'HDD'
  | 'PLACA_MADRE' | 'FUENTE' | 'GABINETE';

export type EstadoProducto = 'ACTIVO' | 'INACTIVO' | 'DESCONTINUADO';

export type TipoDocumento =
  | 'FACTURA' | 'BOLETA' | 'GUIA' | 'NOTA_CREDITO' | 'OTRO';

export type EstadoIngreso = 'REGISTRADO' | 'ANULADO';

export type TipoMovimiento = 'ENTRADA' | 'SALIDA' | 'AJUSTE';

export interface LoginResponse {
  username: string;
  roles: string[];
  permisos: string[];
}

/** com.sistema.inventario.common.Pagina<T> */
export interface Pagina<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
}

export interface Producto {
  id: number;
  nombre: string;
  descripcion: string;
  categoria: string;
  categoriaId: number | null;
  precioVenta: number;
  precioCompra: number;
  stock: number;
  stockMinimo: number;
  codigoBarras: string;
  sku: string;
  marca: string;
  modelo: string;
  tipoComponente: TipoComponente | null;
  estado: EstadoProducto | null;
  tieneEspecificacion: boolean;
}

export interface ProductoCreate {
  nombre: string;
  descripcion?: string;
  categoria: string;
  precioVenta: number;
  precioCompra?: number;
  codigoBarras?: string;
  sku?: string;
  marca?: string;
  modelo?: string;
  tipoComponente?: TipoComponente;
}

export interface Categoria {
  id: number;
  nombre: string;
  descripcion: string;
}

export interface Proveedor {
  id?: number;
  nombre: string;
  ruc: string;
  telefono?: string;
  email?: string;
  direccion?: string;
  contactoNombre?: string;
  contactoTelefono?: string;
  activo?: boolean;
}

export interface IngresoDetalle {
  id?: number;
  productoId: number;
  productoNombre?: string;
  cantidad: number;
  precioUnitario: number;
  subtotal?: number;
}

export interface Ingreso {
  id?: number;
  tipoDocumento: TipoDocumento;
  numeroDocumento: string;
  fecha: string; // LocalDate serializado como "YYYY-MM-DD"
  proveedorId: number;
  proveedorNombre?: string;
  subtotal?: number;
  igv?: number;
  total?: number;
  observacion?: string;
  estado?: EstadoIngreso;
  detalles: IngresoDetalle[];
}

export interface Movimiento {
  id: number;
  productoId: number;
  productoNombre: string;
  tipoMovimiento: TipoMovimiento;
  tipo: string;
  motivo: string;
  cantidad: number;
  stockAnterior: number;
  stockNuevo: number;
  costoUnitario: number | null;
  referenciaTipo: string;
  referenciaId: number | null;
  usuarioUsername: string;
  fecha: string;
  observacion: string;
  nota: string;
}

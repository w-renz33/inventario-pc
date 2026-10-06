# Inventario CiberTecnologias

Sistema de inventario de componentes PC (CPU, GPU, RAM, SSD, HDD, placa madre, fuente, gabinete)
con ingresos de mercadería, kardex de movimientos, proveedores y panel general.

```text
inventario/
├── backend/    # Spring Boot + Java 21 + MySQL  (puerta 8080)
├── frontend/   # Vite + React + TypeScript      (puerta 5173)
└── README.md   # esta guía
```

> Esta guía es para **Windows sin WSL**, con tu propio MySQL ya instalado.
> Sigue los pasos **en orden** y no te saltes ninguno.

## 1. Instalar programas (una sola vez)

| Programa | Q
|---|---|
| Java JDK **21** |
| Node.js **22 LTS** 


Después de instalar, abre una terminal (`cmd`) y comprueba:

```bat
java -version
node --version
```

Lo primero debe decir `21.x` y lo segundo `v22.x`.
Si `java` no aparece, reinstala el JDK 21 marcando la opción de agregarlo al PATH.

## 2. Tu base de datos (2 pasos)

### 2.1. Crea la base en tu MySQL

Abre MySQL Workbench (o la consola de MySQL) y ejecuta:

```sql
CREATE DATABASE mibase;
```

> Cambia `mibase` por el nombre que quieras. Apúntalo, lo usarás abajo.
> Ejemplo: `CREATE DATABASE inventario_grupo3;`

### 2.2. Pon ese nombre y tu contraseña en el backend

Abre con el Bloc de notas este archivo:

```text
backend\src\main\resources\application.properties
```

Busca estas **2 líneas** (están al inicio del archivo):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/inventario_pc?useSSL=false&serverTimezone=America/Lima&allowPublicKeyRetrieval=true
spring.datasource.password=admin123
```

Y cámbialas así:

- Donde dice `inventario_pc`, escribe el **nombre de TU base** (el del paso 2.1).
  No borres nada más de esa línea larga, solo el nombre.
- Donde dice `admin123`, escribe **TU contraseña** de MySQL (la de tu usuario `root`).

Ejemplo, si tu base se llama `inventario_grupo3` y tu clave es `clave123`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/inventario_grupo3?useSSL=false&serverTimezone=America/Lima&allowPublicKeyRetrieval=true
spring.datasource.password=clave123
```

Guarda el archivo. Eso es todo lo que hay que configurar.

> Solo si tu usuario de MySQL **no** se llama `root`, cambia también esta línea:
> `spring.datasource.username=root` → pon tu usuario.

## 3. Levantar el backend (terminal 1)

Abre una terminal en la carpeta `backend` y ejecuta:

```bat
cd backend
mvnw.cmd spring-boot:run
```

- La **primera vez** tarda unos minutos descargando cosas (solo una vez).
- Las tablas se crean solas y se cargan los usuarios de prueba.
- Sabrás que está listo cuando veas algo como `Tomcat started on port 8080`.

**No cierres esta ventana** mientras uses el sistema.

## 4. Levantar el frontend (terminal 2, otra ventana)

Abre **otra** terminal en la carpeta `frontend` y ejecuta:

```bat
cd frontend
corepack enable
pnpm install
pnpm run dev
```

- `corepack enable` es solo la primera vez (si da error, abre la terminal
  como administrador, ejecútalo y vuelve a tu terminal normal).
- `pnpm install` es solo la primera vez.
- Abre en el navegador **exactamente** esta dirección: http://localhost:5173

## 5. Entrar al sistema

| Usuario | Clave | Rol | Puede hacer |
|---|---|---|---|
| `admin` | `admin123` | `ADMIN` | Todo, incluido ver usuarios |
| `jefe` | `jefe123` | `JEFE_ALMACEN` | Catálogo, ingresos, ajuste de stock, categorías |
| `inventario` | `inv123` | `AUXILIAR_ALMACEN` | Ver productos y registrar ingresos |

Los tres usuarios y sus roles los crea `DataSeeder` en cada arranque.
Es idempotente: no duplica nada y no pisa los datos ya sembrados. Las
categorías no se siembran, se crean desde la pantalla de Productos.

## 6. Comprueba que todo funciona (2 minutos)

1. Entra con `admin` / `admin123` → debes ver el **Panel general** con cifras.
2. Ve a **Productos** → cambia de página y filtra por categoría.
3. Crea un producto de prueba (+ Nuevo Producto).
4. Ve a **Ingresos** → registra un ingreso con ese producto y guárdalo.
5. Ve a **Kardex** → elige ese producto: debe aparecer el movimiento de ENTRADA.
6. Repite el paso 3 pero entrando con `inventario` / `inv123`: el botón
   **+ Nuevo Producto** no debe aparecer (ese rol solo registra ingresos).
7. Pulsa **Salir** → vuelves al login.

## 7. Apagar

Pulsa `Ctrl + C` en cada una de las dos terminales.

## 8. Si algo falla

| Lo que ves | Qué hacer |
|---|---|
| En el backend: `Access denied for user 'root'` | La contraseña del paso 2.2 no coincide con tu MySQL. Revísala letra por letra. |
| En el backend: `Unknown database '...'` | El nombre del paso 2.2 no coincide con la base que creaste en 2.1. Deben ser **iguales**. |
| `Port 8080 is already in use` | El backend ya está corriendo en otra ventana. Ciérrala. |
| `Port 5173 is already in use` | El frontend ya está corriendo en otra ventana. Ciérrala. |
| `pnpm no se reconoce` | Ejecuta `corepack enable` y reabre la terminal. |
| El login rebota o dice sesión expirada | Vuelve a entrar con usuario y clave. Entra siempre por http://localhost:5173 |
| `java no se reconoce` o versión distinta de 21 | Reinstala el JDK 21 y verifica con `java -version`. |

## 9. Notas (para cuando quieras saber más)

- Comandos del frontend: `pnpm run dev` (usar), `pnpm run build` (probar que compila).
- El login funciona con sesiones del servidor; no hay que copiar ningún token.
- Los errores de negocio (stock insuficiente, documento duplicado, etc.) aparecen
  como mensajes en pantalla: léelos, dicen la causa.
- Documentación interactiva de la API (Swagger UI): http://localhost:8080/swagger-ui.html
- Dar de baja un producto no lo borra: pasa a estado `DESCONTINUADO` y sigue
  visible en el catálogo con su historial de kardex intacto. Es a propósito, para
  no romper la trazabilidad de los ingresos y movimientos que lo referencian.

## 10. Pantallas del sistema

Referencia de qué contiene cada pantalla y qué puede hacer el usuario en ella.
Los permisos son los mismos que devuelve `/api/auth/me` (campo `permisos`).

### Panel general (`/dashboard`)

| Aspecto | Detalle |
|---|---|
| Elementos | 4 tarjetas KPI (valorizado, stock crítico, ingresos registrados, movimientos 30 días), tabla de últimos ingresos, tabla de stock crítico |
| Interacciones | enlaces de navegación a Productos, Ingresos y Kardex |
| Visible para | los 3 roles |
| Origen de datos | los mismos endpoints de catálogo, ingresos y kardex; los agregados se calculan en el navegador |

### Productos (`/productos`)

| Aspecto | Detalle |
|---|---|
| Elementos | tabla de 8 columnas, filtro por categoría, selector de filas por página, 5 modales (alta/edición, ajuste de stock, detalle, baja, categoría) |
| Interacciones | ver detalle, editar, ajustar stock, registrar o completar especificación, dar de baja, crear categoría |
| `ADMIN` | todo lo anterior |
| `JEFE_ALMACEN` | todo lo anterior |
| `AUXILIAR_ALMACEN` | solo ver detalle y consultar especificación; sin alta, edición, ajuste ni baja |

### Ingresos (`/ingresos`)

| Aspecto | Detalle |
|---|---|
| Elementos | formulario de 2 pasos (cabecera del documento, detalle de productos), historial con 3 filtros (texto, tipo, estado), modal de anulación |
| Interacciones | registrar ingreso, ver detalle, anular ingreso |
| Visible para | los 3 roles; el botón de registrar requiere `ingreso.registrar` |

La anulación es la única acción destructiva y revierte el stock. Si no hay stock
suficiente para revertir, el backend la rechaza con el detalle del faltante.

### Kardex (`/kardex`)

| Aspecto | Detalle |
|---|---|
| Elementos | selector de alcance (por producto / todos), filtros de tipo y rango de fechas, 4 tarjetas de resumen, tabla de movimientos |
| Interacciones | consultar movimientos de un producto o de todo el catálogo |
| Visible para | los 3 roles |

### Proveedores (`/proveedores`)

| Aspecto | Detalle |
|---|---|
| Elementos | tabla de 7 columnas, filtro de texto, 3 modales (alta/edición, detalle, baja) |
| Interacciones | ver detalle, editar, eliminar |
| Visible para | los 3 roles |

### Especificaciones

No es una pantalla propia: es un modal que se abre desde la fila del producto.
Muestra los campos del tipo correspondiente (socket, núcleos, VRAM, capacidad…)
en solo lectura o en edición. Un producto solo admite un tipo de componente y
una sola especificación de ese tipo.

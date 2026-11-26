# Parfums House – Sistema de Perfumería (JavaFX + SQLite + DAO)

Proyecto de POO con la misma estructura del proyecto del profesor, aplicado a una **perfumería**:
`Cliente`, `Producto` (perfume), `Venta` y `DetalleVenta`.

## Estructura

```
perfumeria/
├── pom.xml
├── db/
│   ├── perfumeria.db        ← base de datos (la que abres en DB Browser)
│   └── schema.sql           ← script de las tablas (opcional)
└── src/main/
    ├── java/
    │   ├── module-info.java
    │   └── pe/edu/upeu/perfumeria/
    │       ├── App.java · Launcher.java
    │       ├── controller/      Main, Home, Cliente(+Form), Producto(+Form), Venta
    │       ├── modelo/          Persona ← Cliente · Producto · Venta · DetalleVenta
    │       ├── interfaces/      IClienteDao · IProductoDao · IVentaDao
    │       ├── implementacion/  ClienteDaoImpl · ProductoDaoImpl · VentaDaoImpl
    │       ├── utils/           ConexionDB · UtilAlerts · UtilTabla
    │       └── test/            TestConexion · TestDatos
    └── resources/pe/edu/upeu/perfumeria/view/   *.fxml + style.css
```

Flujo: `FXML → Controller → interfaz DAO → DaoImpl → ConexionDB → db/perfumeria.db`

## Ejecutar en IntelliJ IDEA

1. **File → Open…** y elige la carpeta del proyecto (la que tiene `pom.xml`). Espera a que Maven termine de importar.
2. Abre `App.java` y pulsa ▶ junto a `main` (o ejecuta `Launcher`).
3. La primera vez se crea sola la carpeta/archivo `db/perfumeria.db` con sus 4 tablas.
   En la pantalla de **Inicio** aparece la ruta exacta de la base de datos en uso.
4. Opcional: ejecuta `TestDatos` para cargar clientes, perfumes y una venta de ejemplo
   (no duplica si ya hay datos). `TestConexion` comprueba la conexión y lista las tablas.

Por Maven: `mvn clean javafx:run`

> La base se busca en `db/perfumeria.db` **relativo a la carpeta del proyecto**. Si IntelliJ la creara en otro lugar,
> la ruta real se imprime en la consola y en la pantalla Inicio. Para fijar otra ruta:
> *Run → Edit Configurations → VM options*: `-Dperfumeria.db=C:/ruta/perfumeria.db`

## Conectar con DB Browser for SQLite

1. Abre DB Browser → **File → Open Database** (Ctrl+O) → elige `db/perfumeria.db` del proyecto
   (o la ruta que muestra la pantalla Inicio). Si no ves el archivo, cambia el filtro a *All files (\*)*.
2. **Database Structure**: verás `clientes`, `productos`, `ventas`, `detalle_ventas`.
   **Browse Data**: ves y editas los registros que crea la aplicación.
3. Si cambias algo en DB Browser pulsa **Write Changes (Ctrl+S)**. Mientras haya cambios sin guardar,
   DB Browser mantiene la base bloqueada y la aplicación esperará 5 s y luego mostrará un error.
4. Si DB Browser dice *"attempt to write a readonly database"*: ciérrala y ábrela con **Open Database**
   (no "Read Only") y revisa que `perfumeria.db` y su carpeta no estén marcados como solo lectura.

## Conectar la base desde IntelliJ IDEA

- **IntelliJ IDEA Ultimate** (gratis con la licencia de estudiante): *View → Tool Windows → Database* →
  **+ → Data Source → SQLite** → en *File* elige `db/perfumeria.db` → **Download missing driver files** →
  **Test Connection** → **OK**. Ya puedes ver tablas, datos y ejecutar consultas dentro de IntelliJ.
- **IntelliJ IDEA Community**: no incluye la ventana *Database*. Usa DB Browser (arriba) o instala desde
  *Settings → Plugins* el plugin **Database Navigator**.

## Reglas implementadas

- DNI de 8 dígitos y único; no se puede eliminar un cliente o perfume que ya tiene ventas (llaves foráneas activas).
- La venta guarda cabecera + detalle + descuento de stock en **una transacción**: si no alcanza el stock se revierte todo.
- Perfumes con stock ≤ 5 se muestran en rojo; solo se venden perfumes con stock.


## Login y roles

| Usuario | Contraseña | Rol | Acceso |
|---|---|---|---|
| `admin` | `admin123` | ADMIN | Todo: ventas, perfumes (CRUD), clientes, **usuarios** |
| `vendedor` | `vendedor123` | VENDEDOR | Ventas, clientes y consulta de perfumes (sin editar ni usuarios) |

Se crean solos la primera vez (tabla `usuarios`, contraseñas con hash SHA-256 + sal). Cámbielas desde *Usuarios*.

## Generar el instalador .exe

1. Instale **JDK 21** y **WiX Toolset 3.x** (`candle.exe` y `light.exe` en el PATH).
2. Ejecute `build-exe.bat` → resultado en `dist\ParfumsHouse-1.0.0.exe`.
3. Instalado, la base queda en `C:\Users\<usuario>\ParfumsHouse\perfumeria.db` (ábrala con DB Browser).
   En IntelliJ sigue usando `db/perfumeria.db` del proyecto.

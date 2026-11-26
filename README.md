[readme_parfums_house.md](https://github.com/user-attachments/files/35445063/readme_parfums_house.md)

# Parfums House - Sistema de Gestión de Perfumería

Sistema de escritorio para la gestión de inventario, clientes y ventas de una perfumería, desarrollado en JavaFX y SQLite.

---

## Tecnologías

| Pieza | Versión |
|---|---|
| Java (JDK) | 21 |
| JavaFX (controls + fxml) | 21.0.6 |
| SQLite JDBC (`sqlite-jdbc`) | 3.42.0.0 |
| Maven | wrapper incluido (`mvnw`) |
| Empaquetado | `jpackage` + WiX Toolset 3.x |
| Editor de la base de datos | DB Browser for SQLite |

---

## Estructura del proyecto

```text
perfumeria/
├── pom.xml
├── build-exe.bat              ← genera el instalador .exe
├── mvnw / mvnw.cmd / .mvn/    ← Maven wrapper
├── db/
│   ├── perfumeria.db          ← base de datos de desarrollo (se abre con DB Browser)
│   └── schema.sql             ← script de las tablas (opcional)
└── src/main/
    ├── java/
    │   ├── module-info.java
    │   └── pe/edu/upeu/perfumeria/
    │       ├── App.java · Launcher.java
    │       ├── controller/       Login, Main, Home, Cliente(+Form), Producto(+Form), Venta, Usuario
    │       ├── modelo/           Persona ← Cliente · Usuario | Producto · Venta · DetalleVenta · UserSession
    │       ├── interfaces/       IClienteDao · IProductoDao · IVentaDao · IUsuarioDao
    │       ├── implementacion/   ClienteDaoImpl · ProductoDaoImpl · VentaDaoImpl · UsuarioDaoImpl
    │       ├── utils/            ConexionDB · PasswordUtil · UtilAlerts · UtilTabla
    │       └── test/             TestConexion · TestDatos
    └── resources/pe/edu/upeu/perfumeria/
        ├── view/                 *.fxml + style.css
        └── img/                  logo.png · logo.ico · bienvenida.png
```

### Arquitectura y flujo

El flujo de datos en la aplicación es siempre el mismo:

```text
FXML  →  Controller  →  Interfaz DAO  →  DaoImpl  →  ConexionDB  →  SQLite
```

Cada pantalla es un archivo `.fxml` con su respectivo controller. Los controllers no tocan SQL: le piden los datos a un DAO a través de su interfaz. Esto permite cambiar la base de datos o probar componentes por separado sin necesidad de modificar las vistas.

**Detalles técnicos importantes:**

- **`Launcher`:** Existe exclusivamente para poder empaquetar la aplicación; la clase principal que arranca el `.jar` no puede extender de `Application`, por lo que `Launcher` invoca a `App`.
- **`UserSession`:** Es un patrón *Singleton* que almacena los datos de quién inició sesión. Los controllers lo consultan para gestionar los permisos y decidir qué opciones mostrar según el rol.

---

## Base de datos

Consta de cinco tablas. La aplicación las crea automáticamente si no existen, por lo que no es necesario ejecutar ningún script manualmente.

| Tabla | Descripción |
|---|---|
| `usuarios` | Control de acceso al sistema (roles: admin o vendedor) |
| `clientes` | Registro de compradores, identificados con DNI único |
| `productos` | Catálogo de perfumes: nombre, marca, tipo (EDT/EDP/Parfum), ml, precio y stock |
| `ventas` | Cabecera de cada transacción: cliente, fecha y monto total |
| `detalle_ventas` | Líneas de detalle de cada venta: perfume, cantidad, precio y subtotal |

### Reglas de integridad

Se cumplen tanto a nivel de base de datos como en la lógica del código:

- El DNI del cliente debe tener exactamente 8 dígitos y es único.
- No se permite eliminar un cliente o un perfume que ya tenga ventas asociadas (llaves foráneas activas).
- Registrar una venta guarda la cabecera, el detalle y el descuento de stock en **una sola transacción**. Si ocurre algún error (como falta de stock), se revierte todo para evitar registros incompletos.

### Ubicación del archivo `perfumeria.db`

| Entorno / Situación | Ruta de `perfumeria.db` |
|---|---|
| Ejecutando desde el proyecto (IntelliJ / Maven) | `db/perfumeria.db` |
| Aplicación instalada con el `.exe` | `C:\Users\<usuario>\ParfumsHouse\perfumeria.db` |
| Ruta personalizada mediante argumento JVM | `-Dperfumeria.db=C:/ruta/perfumeria.db` |

> *Nota: La versión instalada no guarda la base de datos en la carpeta del programa (`Program Files`) porque Windows restringe los permisos de escritura en esa ubicación.*

### Inspección con DB Browser for SQLite

1. Abre DB Browser y selecciona **File → Open Database**.
2. Busca y selecciona el archivo `perfumeria.db` (si no aparece, cambia el filtro de archivos a *All files*).
3. En la pestaña **Browse Data** podrás visualizar y editar los registros creados por la aplicación.
4. Si realizas modificaciones manuales, pulsa **Write Changes** (`Ctrl+S`). Mientras existan cambios sin guardar, DB Browser mantendrá bloqueada la base de datos y la aplicación mostrará un error tras esperar unos segundos.

---

## Cómo ejecutarlo

**Requisitos previos:** Tener instalado **JDK 21**.

### Con IntelliJ IDEA

1. Ve a **File → Open…** y selecciona la carpeta raíz del proyecto (donde se encuentra el archivo `pom.xml`). Espera a que Maven termine de descargar las dependencias.
2. Abre `App.java` y pulsa el botón de ejecución (`▶`) junto al método `main`, o ejecuta directamente la clase `Launcher`.
3. Inicia sesión con alguna de las credenciales configuradas.

---

## Diseño

La interfaz fue concebida para transmitir la estética de una perfumería de lujo en lugar de un sistema administrativo genérico:

- **Paleta de colores:** Carbón (`#16161A`), Dorado (`#C9A24D`), Marfil (`#FAF6EE`) y Vino (`#7B2D4A`).
- **Tipografía:** Títulos y marca en tipografía *serif*; el resto de la interfaz en *Segoe UI*.
- **Estilos centralizados:** Todo el diseño visual está definido en `style.css`, evitando estilos o colores dispersos en el código Java.
- **Identidad visual:** El banner de bienvenida es una ilustración propia de frascos de atar árabes dorados bajo un arco de mihrab. Si deseas personalizarlo, reemplaza `img/bienvenida.png` conservando el mismo nombre de archivo.

---

## Posibles mejoras futuras

- Registrar **qué vendedor realizó cada venta** (añadiendo una columna `id_usuario` en la tabla `ventas`) y permitir filtrar el historial por usuario.
- Módulo de reportes para el administrador: ventas por día o mes, perfumes más vendidos y alertas de stock bajo.
- Generación de comprobantes de venta en formato PDF o listos para imprimir.
- Migrar la carpeta `test/` hacia la estructura estándar `src/test` e implementar pruebas unitarias con JUnit.
- Sistema de copias de seguridad (backups) automáticas de la base de datos.

---

## Autor

Desarrollado por **LOUIS ALEJANDRO ATTO Y GRUPO** como proyecto del curso de **Programación Orientada a Objetos**.

Si encuentras algún error o tienes una sugerencia de mejora, no dudes en abrir un *issue* en el repositorio.

---

## Bienvenido a Parfums House

> *Entra, elige una esencia y que cada venta sea una buena historia.*

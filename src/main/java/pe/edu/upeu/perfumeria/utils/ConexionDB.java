package pe.edu.upeu.perfumeria.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionDB {

    private static final String DB_PATH = resolverRuta();

    /**
     * 1) -Dperfumeria.db=ruta  2) carpeta ./db (modo desarrollo en IntelliJ)
     * 3) carpeta del usuario: ParfumsHouse/perfumeria.db (cuando está instalado como .exe)
     */
    private static String resolverRuta() {
        String propiedad = System.getProperty("perfumeria.db");
        if (propiedad != null && !propiedad.isBlank()) {
            return propiedad;
        }
        if (new File("db").isDirectory() && new File("pom.xml").exists()) {
            return "db" + File.separator + "perfumeria.db";
        }
        return System.getProperty("user.home") + File.separator + "ParfumsHouse"
                + File.separator + "perfumeria.db";
    }

    private static boolean inicializada = false;

    private static final String[] TABLAS = {
            """
            CREATE TABLE IF NOT EXISTS usuarios (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                username   TEXT UNIQUE NOT NULL,
                password   TEXT NOT NULL,
                rol        TEXT NOT NULL CHECK (rol IN ('ADMIN', 'VENDEDOR')),
                nombre     TEXT NOT NULL,
                apellido   TEXT NOT NULL,
                dni        TEXT,
                telefono   TEXT
            )""",
            """
            CREATE TABLE IF NOT EXISTS clientes (
                id_cliente INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre     TEXT NOT NULL,
                apellido   TEXT NOT NULL,
                dni        TEXT UNIQUE NOT NULL,
                telefono   TEXT
            )""",
            """
            CREATE TABLE IF NOT EXISTS productos (
                id_producto  INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre       TEXT NOT NULL,
                marca        TEXT NOT NULL,
                tipo         TEXT NOT NULL,
                capacidad_ml INTEGER NOT NULL,
                precio       REAL NOT NULL,
                stock        INTEGER NOT NULL
            )""",
            """
            CREATE TABLE IF NOT EXISTS ventas (
                id_venta    INTEGER PRIMARY KEY AUTOINCREMENT,
                id_cliente  INTEGER NOT NULL,
                fecha_venta TEXT NOT NULL,
                total       REAL NOT NULL,
                FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
            )""",
            """
            CREATE TABLE IF NOT EXISTS detalle_ventas (
                id_detalle      INTEGER PRIMARY KEY AUTOINCREMENT,
                id_venta        INTEGER NOT NULL,
                id_producto     INTEGER NOT NULL,
                cantidad        INTEGER NOT NULL,
                precio_unitario REAL NOT NULL,
                subtotal        REAL NOT NULL,
                FOREIGN KEY (id_venta)    REFERENCES ventas(id_venta) ON DELETE CASCADE,
                FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
            )"""
    };


    public static String getRutaAbsoluta() {
        return new File(DB_PATH).getAbsolutePath();
    }

    public static Connection getConexion() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver de SQLite", e);
        }
        File archivo = new File(DB_PATH).getAbsoluteFile();
        File carpeta = archivo.getParentFile();
        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + archivo.getPath());
        try (Statement st = conn.createStatement()) {

            st.execute("PRAGMA foreign_keys = ON");

            st.execute("PRAGMA busy_timeout = 5000");
        }
        inicializarTablas(conn);
        return conn;
    }

    private static synchronized void inicializarTablas(Connection conn) throws SQLException {
        if (inicializada) {
            return;
        }
        try (Statement st = conn.createStatement()) {
            for (String sql : TABLAS) {
                st.execute(sql);
            }
        }
        crearUsuariosIniciales(conn);
        inicializada = true;
        System.out.println("Base de datos lista: " + getRutaAbsoluta());
    }

    /** Si no hay usuarios, crea admin/admin123 y vendedor/vendedor123 (cambiar tras el primer ingreso). */
    private static void crearUsuariosIniciales(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement();
             java.sql.ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM usuarios")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }
        String sql = "INSERT INTO usuarios (username, password, rol, nombre, apellido) VALUES (?, ?, ?, ?, ?)";
        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            Object[][] datos = {
                    {"admin", "admin123", "ADMIN", "Administrador", "General"},
                    {"vendedor", "vendedor123", "VENDEDOR", "Vendedor", "Tienda"}
            };
            for (Object[] d : datos) {
                ps.setString(1, (String) d[0]);
                ps.setString(2, PasswordUtil.hash((String) d[1]));
                ps.setString(3, (String) d[2]);
                ps.setString(4, (String) d[3]);
                ps.setString(5, (String) d[4]);
                ps.executeUpdate();
            }
        }
    }
}

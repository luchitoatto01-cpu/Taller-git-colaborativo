-- Script para crear las tablas a mano en DB Browser for SQLite (pestaña "Ejecutar SQL").
-- La aplicación también las crea sola al conectarse, por eso es OPCIONAL.

PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS clientes (
    id_cliente INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre     TEXT NOT NULL,
    apellido   TEXT NOT NULL,
    dni        TEXT UNIQUE NOT NULL,
    telefono   TEXT
);

CREATE TABLE IF NOT EXISTS productos (
    id_producto  INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre       TEXT NOT NULL,
    marca        TEXT NOT NULL,
    tipo         TEXT NOT NULL,   -- EDT, EDP, Parfum
    capacidad_ml INTEGER NOT NULL,
    precio       REAL NOT NULL,
    stock        INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS ventas (
    id_venta    INTEGER PRIMARY KEY AUTOINCREMENT,
    id_cliente  INTEGER NOT NULL,
    fecha_venta TEXT NOT NULL,
    total       REAL NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
);

CREATE TABLE IF NOT EXISTS detalle_ventas (
    id_detalle      INTEGER PRIMARY KEY AUTOINCREMENT,
    id_venta        INTEGER NOT NULL,
    id_producto     INTEGER NOT NULL,
    cantidad        INTEGER NOT NULL,
    precio_unitario REAL NOT NULL,
    subtotal        REAL NOT NULL,
    FOREIGN KEY (id_venta)    REFERENCES ventas(id_venta) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
    username   TEXT UNIQUE NOT NULL,
    password   TEXT NOT NULL,   -- hash SHA-256 con sal (lo crea la aplicación)
    rol        TEXT NOT NULL CHECK (rol IN ('ADMIN', 'VENDEDOR')),
    nombre     TEXT NOT NULL,
    apellido   TEXT NOT NULL,
    dni        TEXT,
    telefono   TEXT
);

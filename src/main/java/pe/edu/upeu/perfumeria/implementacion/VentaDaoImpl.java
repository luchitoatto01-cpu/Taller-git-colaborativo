package pe.edu.upeu.perfumeria.implementacion;

import pe.edu.upeu.perfumeria.interfaces.IVentaDao;
import pe.edu.upeu.perfumeria.modelo.Cliente;
import pe.edu.upeu.perfumeria.modelo.DetalleVenta;
import pe.edu.upeu.perfumeria.modelo.Producto;
import pe.edu.upeu.perfumeria.modelo.Venta;
import pe.edu.upeu.perfumeria.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VentaDaoImpl implements IVentaDao {

    /**
     * Registra la venta COMPLETA en una sola transacción:
     * cabecera (ventas) + líneas (detalle_ventas) + descuento de stock.
     * Si algo falla (por ejemplo, stock insuficiente) se revierte todo.
     */
    @Override
    public boolean registrarVenta(Venta venta) {
        String sqlVenta = "INSERT INTO ventas (id_cliente, fecha_venta, total) VALUES (?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        String sqlStock = "UPDATE productos SET stock = stock - ? WHERE id_producto = ? AND stock >= ?";

        Connection conn = null;
        try {
            conn = ConexionDB.getConexion();
            conn.setAutoCommit(false);

            int idVenta;
            try (PreparedStatement st = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                st.setInt(1, venta.getCliente().getIdCliente());
                st.setString(2, venta.getFechaVenta());
                st.setDouble(3, venta.getTotal());
                st.executeUpdate();
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    idVenta = rs.getInt(1);
                }
            }

            try (PreparedStatement stD = conn.prepareStatement(sqlDetalle);
                 PreparedStatement stS = conn.prepareStatement(sqlStock)) {
                for (DetalleVenta d : venta.getDetalles()) {
                    stD.setInt(1, idVenta);
                    stD.setInt(2, d.getProducto().getIdProducto());
                    stD.setInt(3, d.getCantidad());
                    stD.setDouble(4, d.getPrecioUnitario());
                    stD.setDouble(5, d.getSubtotal());
                    stD.executeUpdate();

                    stS.setInt(1, d.getCantidad());
                    stS.setInt(2, d.getProducto().getIdProducto());
                    stS.setInt(3, d.getCantidad());
                    if (stS.executeUpdate() == 0) {
                        // no alcanzó el stock: se cancela toda la venta
                        conn.rollback();
                        return false;
                    }
                }
            }

            conn.commit();
            venta.setIdVenta(idVenta);
            return true;
        } catch (SQLException e) {
            System.out.println("Error al registrar venta: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error en rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public List<Venta> listarVentas() {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT v.id_venta, v.fecha_venta, v.total, " +
                "c.id_cliente, c.nombre, c.apellido, c.dni, c.telefono " +
                "FROM ventas v INNER JOIN clientes c ON v.id_cliente = c.id_cliente " +
                "ORDER BY v.id_venta DESC";
        try (Connection conn = ConexionDB.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Venta v = new Venta();
                v.setIdVenta(rs.getInt("id_venta"));
                v.setFechaVenta(rs.getString("fecha_venta"));
                v.setTotal(rs.getDouble("total"));
                v.setCliente(new Cliente(
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("dni"),
                        rs.getString("telefono")));
                lista.add(v);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar ventas: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<DetalleVenta> listarDetalles(int idVenta) {
        List<DetalleVenta> lista = new ArrayList<>();
        String sql = "SELECT d.id_detalle, d.id_venta, d.cantidad, d.precio_unitario, d.subtotal, " +
                "p.id_producto, p.nombre, p.marca, p.tipo, p.capacidad_ml, p.precio, p.stock " +
                "FROM detalle_ventas d INNER JOIN productos p ON d.id_producto = p.id_producto " +
                "WHERE d.id_venta = ? ORDER BY d.id_detalle";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idVenta);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto(
                            rs.getInt("id_producto"),
                            rs.getString("nombre"),
                            rs.getString("marca"),
                            rs.getString("tipo"),
                            rs.getInt("capacidad_ml"),
                            rs.getDouble("precio"),
                            rs.getInt("stock"));
                    DetalleVenta d = new DetalleVenta(p, rs.getInt("cantidad"), rs.getDouble("precio_unitario"));
                    d.setIdDetalle(rs.getInt("id_detalle"));
                    d.setIdVenta(rs.getInt("id_venta"));
                    d.setSubtotal(rs.getDouble("subtotal"));
                    lista.add(d);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar detalles: " + e.getMessage());
        }
        return lista;
    }
}

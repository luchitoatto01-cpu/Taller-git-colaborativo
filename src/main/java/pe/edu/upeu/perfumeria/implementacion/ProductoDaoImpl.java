package pe.edu.upeu.perfumeria.implementacion;

import pe.edu.upeu.perfumeria.interfaces.IProductoDao;
import pe.edu.upeu.perfumeria.modelo.Producto;
import pe.edu.upeu.perfumeria.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductoDaoImpl implements IProductoDao {

    private Producto mapRow(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id_producto"),
                rs.getString("nombre"),
                rs.getString("marca"),
                rs.getString("tipo"),
                rs.getInt("capacidad_ml"),
                rs.getDouble("precio"),
                rs.getInt("stock"));
    }

    @Override
    public boolean insertar(Producto p) {
        String sql = "INSERT INTO productos (nombre, marca, tipo, capacidad_ml, precio, stock) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, p.getNombre());
            st.setString(2, p.getMarca());
            st.setString(3, p.getTipo());
            st.setInt(4, p.getCapacidadMl());
            st.setDouble(5, p.getPrecio());
            st.setInt(6, p.getStock());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Producto p) {
        String sql = "UPDATE productos SET nombre=?, marca=?, tipo=?, capacidad_ml=?, precio=?, stock=? WHERE id_producto=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, p.getNombre());
            st.setString(2, p.getMarca());
            st.setString(3, p.getTipo());
            st.setInt(4, p.getCapacidadMl());
            st.setDouble(5, p.getPrecio());
            st.setInt(6, p.getStock());
            st.setInt(7, p.getIdProducto());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int idProducto) {
        String sql = "DELETE FROM productos WHERE id_producto=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idProducto);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            // por ejemplo: el producto ya fue vendido (llave foránea)
            System.out.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Producto> buscar(String texto, int limite) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE stock > 0 AND (nombre LIKE ? OR marca LIKE ?) "
                + "ORDER BY nombre LIMIT ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement st = conn.prepareStatement(sql)) {
            String patron = "%" + texto + "%";
            st.setString(1, patron);
            st.setString(2, patron);
            st.setInt(3, limite);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar productos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos ORDER BY marca, nombre";
        try (Connection conn = ConexionDB.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar productos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Producto buscarPorId(int idProducto) {
        String sql = "SELECT * FROM productos WHERE id_producto=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idProducto);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar producto: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean actualizarStock(int idProducto, int nuevoStock) {
        String sql = "UPDATE productos SET stock=? WHERE id_producto=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, nuevoStock);
            st.setInt(2, idProducto);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar stock: " + e.getMessage());
            return false;
        }
    }
}

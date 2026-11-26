package pe.edu.upeu.perfumeria.implementacion;

import pe.edu.upeu.perfumeria.interfaces.IClienteDao;
import pe.edu.upeu.perfumeria.modelo.Cliente;
import pe.edu.upeu.perfumeria.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClienteDaoImpl implements IClienteDao {

    private Cliente mapRow(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("dni"),
                rs.getString("telefono"));
    }

    @Override
    public boolean insertar(Cliente c) {
        String sql = "INSERT INTO clientes (nombre, apellido, dni, telefono) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, c.getNombre());
            st.setString(2, c.getApellido());
            st.setString(3, c.getDni());
            st.setString(4, c.getTelefono());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar cliente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Cliente c) {
        String sql = "UPDATE clientes SET nombre=?, apellido=?, dni=?, telefono=? WHERE id_cliente=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, c.getNombre());
            st.setString(2, c.getApellido());
            st.setString(3, c.getDni());
            st.setString(4, c.getTelefono());
            st.setInt(5, c.getIdCliente());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int idCliente) {
        String sql = "DELETE FROM clientes WHERE id_cliente=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idCliente);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            // por ejemplo: el cliente ya tiene ventas (llave foránea)
            System.out.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY apellido, nombre";
        try (Connection conn = ConexionDB.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar clientes: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Cliente buscarPorId(int idCliente) {
        String sql = "SELECT * FROM clientes WHERE id_cliente=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idCliente);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar cliente por id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Cliente buscarPorDni(String dni) {
        String sql = "SELECT * FROM clientes WHERE dni=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, dni);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar cliente por DNI: " + e.getMessage());
        }
        return null;
    }
}

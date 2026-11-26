package pe.edu.upeu.perfumeria.implementacion;

import pe.edu.upeu.perfumeria.interfaces.IUsuarioDao;
import pe.edu.upeu.perfumeria.modelo.Usuario;
import pe.edu.upeu.perfumeria.utils.ConexionDB;
import pe.edu.upeu.perfumeria.utils.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDaoImpl implements IUsuarioDao {

    private Usuario mapRow(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id_usuario"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("dni"),
                rs.getString("telefono"),
                rs.getString("username"),
                rs.getString("rol"));
    }

    @Override
    public Usuario login(String username, String password) {
        String sql = "SELECT * FROM usuarios WHERE username = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, username);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next() && PasswordUtil.verificar(password, rs.getString("password"))) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en el login: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean insertar(Usuario u) {
        String sql = "INSERT INTO usuarios (username, password, rol, nombre, apellido, dni, telefono) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, u.getUsername());
            st.setString(2, PasswordUtil.hash(u.getPassword()));
            st.setString(3, u.getRol());
            st.setString(4, u.getNombre());
            st.setString(5, u.getApellido());
            st.setString(6, u.getDni());
            st.setString(7, u.getTelefono());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Usuario u) {
        boolean cambiaClave = u.getPassword() != null && !u.getPassword().isBlank();
        String sql = "UPDATE usuarios SET username=?, rol=?, nombre=?, apellido=?, dni=?, telefono=?"
                + (cambiaClave ? ", password=?" : "") + " WHERE id_usuario=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement st = conn.prepareStatement(sql)) {
            int i = 1;
            st.setString(i++, u.getUsername());
            st.setString(i++, u.getRol());
            st.setString(i++, u.getNombre());
            st.setString(i++, u.getApellido());
            st.setString(i++, u.getDni());
            st.setString(i++, u.getTelefono());
            if (cambiaClave) {
                st.setString(i++, PasswordUtil.hash(u.getPassword()));
            }
            st.setInt(i, u.getIdUsuario());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int idUsuario) {
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement st = conn.prepareStatement("DELETE FROM usuarios WHERE id_usuario=?")) {
            st.setInt(1, idUsuario);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM usuarios ORDER BY rol, nombre")) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }
}

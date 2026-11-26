package pe.edu.upeu.perfumeria.interfaces;

import pe.edu.upeu.perfumeria.modelo.Usuario;

import java.util.List;

public interface IUsuarioDao {
    Usuario login(String username, String password);
    boolean insertar(Usuario usuario);
    /** Si usuario.getPassword() viene vacío, se conserva la clave actual. */
    boolean actualizar(Usuario usuario);
    boolean eliminar(int idUsuario);
    List<Usuario> listar();
}

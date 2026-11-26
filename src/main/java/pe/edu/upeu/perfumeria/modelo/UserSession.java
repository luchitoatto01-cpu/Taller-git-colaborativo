package pe.edu.upeu.perfumeria.modelo;

/** Guarda el usuario que inició sesión (patrón Singleton). */
public class UserSession {
    private static final UserSession INSTANCE = new UserSession();
    private Usuario usuario;

    private UserSession() {}

    public static UserSession getInstance() { return INSTANCE; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public boolean isAdmin() { return usuario != null && usuario.isAdmin(); }
    public void cerrar() { usuario = null; }
}

package pe.edu.upeu.perfumeria.modelo;

public class Usuario extends Persona {
    public static final String ADMIN = "ADMIN";
    public static final String VENDEDOR = "VENDEDOR";

    private int idUsuario;
    private String username;
    private String password; // solo en texto plano al crear/editar; en BD se guarda con hash
    private String rol;

    public Usuario() {}

    public Usuario(int idUsuario, String nombre, String apellido, String dni, String telefono,
                   String username, String rol) {
        super(nombre, apellido, dni, telefono);
        this.idUsuario = idUsuario;
        this.username = username;
        this.rol = rol;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public boolean isAdmin() { return ADMIN.equals(rol); }
}

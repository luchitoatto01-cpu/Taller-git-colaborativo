package pe.edu.upeu.perfumeria.modelo;

public class Cliente extends Persona {
    private int idCliente;

    public Cliente() {}

    public Cliente(int idCliente, String nombre, String apellido, String dni, String telefono) {
        super(nombre, apellido, dni, telefono);
        this.idCliente = idCliente;
    }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    @Override
    public String toString() {
        return getNombreCompleto() + " (" + getDni() + ")";
    }
}

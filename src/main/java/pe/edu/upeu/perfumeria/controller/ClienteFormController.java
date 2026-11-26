package pe.edu.upeu.perfumeria.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.perfumeria.implementacion.ClienteDaoImpl;
import pe.edu.upeu.perfumeria.interfaces.IClienteDao;
import pe.edu.upeu.perfumeria.modelo.Cliente;
import pe.edu.upeu.perfumeria.utils.UtilAlerts;

public class ClienteFormController {

    private final IClienteDao clienteDao = new ClienteDaoImpl();

    @FXML
    private Label lbTitulo;
    @FXML
    private TextField txtNombre, txtApellido, txtDni, txtTelefono;

    private Cliente cliente;
    private boolean saved = false;

    public boolean isSaved() {
        return saved;
    }

    public Cliente getCliente() {
        return cliente;
    }

    // recibe el cliente a editar (null = cliente nuevo)
    public void setCliente(Cliente c) {
        this.cliente = c;
        if (c != null) {
            lbTitulo.setText("EDITAR CLIENTE");
            txtNombre.setText(c.getNombre());
            txtApellido.setText(c.getApellido());
            txtDni.setText(c.getDni());
            txtTelefono.setText(c.getTelefono());
        }
    }

    @FXML
    private void handleCancel() {
        cerrar();
    }

    @FXML
    private void handleSave() {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String dni = txtDni.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty()) {
            error("Nombre, apellido y DNI son obligatorios.");
            return;
        }
        if (!dni.matches("\\d{8}")) {
            error("El DNI debe tener 8 dígitos numéricos.");
            return;
        }
        if (!telefono.isEmpty() && !telefono.matches("[0-9+ ]{6,15}")) {
            error("El teléfono solo puede tener números (6 a 15 caracteres).");
            return;
        }
        // el DNI no se puede repetir (la BD tiene UNIQUE)
        Cliente existente = clienteDao.buscarPorDni(dni);
        boolean esOtro = existente != null
                && (cliente == null || existente.getIdCliente() != cliente.getIdCliente());
        if (esOtro) {
            error("Ya existe un cliente con el DNI " + dni + ".");
            return;
        }

        if (cliente == null) {
            cliente = new Cliente();
        }
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setDni(dni);
        cliente.setTelefono(telefono.isEmpty() ? null : telefono);
        saved = true;
        cerrar();
    }

    private void error(String mensaje) {
        UtilAlerts.showError("Validación", mensaje);
    }

    private void cerrar() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }
}

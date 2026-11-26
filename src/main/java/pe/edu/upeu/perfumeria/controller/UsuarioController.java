package pe.edu.upeu.perfumeria.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pe.edu.upeu.perfumeria.implementacion.UsuarioDaoImpl;
import pe.edu.upeu.perfumeria.interfaces.IUsuarioDao;
import pe.edu.upeu.perfumeria.modelo.UserSession;
import pe.edu.upeu.perfumeria.modelo.Usuario;
import pe.edu.upeu.perfumeria.utils.UtilAlerts;
import pe.edu.upeu.perfumeria.utils.UtilTabla;

/** Gestión de usuarios (solo ADMIN). Formulario en la misma pantalla. */
public class UsuarioController {

    @FXML
    private TableView<Usuario> tableUsuario;
    @FXML
    private TableColumn<Usuario, Number> colNumero;
    @FXML
    private TableColumn<Usuario, String> colNombre, colUsuario, colRol, colDni;
    @FXML
    private TextField txtNombre, txtApellido, txtDni, txtTelefono, txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ComboBox<String> cbRol;

    private final IUsuarioDao usuarioDao = new UsuarioDaoImpl();
    private Usuario seleccionado; // null = modo "nuevo"

    @FXML
    public void initialize() {
        UtilTabla.columnaNumerada(colNumero);
        colNombre.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getNombreCompleto()));
        colUsuario.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getUsername()));
        colRol.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getRol()));
        colDni.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                cd.getValue().getDni() == null ? "" : cd.getValue().getDni()));
        cbRol.setItems(FXCollections.observableArrayList(Usuario.ADMIN, Usuario.VENDEDOR));
        cbRol.getSelectionModel().select(Usuario.VENDEDOR);

        tableUsuario.getSelectionModel().selectedItemProperty().addListener((obs, old, u) -> {
            if (u != null) {
                cargarEnFormulario(u);
            }
        });
        cargarDatos();
    }

    private void cargarDatos() {
        tableUsuario.setItems(FXCollections.observableArrayList(usuarioDao.listar()));
    }

    private void cargarEnFormulario(Usuario u) {
        seleccionado = u;
        txtNombre.setText(u.getNombre());
        txtApellido.setText(u.getApellido());
        txtDni.setText(u.getDni());
        txtTelefono.setText(u.getTelefono());
        txtUsername.setText(u.getUsername());
        txtPassword.clear();
        txtPassword.setPromptText("Vacío = conservar la actual");
        cbRol.getSelectionModel().select(u.getRol());
    }

    @FXML
    private void handleLimpiar() {
        seleccionado = null;
        tableUsuario.getSelectionModel().clearSelection();
        txtNombre.clear();
        txtApellido.clear();
        txtDni.clear();
        txtTelefono.clear();
        txtUsername.clear();
        txtPassword.clear();
        txtPassword.setPromptText("Contraseña");
        cbRol.getSelectionModel().select(Usuario.VENDEDOR);
    }

    @FXML
    private void handleGuardar() {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String username = txtUsername.getText().trim();
        String clave = txtPassword.getText();

        if (nombre.isEmpty() || apellido.isEmpty() || username.isEmpty()) {
            UtilAlerts.showWarning("Campos requeridos", "Nombre, apellido y usuario son obligatorios.");
            return;
        }
        if (seleccionado == null && clave.length() < 6) {
            UtilAlerts.showWarning("Contraseña", "La contraseña debe tener al menos 6 caracteres.");
            return;
        }
        if (seleccionado != null && !clave.isEmpty() && clave.length() < 6) {
            UtilAlerts.showWarning("Contraseña", "La nueva contraseña debe tener al menos 6 caracteres.");
            return;
        }
        int idPropio = UserSession.getInstance().getUsuario().getIdUsuario();
        if (seleccionado != null && seleccionado.getIdUsuario() == idPropio
                && !Usuario.ADMIN.equals(cbRol.getValue())) {
            UtilAlerts.showWarning("Rol", "No puede quitarse a sí mismo el rol de administrador.");
            return;
        }

        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setDni(txtDni.getText().trim());
        u.setTelefono(txtTelefono.getText().trim());
        u.setUsername(username);
        u.setPassword(clave);
        u.setRol(cbRol.getValue());

        boolean ok;
        if (seleccionado == null) {
            ok = usuarioDao.insertar(u);
        } else {
            u.setIdUsuario(seleccionado.getIdUsuario());
            ok = usuarioDao.actualizar(u);
        }
        if (ok) {
            UtilAlerts.showInfo("Éxito", "Usuario guardado correctamente.");
            handleLimpiar();
            cargarDatos();
        } else {
            UtilAlerts.showError("Error", "No se pudo guardar. Verifique que el usuario no esté repetido.");
        }
    }

    @FXML
    private void handleEliminar() {
        Usuario u = tableUsuario.getSelectionModel().getSelectedItem();
        if (u == null) {
            UtilAlerts.showWarning("Selección requerida", "Seleccione un usuario para eliminar.");
            return;
        }
        if (u.getIdUsuario() == UserSession.getInstance().getUsuario().getIdUsuario()) {
            UtilAlerts.showWarning("No permitido", "No puede eliminar su propio usuario.");
            return;
        }
        if (UtilAlerts.showConfirmation("Confirmar eliminación", "¿Eliminar al usuario " + u.getUsername() + "?")) {
            if (usuarioDao.eliminar(u.getIdUsuario())) {
                handleLimpiar();
                cargarDatos();
            } else {
                UtilAlerts.showError("Error", "No se pudo eliminar el usuario.");
            }
        }
    }
}

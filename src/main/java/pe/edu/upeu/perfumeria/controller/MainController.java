package pe.edu.upeu.perfumeria.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pe.edu.upeu.perfumeria.modelo.UserSession;
import pe.edu.upeu.perfumeria.modelo.Usuario;
import pe.edu.upeu.perfumeria.utils.UtilAlerts;

public class MainController {

    @FXML
    private StackPane contentArea;
    @FXML
    private Label lblUsuario, lblRol;
    @FXML
    private Button btnUsuarios;

    @FXML
    public void initialize() {
        Usuario u = UserSession.getInstance().getUsuario();
        if (u != null) {
            lblUsuario.setText(u.getNombreCompleto());
            lblRol.setText(u.getRol());
        }
        // el vendedor no ve la gestión de usuarios
        boolean admin = UserSession.getInstance().isAdmin();
        btnUsuarios.setVisible(admin);
        btnUsuarios.setManaged(admin);
        showHome();
    }

    @FXML
    private void showHome() {
        loadView("home-view.fxml");
    }

    @FXML
    private void showClientes() {
        loadView("cliente-view.fxml");
    }

    @FXML
    private void showProductos() {
        loadView("producto-view.fxml");
    }

    @FXML
    private void showVentas() {
        loadView("venta-view.fxml");
    }

    @FXML
    private void showUsuarios() {
        if (UserSession.getInstance().isAdmin()) {
            loadView("usuario-view.fxml");
        }
    }

    @FXML
    private void handleLogout() {
        if (!UtilAlerts.showConfirmation("Cerrar sesión", "¿Desea cerrar la sesión?")) {
            return;
        }
        UserSession.getInstance().cerrar();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/pe/edu/upeu/perfumeria/view/login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(new Scene(root));
            stage.setTitle("Parfums House - Iniciar sesión");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // método que busca y carga las vistas dentro del área central
    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/pe/edu/upeu/perfumeria/view/" + fxml));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
            UtilAlerts.showError("Error al cargar la vista", fxml + "\n" + e.getMessage());
        }
    }
}

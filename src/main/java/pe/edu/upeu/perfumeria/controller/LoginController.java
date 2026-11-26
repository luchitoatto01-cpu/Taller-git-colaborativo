package pe.edu.upeu.perfumeria.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.perfumeria.implementacion.UsuarioDaoImpl;
import pe.edu.upeu.perfumeria.interfaces.IUsuarioDao;
import pe.edu.upeu.perfumeria.modelo.UserSession;
import pe.edu.upeu.perfumeria.modelo.Usuario;
import pe.edu.upeu.perfumeria.utils.UtilAlerts;

public class LoginController {

    @FXML
    private TextField txtUser;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lblError;

    private final IUsuarioDao usuarioDao = new UsuarioDaoImpl();

    @FXML
    private void handleLogin() {
        String user = txtUser.getText().trim();
        String pass = txtPassword.getText();
        if (user.isEmpty() || pass.isEmpty()) {
            lblError.setText("Ingrese usuario y contraseña.");
            return;
        }
        Usuario u = usuarioDao.login(user, pass);
        if (u == null) {
            lblError.setText("Usuario o contraseña incorrectos.");
            txtPassword.clear();
            return;
        }
        UserSession.getInstance().setUsuario(u);
        abrirPrincipal();
    }

    private void abrirPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/pe/edu/upeu/perfumeria/view/main-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) txtUser.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Parfums House - Sistema de Perfumería");
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
            UtilAlerts.showError("Error", "No se pudo abrir el sistema: " + e.getMessage());
        }
    }
}

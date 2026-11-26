package pe.edu.upeu.perfumeria.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class UtilAlerts {

    private static Alert crear(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        return alert;
    }

    public static void showInfo(String titulo, String mensaje) {
        crear(Alert.AlertType.INFORMATION, titulo, mensaje).showAndWait();
    }

    public static void showWarning(String titulo, String mensaje) {
        crear(Alert.AlertType.WARNING, titulo, mensaje).showAndWait();
    }

    public static void showError(String titulo, String mensaje) {
        crear(Alert.AlertType.ERROR, titulo, mensaje).showAndWait();
    }

    public static boolean showConfirmation(String titulo, String mensaje) {
        Optional<ButtonType> result = crear(Alert.AlertType.CONFIRMATION, titulo, mensaje).showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}

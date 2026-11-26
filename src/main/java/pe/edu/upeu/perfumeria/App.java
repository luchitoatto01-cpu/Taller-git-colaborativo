package pe.edu.upeu.perfumeria;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                App.class.getResource("/pe/edu/upeu/perfumeria/view/login-view.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.getIcons().add(new Image(
                App.class.getResourceAsStream("/pe/edu/upeu/perfumeria/img/logo.png")));
        stage.setTitle("Parfums House - Iniciar sesión");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

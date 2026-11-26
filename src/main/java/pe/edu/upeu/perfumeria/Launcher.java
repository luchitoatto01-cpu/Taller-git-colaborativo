package pe.edu.upeu.perfumeria;

import javafx.application.Application;

// Se usa para generar el .jar (la clase que arranca no puede extender Application)
public class Launcher {
    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}

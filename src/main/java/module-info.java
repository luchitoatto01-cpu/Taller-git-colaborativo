module pe.edu.upeu.perfumeria {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    // driver de SQLite
    requires org.xerial.sqlitejdbc;

    // FXML necesita acceso a los controladores para inyectar los @FXML
    opens pe.edu.upeu.perfumeria to javafx.fxml;
    opens pe.edu.upeu.perfumeria.controller to javafx.fxml;
    // el modelo se abre para que JavaFX pueda leerlo
    opens pe.edu.upeu.perfumeria.modelo to javafx.base;

    exports pe.edu.upeu.perfumeria;
    exports pe.edu.upeu.perfumeria.controller;
    exports pe.edu.upeu.perfumeria.modelo;
    exports pe.edu.upeu.perfumeria.interfaces;
    exports pe.edu.upeu.perfumeria.implementacion;
    exports pe.edu.upeu.perfumeria.utils;
    exports pe.edu.upeu.perfumeria.test;
}

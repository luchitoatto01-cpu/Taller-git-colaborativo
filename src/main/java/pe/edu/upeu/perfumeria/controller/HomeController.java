package pe.edu.upeu.perfumeria.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import pe.edu.upeu.perfumeria.implementacion.ClienteDaoImpl;
import pe.edu.upeu.perfumeria.implementacion.ProductoDaoImpl;
import pe.edu.upeu.perfumeria.implementacion.VentaDaoImpl;
import pe.edu.upeu.perfumeria.modelo.UserSession;
import pe.edu.upeu.perfumeria.utils.ConexionDB;

public class HomeController {

    @FXML
    private Label lblClientes, lblProductos, lblVentas, lblRuta;

    @FXML
    public void initialize() {
        lblClientes.setText(String.valueOf(new ClienteDaoImpl().listar().size()));
        lblProductos.setText(String.valueOf(new ProductoDaoImpl().listar().size()));
        lblVentas.setText(String.valueOf(new VentaDaoImpl().listarVentas().size()));
        boolean admin = UserSession.getInstance().isAdmin();
        lblRuta.setVisible(admin);
        lblRuta.setManaged(admin);
        lblRuta.setText("Base de datos: " + ConexionDB.getRutaAbsoluta());
    }
}

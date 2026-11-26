package pe.edu.upeu.perfumeria.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.perfumeria.modelo.Producto;
import pe.edu.upeu.perfumeria.utils.UtilAlerts;

public class ProductoFormController {

    @FXML
    private Label lbTitulo;
    @FXML
    private TextField txtNombre, txtMarca, txtCapacidad, txtPrecio, txtStock;
    @FXML
    private ComboBox<String> cbTipo;

    private Producto producto;
    private boolean saved = false;

    @FXML
    public void initialize() {
        cbTipo.getItems().addAll("EDT", "EDP", "Parfum");
        cbTipo.setValue("EDP");
    }

    public boolean isSaved() {
        return saved;
    }

    public Producto getProducto() {
        return producto;
    }

    // recibe el producto a editar (null = producto nuevo)
    public void setProducto(Producto p) {
        this.producto = p;
        if (p != null) {
            lbTitulo.setText("EDITAR PERFUME");
            txtNombre.setText(p.getNombre());
            txtMarca.setText(p.getMarca());
            cbTipo.setValue(p.getTipo());
            txtCapacidad.setText(String.valueOf(p.getCapacidadMl()));
            txtPrecio.setText(String.valueOf(p.getPrecio()));
            txtStock.setText(String.valueOf(p.getStock()));
        }
    }

    @FXML
    private void handleCancel() {
        cerrar();
    }

    @FXML
    private void handleSave() {
        String nombre = txtNombre.getText().trim();
        String marca = txtMarca.getText().trim();
        if (nombre.isEmpty() || marca.isEmpty()) {
            error("El nombre y la marca son obligatorios.");
            return;
        }
        if (cbTipo.getValue() == null) {
            error("Seleccione el tipo (EDT, EDP o Parfum).");
            return;
        }

        int capacidad;
        int stock;
        double precio;
        try {
            capacidad = Integer.parseInt(txtCapacidad.getText().trim());
        } catch (NumberFormatException e) {
            error("La capacidad (ml) debe ser un número entero.");
            return;
        }
        try {
            precio = Double.parseDouble(txtPrecio.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            error("El precio debe ser un número (ej: 250.50).");
            return;
        }
        try {
            stock = Integer.parseInt(txtStock.getText().trim());
        } catch (NumberFormatException e) {
            error("El stock debe ser un número entero.");
            return;
        }
        if (capacidad <= 0) {
            error("La capacidad debe ser mayor a 0.");
            return;
        }
        if (precio <= 0) {
            error("El precio debe ser mayor a 0.");
            return;
        }
        if (stock < 0) {
            error("El stock no puede ser negativo.");
            return;
        }

        if (producto == null) {
            producto = new Producto();
        }
        producto.setNombre(nombre);
        producto.setMarca(marca);
        producto.setTipo(cbTipo.getValue());
        producto.setCapacidadMl(capacidad);
        producto.setPrecio(precio);
        producto.setStock(stock);
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

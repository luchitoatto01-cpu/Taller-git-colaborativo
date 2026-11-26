package pe.edu.upeu.perfumeria.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.upeu.perfumeria.App;
import pe.edu.upeu.perfumeria.implementacion.ProductoDaoImpl;
import pe.edu.upeu.perfumeria.interfaces.IProductoDao;
import pe.edu.upeu.perfumeria.modelo.Producto;
import pe.edu.upeu.perfumeria.modelo.UserSession;
import pe.edu.upeu.perfumeria.utils.UtilAlerts;
import pe.edu.upeu.perfumeria.utils.UtilTabla;

public class ProductoController {

    @FXML
    private TableView<Producto> tableProducto;
    @FXML
    private TableColumn<Producto, Number> colNumero;
    @FXML
    private TableColumn<Producto, String> colNombre, colMarca, colTipo, colPrecio;
    @FXML
    private TableColumn<Producto, Integer> colMl, colStock;
    @FXML
    private Button btnNuevo;
    @FXML
    private HBox boxAcciones;

    private final IProductoDao productoDao = new ProductoDaoImpl();
    private ObservableList<Producto> listaProductos;

    @FXML
    public void initialize() {
        UtilTabla.columnaNumerada(colNumero);
        colNombre.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getNombre()));
        colMarca.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getMarca()));
        colTipo.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getTipo()));
        colMl.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getCapacidadMl()));
        colPrecio.setCellValueFactory(cd -> new ReadOnlyStringWrapper(UtilTabla.soles(cd.getValue().getPrecio())));
        colStock.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getStock()));

        // el stock bajo (5 o menos) se muestra en rojo
        colStock.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(item));
                    setStyle(item <= 5 ? "-fx-text-fill: #C62828; -fx-font-weight: bold;" : "");
                }
            }
        });
        // el vendedor solo consulta el catálogo
        boolean admin = UserSession.getInstance().isAdmin();
        for (javafx.scene.Node n : new javafx.scene.Node[]{btnNuevo, boxAcciones}) {
            n.setVisible(admin);
            n.setManaged(admin);
        }
        cargarDatos();
    }

    private void cargarDatos() {
        listaProductos = FXCollections.observableArrayList(productoDao.listar());
        tableProducto.setItems(listaProductos);
    }

    @FXML
    private void handleNew() {
        if (!UserSession.getInstance().isAdmin()) {
            return;
        }
        showForm(null);
    }

    @FXML
    private void handleEdit() {
        if (!UserSession.getInstance().isAdmin()) {
            return;
        }
        Producto seleccionado = tableProducto.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            UtilAlerts.showWarning("Selección requerida", "Seleccione un perfume para editar.");
            return;
        }
        showForm(productoDao.buscarPorId(seleccionado.getIdProducto()));
    }

    @FXML
    private void handleDelete() {
        if (!UserSession.getInstance().isAdmin()) {
            return;
        }
        Producto seleccionado = tableProducto.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            UtilAlerts.showWarning("Selección requerida", "Seleccione un perfume para eliminar.");
            return;
        }
        if (UtilAlerts.showConfirmation("Confirmar eliminación",
                "¿Eliminar el perfume " + seleccionado.getNombre() + " (" + seleccionado.getMarca() + ")?")) {
            if (productoDao.eliminar(seleccionado.getIdProducto())) {
                listaProductos.remove(seleccionado);
            } else {
                UtilAlerts.showError("No se pudo eliminar",
                        "El perfume no se pudo eliminar. Puede que ya esté en ventas registradas.");
            }
        }
    }

    private void showForm(Producto producto) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    App.class.getResource("/pe/edu/upeu/perfumeria/view/producto-form.fxml"));
            Parent root = loader.load();
            ProductoFormController form = loader.getController();
            form.setProducto(producto);

            Stage stage = new Stage();
            stage.setTitle(producto == null ? "Nuevo perfume" : "Editar perfume");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tableProducto.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (form.isSaved()) {
                Producto procesado = form.getProducto();
                boolean ok = (producto == null) ? productoDao.insertar(procesado)
                                                : productoDao.actualizar(procesado);
                if (ok) {
                    UtilAlerts.showInfo("Éxito",
                            producto == null ? "Perfume registrado correctamente." : "Perfume actualizado correctamente.");
                } else {
                    UtilAlerts.showError("Error", "No se pudo guardar el perfume.");
                }
                cargarDatos();
            }
        } catch (Exception e) {
            e.printStackTrace();
            UtilAlerts.showError("Error", "No se pudo abrir el formulario: " + e.getMessage());
        }
    }
}

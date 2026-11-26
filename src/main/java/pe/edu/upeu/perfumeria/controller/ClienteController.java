package pe.edu.upeu.perfumeria.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.upeu.perfumeria.App;
import pe.edu.upeu.perfumeria.implementacion.ClienteDaoImpl;
import pe.edu.upeu.perfumeria.interfaces.IClienteDao;
import pe.edu.upeu.perfumeria.modelo.Cliente;
import pe.edu.upeu.perfumeria.utils.UtilAlerts;
import pe.edu.upeu.perfumeria.utils.UtilTabla;

public class ClienteController {

    @FXML
    private TableView<Cliente> tableCliente;
    @FXML
    private TableColumn<Cliente, Number> colNumero;
    @FXML
    private TableColumn<Cliente, String> colNombre, colApellido, colDni, colTelefono;

    private final IClienteDao clienteDao = new ClienteDaoImpl();
    private ObservableList<Cliente> listaClientes;

    @FXML
    public void initialize() {
        UtilTabla.columnaNumerada(colNumero);
        colNombre.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getNombre()));
        colApellido.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getApellido()));
        colDni.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getDni()));
        colTelefono.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getTelefono()));
        cargarDatos();
    }

    private void cargarDatos() {
        listaClientes = FXCollections.observableArrayList(clienteDao.listar());
        tableCliente.setItems(listaClientes);
    }

    @FXML
    private void handleNew() {
        showForm(null);
    }

    @FXML
    private void handleEdit() {
        Cliente seleccionado = tableCliente.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            UtilAlerts.showWarning("Selección requerida", "Seleccione un cliente para editar.");
            return;
        }
        // se vuelve a leer de la BD para editar los datos actuales
        showForm(clienteDao.buscarPorId(seleccionado.getIdCliente()));
    }

    @FXML
    private void handleDelete() {
        Cliente seleccionado = tableCliente.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            UtilAlerts.showWarning("Selección requerida", "Seleccione un cliente para eliminar.");
            return;
        }
        if (UtilAlerts.showConfirmation("Confirmar eliminación",
                "¿Eliminar al cliente " + seleccionado.getNombreCompleto() + "?")) {
            if (clienteDao.eliminar(seleccionado.getIdCliente())) {
                listaClientes.remove(seleccionado);
            } else {
                UtilAlerts.showError("No se pudo eliminar",
                        "El cliente no se pudo eliminar. Puede que ya tenga ventas registradas.");
            }
        }
    }

    private void showForm(Cliente cliente) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    App.class.getResource("/pe/edu/upeu/perfumeria/view/cliente-form.fxml"));
            Parent root = loader.load();
            ClienteFormController form = loader.getController();
            form.setCliente(cliente);

            Stage stage = new Stage();
            stage.setTitle(cliente == null ? "Nuevo cliente" : "Editar cliente");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tableCliente.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (form.isSaved()) {
                Cliente procesado = form.getCliente();
                boolean ok = (cliente == null) ? clienteDao.insertar(procesado)
                                               : clienteDao.actualizar(procesado);
                if (ok) {
                    UtilAlerts.showInfo("Éxito",
                            cliente == null ? "Cliente registrado correctamente." : "Cliente actualizado correctamente.");
                } else {
                    UtilAlerts.showError("Error", "No se pudo guardar el cliente.");
                }
                cargarDatos();
            }
        } catch (Exception e) {
            e.printStackTrace();
            UtilAlerts.showError("Error", "No se pudo abrir el formulario: " + e.getMessage());
        }
    }
}

package pe.edu.upeu.perfumeria.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.animation.PauseTransition;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pe.edu.upeu.perfumeria.implementacion.ClienteDaoImpl;
import pe.edu.upeu.perfumeria.implementacion.ProductoDaoImpl;
import pe.edu.upeu.perfumeria.implementacion.VentaDaoImpl;
import pe.edu.upeu.perfumeria.interfaces.IClienteDao;
import pe.edu.upeu.perfumeria.interfaces.IProductoDao;
import pe.edu.upeu.perfumeria.interfaces.IVentaDao;
import pe.edu.upeu.perfumeria.modelo.Cliente;
import pe.edu.upeu.perfumeria.modelo.DetalleVenta;
import pe.edu.upeu.perfumeria.modelo.Producto;
import pe.edu.upeu.perfumeria.modelo.Venta;
import pe.edu.upeu.perfumeria.utils.UtilAlerts;
import pe.edu.upeu.perfumeria.utils.UtilTabla;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaController {

    // ----- nueva venta -----
    @FXML
    private TextField txtBuscarCliente;
    @FXML
    private Label lblCliente;
    @FXML
    private TextField txtBuscarProducto;
    @FXML
    private ListView<Producto> lvProductos;
    @FXML
    private TextField txtCantidad;
    @FXML
    private Label lblStock, lblTotal;

    @FXML
    private TableView<DetalleVenta> tableCarrito;
    @FXML
    private TableColumn<DetalleVenta, String> colCarProducto, colCarPrecio, colCarSubtotal;
    @FXML
    private TableColumn<DetalleVenta, Integer> colCarCantidad;

    // ----- historial -----
    @FXML
    private TableView<Venta> tableVentas;
    @FXML
    private TableColumn<Venta, Number> colVNumero;
    @FXML
    private TableColumn<Venta, Integer> colVId;
    @FXML
    private TableColumn<Venta, String> colVFecha, colVCliente, colVTotal;

    private final IClienteDao clienteDao = new ClienteDaoImpl();
    private final IProductoDao productoDao = new ProductoDaoImpl();
    private final IVentaDao ventaDao = new VentaDaoImpl();

    private final ObservableList<DetalleVenta> carrito = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // carrito
        colCarProducto.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                cd.getValue().getProducto().getNombre() + " - " + cd.getValue().getProducto().getMarca()
                        + " " + cd.getValue().getProducto().getTipo()
                        + " " + cd.getValue().getProducto().getCapacidadMl() + "ml"));
        colCarCantidad.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getCantidad()));
        colCarPrecio.setCellValueFactory(cd -> new ReadOnlyStringWrapper(UtilTabla.soles(cd.getValue().getPrecioUnitario())));
        colCarSubtotal.setCellValueFactory(cd -> new ReadOnlyStringWrapper(UtilTabla.soles(cd.getValue().getSubtotal())));
        tableCarrito.setItems(carrito);
        // la columna del perfume se lleva la mayor parte del ancho
        colCarProducto.prefWidthProperty().bind(tableCarrito.widthProperty().multiply(0.50));
        colCarCantidad.prefWidthProperty().bind(tableCarrito.widthProperty().multiply(0.12));
        colCarPrecio.prefWidthProperty().bind(tableCarrito.widthProperty().multiply(0.17));
        colCarSubtotal.prefWidthProperty().bind(tableCarrito.widthProperty().multiply(0.17));

        // historial
        UtilTabla.columnaNumerada(colVNumero);
        colVId.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getIdVenta()));
        colVFecha.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getFechaVenta()));
        colVCliente.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getCliente().getNombreCompleto()));
        colVTotal.setCellValueFactory(cd -> new ReadOnlyStringWrapper(UtilTabla.soles(cd.getValue().getTotal())));

        configurarBuscadorProductos();
        limpiarProducto();
        cargarVentas();
        actualizarTotal();
    }

    private Cliente clienteVenta; // cliente encontrado para la venta en curso

    /** Busca por DNI (8 dígitos) o por ID, sin cargar la lista completa de clientes. */
    @FXML
    private void handleBuscarCliente() {
        String texto = txtBuscarCliente.getText().trim();
        if (!texto.matches("\\d+")) {
            UtilAlerts.showWarning("Búsqueda", "Ingrese solo números: el DNI (8 dígitos) o el ID del cliente.");
            return;
        }
        Cliente c = null;
        if (texto.length() == 8) {
            c = clienteDao.buscarPorDni(texto);
        }
        if (c == null && texto.length() <= 9) {
            c = clienteDao.buscarPorId(Integer.parseInt(texto));
        }
        if (c == null) {
            clienteVenta = null;
            lblCliente.setText("Cliente no encontrado");
            lblCliente.setStyle("-fx-text-fill: #B3261E; -fx-font-weight: bold;");
            return;
        }
        clienteVenta = c;
        lblCliente.setText("✔ " + c.getNombreCompleto() + "  (DNI " + c.getDni() + ")");
        lblCliente.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold;");
    }

    private void limpiarCliente() {
        clienteVenta = null;
        txtBuscarCliente.clear();
        lblCliente.setText("Ningún cliente seleccionado");
        lblCliente.setStyle("-fx-text-fill: #8A8478;");
    }

    // ---------- buscador de perfumes (por nombre o marca) ----------
    private static final int MAX_RESULTADOS = 15;
    private Producto productoVenta;      // perfume elegido para agregar
    private boolean actualizandoTexto;   // evita buscar al rellenar el campo por código
    private final PauseTransition retardo = new PauseTransition(Duration.millis(250));

    private void configurarBuscadorProductos() {
        lvProductos.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Producto p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p + "   ·   Stock: " + p.getStock());
            }
        });
        retardo.setOnFinished(e -> buscarProductos());
        txtBuscarProducto.textProperty().addListener((obs, anterior, texto) -> {
            if (actualizandoTexto) {
                return;
            }
            productoVenta = null;
            lblStock.setText("Stock: -");
            retardo.playFromStart();   // espera a que deje de escribir
        });
        txtBuscarProducto.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.DOWN && !lvProductos.getItems().isEmpty()) {
                lvProductos.requestFocus();
                lvProductos.getSelectionModel().selectFirst();
            } else if (ev.getCode() == KeyCode.ENTER && !lvProductos.getItems().isEmpty()) {
                elegirProducto(lvProductos.getItems().get(0));
            } else if (ev.getCode() == KeyCode.ESCAPE) {
                mostrarResultados(false);
            }
        });
        lvProductos.setOnMouseClicked(ev -> elegirProducto(lvProductos.getSelectionModel().getSelectedItem()));
        lvProductos.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                elegirProducto(lvProductos.getSelectionModel().getSelectedItem());
            }
        });
    }

    /** Consulta solo los primeros resultados en la BD: no se carga el catálogo completo. */
    private void buscarProductos() {
        String texto = txtBuscarProducto.getText().trim();
        if (texto.isEmpty()) {
            mostrarResultados(false);
            return;
        }
        List<Producto> resultados = productoDao.buscar(texto, MAX_RESULTADOS);
        lvProductos.setItems(FXCollections.observableArrayList(resultados));
        mostrarResultados(!resultados.isEmpty());
        if (resultados.isEmpty()) {
            lblStock.setText("Sin resultados");
        }
    }

    private void mostrarResultados(boolean visible) {
        lvProductos.setVisible(visible);
        lvProductos.setManaged(visible);
    }

    private void elegirProducto(Producto p) {
        if (p == null) {
            return;
        }
        productoVenta = p;
        actualizandoTexto = true;
        txtBuscarProducto.setText(p.getNombre() + " - " + p.getMarca() + " " + p.getTipo()
                + " " + p.getCapacidadMl() + "ml");
        actualizandoTexto = false;
        lblStock.setText("Stock: " + p.getStock());
        mostrarResultados(false);
        txtCantidad.requestFocus();
        txtCantidad.selectAll();
    }

    private void limpiarProducto() {
        productoVenta = null;
        actualizandoTexto = true;
        txtBuscarProducto.clear();
        actualizandoTexto = false;
        lblStock.setText("Stock: -");
        mostrarResultados(false);
    }

    private void cargarVentas() {
        tableVentas.setItems(FXCollections.observableArrayList(ventaDao.listarVentas()));
    }

    private double calcularTotal() {
        double total = carrito.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
        return Math.round(total * 100.0) / 100.0;
    }

    private void actualizarTotal() {
        lblTotal.setText(UtilTabla.soles(calcularTotal()));
    }

    @FXML
    private void handleAgregar() {
        Producto elegido = productoVenta;
        if (elegido == null) {
            UtilAlerts.showWarning("Perfume requerido", "Busque y seleccione un perfume de la lista.");
            return;
        }
        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException e) {
            UtilAlerts.showWarning("Cantidad inválida", "La cantidad debe ser un número entero.");
            return;
        }
        if (cantidad <= 0) {
            UtilAlerts.showWarning("Cantidad inválida", "La cantidad debe ser mayor a 0.");
            return;
        }

        // se consulta el stock actual en la BD
        Producto actual = productoDao.buscarPorId(elegido.getIdProducto());
        if (actual == null) {
            UtilAlerts.showError("Error", "El perfume ya no existe.");
            limpiarProducto();
            return;
        }
        DetalleVenta existente = carrito.stream()
                .filter(d -> d.getProducto().getIdProducto() == actual.getIdProducto())
                .findFirst().orElse(null);
        int yaEnCarrito = existente == null ? 0 : existente.getCantidad();
        if (cantidad + yaEnCarrito > actual.getStock()) {
            UtilAlerts.showError("Stock insuficiente",
                    "Stock disponible de " + actual.getNombre() + ": " + actual.getStock()
                            + " (ya tiene " + yaEnCarrito + " en la venta).");
            return;
        }

        if (existente != null) {
            existente.setCantidad(existente.getCantidad() + cantidad);
            existente.setSubtotal(existente.getCantidad() * existente.getPrecioUnitario());
            tableCarrito.refresh();
        } else {
            carrito.add(new DetalleVenta(actual, cantidad, actual.getPrecio()));
        }
        txtCantidad.setText("1");
        actualizarTotal();
        limpiarProducto();                 // listo para buscar el siguiente perfume
        txtBuscarProducto.requestFocus();
    }

    @FXML
    private void handleQuitar() {
        DetalleVenta seleccionado = tableCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            UtilAlerts.showWarning("Selección requerida", "Seleccione una línea de la venta para quitar.");
            return;
        }
        carrito.remove(seleccionado);
        actualizarTotal();
    }

    @FXML
    private void handleRegistrar() {
        Cliente cliente = clienteVenta;
        if (cliente == null) {
            UtilAlerts.showWarning("Cliente requerido", "Busque el cliente por DNI o ID antes de registrar la venta.");
            return;
        }
        if (carrito.isEmpty()) {
            UtilAlerts.showWarning("Venta vacía", "Agregue al menos un perfume a la venta.");
            return;
        }

        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setFechaVenta(LocalDate.now().toString());
        venta.setDetalles(new ArrayList<>(carrito));
        venta.setTotal(calcularTotal());

        if (ventaDao.registrarVenta(venta)) {
            UtilAlerts.showInfo("Venta registrada",
                    "Venta N° " + venta.getIdVenta() + " registrada.\nTotal: " + UtilTabla.soles(venta.getTotal()));
            carrito.clear();
            limpiarCliente();
            limpiarProducto();
            actualizarTotal();
            cargarVentas();
        } else {
            UtilAlerts.showError("Error",
                    "No se pudo registrar la venta (revise el stock). No se hizo ningún cambio.");
            limpiarProducto();
        }
    }

    @FXML
    private void handleVerDetalle() {
        Venta seleccionada = tableVentas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            UtilAlerts.showWarning("Selección requerida", "Seleccione una venta del historial.");
            return;
        }
        List<DetalleVenta> detalles = ventaDao.listarDetalles(seleccionada.getIdVenta());
        StringBuilder sb = new StringBuilder();
        sb.append("Cliente: ").append(seleccionada.getCliente().getNombreCompleto()).append("\n");
        sb.append("Fecha: ").append(seleccionada.getFechaVenta()).append("\n\n");
        for (DetalleVenta d : detalles) {
            sb.append(d.getCantidad()).append(" x ")
                    .append(d.getProducto().getNombre()).append(" (").append(d.getProducto().getMarca()).append(")")
                    .append("  ").append(UtilTabla.soles(d.getPrecioUnitario()))
                    .append("  =  ").append(UtilTabla.soles(d.getSubtotal())).append("\n");
        }
        sb.append("\nTOTAL: ").append(UtilTabla.soles(seleccionada.getTotal()));
        UtilAlerts.showInfo("Venta N° " + seleccionada.getIdVenta(), sb.toString());
    }
}

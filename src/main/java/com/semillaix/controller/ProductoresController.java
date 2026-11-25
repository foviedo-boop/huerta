package com.semillaix.controller;

import com.semillaix.model.Productor;
import com.semillaix.model.Session;
import com.semillaix.service.ApiService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

/**
 * Controlador de la pantalla principal de Productores (ProductoresView.fxml).
 * Requisitos del PDF:
 * - Perfil admin: alta, modificación y baja de productores y lotes.
 * - Perfil user: consulta de productores y lotes.
 * - Listado de productores con búsqueda por nombre o DNI.
 */
public class ProductoresController {

    @FXML private Label lblUsuarioInfo;
    @FXML private Label lblRolBadge;
    @FXML private Button btnCerrarSesion;

    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnNuevoProductor;

    @FXML private TableView<Productor> tblProductores;
    @FXML private TableColumn<Productor, String> colNombre;
    @FXML private TableColumn<Productor, String> colDniCuit;
    @FXML private TableColumn<Productor, String> colLocalidad;
    @FXML private TableColumn<Productor, String> colEstado;

    @FXML private Button btnVerFicha;
    @FXML private Button btnEditarProductor;
    @FXML private Button btnEliminarProductor;
    @FXML private Label lblTotalProductores;

    private final ApiService apiService = new ApiService();
    private final ObservableList<Productor> listaProductores = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarDatosUsuario();
        configurarPermisos();
        configurarTabla();
        configurarEventos();

        // Cargar lista inicial de productores
        cargarProductores("");
    }

    private void configurarDatosUsuario() {
        if (Session.getInstance().isLoggedIn()) {
            lblUsuarioInfo.setText("Usuario: " + Session.getInstance().getUsuarioActual().getEmail());
            String rol = Session.getInstance().getUsuarioActual().getRol().toUpperCase();
            lblRolBadge.setText(rol);
            if (!Session.getInstance().isAdmin()) {
                lblRolBadge.setStyle("-fx-background-color: #e3f2fd; -fx-text-fill: #1976d2; -fx-padding: 3 8 3 8; -fx-background-radius: 12; -fx-font-weight: bold;");
            }
        }
    }

    /**
     * Aplica las restricciones de perfil según el PDF:
     * - Admin: puede crear, modificar y eliminar.
     * - User: solo consulta y ver ficha.
     */
    private void configurarPermisos() {
        boolean esAdmin = Session.getInstance().isAdmin();

        btnNuevoProductor.setVisible(esAdmin);
        btnNuevoProductor.setManaged(esAdmin);

        btnEditarProductor.setVisible(esAdmin);
        btnEditarProductor.setManaged(esAdmin);

        btnEliminarProductor.setVisible(esAdmin);
        btnEliminarProductor.setManaged(esAdmin);
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombre()));
        colDniCuit.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDniCuit()));
        colLocalidad.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLocalidad()));
        colEstado.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEstado()));

        tblProductores.setItems(listaProductores);

        // Doble clic en una fila abre la ficha
        tblProductores.setRowFactory(tv -> {
            TableRow<Productor> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    abrirFichaProductor(row.getItem());
                }
            });
            return row;
        });
    }

    private void configurarEventos() {
        btnBuscar.setOnAction(e -> cargarProductores(txtBuscar.getText()));
        txtBuscar.setOnAction(e -> cargarProductores(txtBuscar.getText()));
        btnLimpiar.setOnAction(e -> {
            txtBuscar.clear();
            cargarProductores("");
        });

        btnNuevoProductor.setOnAction(e -> abrirFormularioProductor(null));
        btnEditarProductor.setOnAction(e -> {
            Productor seleccionado = tblProductores.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                abrirFormularioProductor(seleccionado);
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Selecciona un productor para modificar.");
            }
        });

        btnEliminarProductor.setOnAction(e -> eliminarProductorSeleccionado());

        btnVerFicha.setOnAction(e -> {
            Productor seleccionado = tblProductores.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                abrirFichaProductor(seleccionado);
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Selecciona un productor para ver su ficha.");
            }
        });

        btnCerrarSesion.setOnAction(e -> cerrarSesion());
    }

    public void cargarProductores(String busqueda) {
        new Thread(() -> {
            try {
                List<Productor> list = apiService.getProductores(busqueda);
                Platform.runLater(() -> {
                    listaProductores.setAll(list);
                    lblTotalProductores.setText("Total: " + list.size() + " productores");
                });
            } catch (Exception ex) {
                Platform.runLater(() -> mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la lista: " + ex.getMessage()));
            }
        }).start();
    }

    private void abrirFormularioProductor(Productor productorAEditar) {
        try {
            FXMLLoader loader = new FXMLLoader(obtenerUrlVista("ProductorFormView.fxml"));
            ProductorFormController controller = new ProductorFormController(this, productorAEditar);
            loader.setController(controller);
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle(productorAEditar == null ? "Alta de Productor" : "Modificar Productor");
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(tblProductores.getScene().getWindow());
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir el formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void abrirFichaProductor(Productor productor) {
        try {
            FXMLLoader loader = new FXMLLoader(obtenerUrlVista("ProductorFichaView.fxml"));
            ProductorFichaController controller = new ProductorFichaController(productor);
            loader.setController(controller);
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle("Ficha de Productor: " + productor.getNombre());
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(tblProductores.getScene().getWindow());
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la ficha: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void eliminarProductorSeleccionado() {
        Productor seleccionado = tblProductores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Selecciona un productor para eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar al productor " + seleccionado.getNombre() + "?");
        confirm.setContentText("Esta acción eliminará el productor de la base de datos.");
        Optional<ButtonType> res = confirm.showAndWait();

        if (res.isPresent() && res.get() == ButtonType.OK) {
            new Thread(() -> {
                try {
                    apiService.eliminarProductor(seleccionado.getId());
                    Platform.runLater(() -> {
                        listaProductores.remove(seleccionado);
                        lblTotalProductores.setText("Total: " + listaProductores.size() + " productores");
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Productor eliminado correctamente.");
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar: " + ex.getMessage()));
                }
            }).start();
        }
    }

    private void cerrarSesion() {
        Session.getInstance().logout();
        try {
            FXMLLoader loader = new FXMLLoader(obtenerUrlVista("LoginView.fxml"));
            loader.setController(new LoginController());
            Parent root = loader.load();

            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            stage.setTitle("SemillaIX - Iniciar Sesión");
            stage.setScene(new Scene(root, 480, 550));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private URL obtenerUrlVista(String nombreFxml) throws IOException {
        URL res = getClass().getResource("/com/semillaix/view/" + nombreFxml);
        if (res == null) {
            File f = new File("C:/Users/Usuario/Documents/navas1/" + nombreFxml);
            if (f.exists()) return f.toURI().toURL();
        }
        return res;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

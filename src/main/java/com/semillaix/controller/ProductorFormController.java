package com.semillaix.controller;

import com.semillaix.model.Productor;
import com.semillaix.service.ApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controlador para alta y modificación de productores (ProductorFormView.fxml).
 * Requisito del PDF: Formulario de alta de productor (solo admin).
 */
public class ProductorFormController {

    @FXML private Label lblTituloFormulario;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDniCuit;
    @FXML private TextField txtLocalidad;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private Label lblError;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private final ProductoresController padreController;
    private final Productor productorAEditar;
    private final ApiService apiService = new ApiService();

    public ProductorFormController(ProductoresController padreController, Productor productorAEditar) {
        this.padreController = padreController;
        this.productorAEditar = productorAEditar;
    }

    @FXML
    public void initialize() {
        cmbEstado.setItems(FXCollections.observableArrayList("ACTIVO", "INACTIVO"));
        cmbEstado.setValue("ACTIVO");
        lblError.setText("");

        if (productorAEditar != null) {
            lblTituloFormulario.setText("Modificar Productor");
            txtNombre.setText(productorAEditar.getNombre());
            txtDniCuit.setText(productorAEditar.getDniCuit());
            txtLocalidad.setText(productorAEditar.getLocalidad());
            if (productorAEditar.getEstado() != null) {
                cmbEstado.setValue(productorAEditar.getEstado());
            }
        }

        btnCancelar.setOnAction(e -> cerrarVentana());
        btnGuardar.setOnAction(e -> handleGuardar());
    }

    private void handleGuardar() {
        String nombre = txtNombre.getText().trim();
        String dniCuit = txtDniCuit.getText().trim();
        String localidad = txtLocalidad.getText().trim();
        String estado = cmbEstado.getValue();

        if (nombre.isEmpty() || dniCuit.isEmpty() || localidad.isEmpty()) {
            lblError.setText("Por favor, completa todos los campos obligatorios.");
            return;
        }

        btnGuardar.setDisable(true);
        lblError.setText("Guardando...");

        new Thread(() -> {
            try {
                if (productorAEditar == null) {
                    // Alta nuevo productor
                    Productor nuevo = new Productor(0, nombre, dniCuit, localidad, estado);
                    apiService.crearProductor(nuevo);
                } else {
                    // Modificación
                    productorAEditar.setNombre(nombre);
                    productorAEditar.setDniCuit(dniCuit);
                    productorAEditar.setLocalidad(localidad);
                    productorAEditar.setEstado(estado);
                    apiService.actualizarProductor(productorAEditar);
                }

                Platform.runLater(() -> {
                    if (padreController != null) {
                        padreController.cargarProductores("");
                    }
                    cerrarVentana();
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    lblError.setText(ex.getMessage() != null ? ex.getMessage() : "Error al guardar productor.");
                    btnGuardar.setDisable(false);
                });
            }
        }).start();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}

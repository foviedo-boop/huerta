package com.semillaix.controller;

import com.semillaix.model.Lote;
import com.semillaix.model.Productor;
import com.semillaix.service.ApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

/**
 * Controlador para registrar un nuevo lote o producto (LoteFormView.fxml).
 * Requisito del PDF:
 * - Alta de lote (user o admin, según regla del semillero).
 * - Campos: productor, tipo, cantidad, fecha.
 */
public class LoteFormController {

    @FXML private Label lblTituloLote;
    @FXML private Label lblProductorAsignado;
    @FXML private TextField txtTipo;
    @FXML private TextField txtCantidad;
    @FXML private DatePicker dpFecha;
    @FXML private Label lblError;
    @FXML private Button btnGuardarLote;
    @FXML private Button btnCancelar;

    private final ProductorFichaController fichaPadre;
    private final Productor productor;
    private final ApiService apiService = new ApiService();

    public LoteFormController(ProductorFichaController fichaPadre, Productor productor) {
        this.fichaPadre = fichaPadre;
        this.productor = productor;
    }

    @FXML
    public void initialize() {
        lblError.setText("");
        dpFecha.setValue(LocalDate.now());

        if (productor != null) {
            lblProductorAsignado.setText("Productor: " + productor.getNombre());
        }

        btnCancelar.setOnAction(e -> cerrarVentana());
        btnGuardarLote.setOnAction(e -> handleGuardar());
    }

    private void handleGuardar() {
        String tipo = txtTipo.getText().trim();
        String cantidadStr = txtCantidad.getText().trim();
        LocalDate fecha = dpFecha.getValue();

        if (tipo.isEmpty() || cantidadStr.isEmpty() || fecha == null) {
            lblError.setText("Por favor, completa todos los campos.");
            return;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(cantidadStr);
            if (cantidad <= 0) {
                lblError.setText("La cantidad debe ser mayor a cero.");
                return;
            }
        } catch (NumberFormatException ex) {
            lblError.setText("La cantidad debe ser un número válido.");
            return;
        }

        btnGuardarLote.setDisable(true);
        lblError.setText("Guardando lote...");

        new Thread(() -> {
            try {
                Lote nuevoLote = new Lote(
                        0,
                        productor != null ? productor.getId() : 0,
                        productor != null ? productor.getNombre() : "",
                        tipo,
                        cantidad,
                        fecha.toString()
                );

                apiService.crearLote(nuevoLote);

                Platform.runLater(() -> {
                    if (fichaPadre != null) {
                        fichaPadre.cargarLotes();
                    }
                    cerrarVentana();
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    lblError.setText(ex.getMessage() != null ? ex.getMessage() : "Error al registrar lote.");
                    btnGuardarLote.setDisable(false);
                });
            }
        }).start();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}

package com.semillaix.controller;

import com.semillaix.model.Lote;
import com.semillaix.model.Productor;
import com.semillaix.service.ApiService;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
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

/**
 * Controlador de la Ficha del Productor (ProductorFichaView.fxml).
 * Requisitos del PDF:
 * - Ficha del productor con sus datos y lotes asociados.
 * - Listado de lotes disponibles del productor.
 * - Botón para dar de alta nuevo lote.
 */
public class ProductorFichaController {

    @FXML private Label lblNombreProductor;
    @FXML private Label lblDniCuit;
    @FXML private Label lblLocalidad;
    @FXML private Label lblEstado;

    @FXML private Button btnAgregarLote;
    @FXML private TableView<Lote> tblLotesProductor;
    @FXML private TableColumn<Lote, Number> colLoteId;
    @FXML private TableColumn<Lote, String> colTipo;
    @FXML private TableColumn<Lote, Number> colCantidad;
    @FXML private TableColumn<Lote, String> colFecha;

    @FXML private Button btnCerrar;

    private final Productor productor;
    private final ApiService apiService = new ApiService();
    private final ObservableList<Lote> listaLotes = FXCollections.observableArrayList();

    public ProductorFichaController(Productor productor) {
        this.productor = productor;
    }

    @FXML
    public void initialize() {
        if (productor != null) {
            lblNombreProductor.setText(productor.getNombre());
            lblDniCuit.setText(productor.getDniCuit());
            lblLocalidad.setText(productor.getLocalidad());
            lblEstado.setText(productor.getEstado() != null ? productor.getEstado() : "ACTIVO");
        }

        colLoteId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));
        colCantidad.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getCantidad()));
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha()));

        tblLotesProductor.setItems(listaLotes);

        btnCerrar.setOnAction(e -> {
            Stage stage = (Stage) btnCerrar.getScene().getWindow();
            stage.close();
        });

        btnAgregarLote.setOnAction(e -> abrirFormularioLote());

        cargarLotes();
    }

    public void cargarLotes() {
        if (productor == null) return;
        new Thread(() -> {
            try {
                List<Lote> list = apiService.getLotesPorProductor(productor.getId());
                Platform.runLater(() -> listaLotes.setAll(list));
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    Alert a = new Alert(Alert.AlertType.ERROR, "Error al cargar lotes: " + ex.getMessage(), ButtonType.OK);
                    a.showAndWait();
                });
            }
        }).start();
    }

    private void abrirFormularioLote() {
        try {
            FXMLLoader loader = new FXMLLoader(obtenerUrlVista("LoteFormView.fxml"));
            LoteFormController controller = new LoteFormController(this, productor);
            loader.setController(controller);
            Parent root = loader.load();

            Stage modal = new Stage();
            modal.setTitle("Registrar Lote - " + productor.getNombre());
            modal.initModality(Modality.WINDOW_MODAL);
            modal.initOwner(btnAgregarLote.getScene().getWindow());
            modal.setScene(new Scene(root));
            modal.showAndWait();
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
}

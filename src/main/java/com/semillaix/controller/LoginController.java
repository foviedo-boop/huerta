package com.semillaix.controller;

import com.semillaix.model.Session;
import com.semillaix.model.Usuario;
import com.semillaix.service.ApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Controlador de la pantalla de Login (LoginView.fxml).
 * Requisitos del PDF:
 * - Login con email y clave. Solo usuarios registrados acceden.
 * - El servidor valida y devuelve un JWT.
 * - Guarda el token en la sesión cliente.
 */
public class LoginController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private Button btnLogin;

    private final ApiService apiService = new ApiService();

    @FXML
    public void initialize() {
        lblError.setText("");

        // Permitir presionar Enter en cualquiera de los dos campos para iniciar sesión
        txtEmail.setOnAction(e -> handleLogin());
        txtPassword.setOnAction(e -> handleLogin());
        btnLogin.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            lblError.setText("Por favor, ingresa el correo y la contraseña.");
            return;
        }

        lblError.setText("Iniciando sesión...");
        btnLogin.setDisable(true);

        // Ejecutar en hilo secundario para no bloquear la interfaz gráfica
        new Thread(() -> {
            try {
                Usuario usuario = apiService.login(email, password);

                Platform.runLater(() -> {
                    lblError.setText("");
                    btnLogin.setDisable(false);
                    // Transicionar a la pantalla principal de Productores
                    abrirPantallaPrincipal();
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    lblError.setText(ex.getMessage() != null ? ex.getMessage() : "Error al iniciar sesión.");
                    btnLogin.setDisable(false);
                });
            }
        }).start();
    }

    private void abrirPantallaPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader();
            // Buscar recurso en resources o archivo directo
            URL fxmlUrl = getClass().getResource("/com/semillaix/view/ProductoresView.fxml");
            if (fxmlUrl == null) {
                File file = new File("C:/Users/Usuario/Documents/navas1/ProductoresView.fxml");
                if (file.exists()) {
                    fxmlUrl = file.toURI().toURL();
                }
            }

            loader.setLocation(fxmlUrl);
            loader.setController(new ProductoresController());
            Parent root = loader.load();

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setTitle("SemillaIX - Gestión de Productores [" + Session.getInstance().getUsuarioActual().getRol().toUpperCase() + "]");
            stage.setScene(new Scene(root, 950, 650));
            stage.centerOnScreen();
        } catch (IOException e) {
            lblError.setText("Error al cargar pantalla principal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

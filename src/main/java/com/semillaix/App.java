package com.semillaix;

import com.semillaix.controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

/**
 * Punto de entrada principal de la aplicación JavaFX SemillaIX.
 * Inicia mostrando la pantalla de Login con autenticación JWT.
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        
        // Carga la vista de Login
        URL fxmlUrl = getClass().getResource("/com/semillaix/view/LoginView.fxml");
        if (fxmlUrl == null) {
            File localFile = new File("C:/Users/Usuario/Documents/navas1/LoginView.fxml");
            if (localFile.exists()) {
                fxmlUrl = localFile.toURI().toURL();
            }
        }

        loader.setLocation(fxmlUrl);
        loader.setController(new LoginController());
        Parent root = loader.load();

        primaryStage.setTitle("🌱 SemillaIX - Iniciar Sesión");
        primaryStage.setScene(new Scene(root, 480, 550));
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

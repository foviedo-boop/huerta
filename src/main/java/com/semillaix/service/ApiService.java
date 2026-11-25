package com.semillaix.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.semillaix.model.Lote;
import com.semillaix.model.Productor;
import com.semillaix.model.Session;
import com.semillaix.model.Usuario;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio encargado de la comunicación HTTP REST con el backend Node.js + Express.
 * Cumple con los requisitos del PDF:
 * - Comunicación mediante API REST (GET, POST, PUT, DELETE).
 * - Seguridad: Envío de token JWT en el header Authorization: Bearer <token>.
 * - Rutas de escritura protegidas para rol admin.
 */
public class ApiService {
    // URL base de la API REST de Node.js (por defecto en localhost:3000)
    private static final String BASE_URL = "http://localhost:3000/api";
    
    private final HttpClient client;
    private final Gson gson;

    public ApiService() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(6))
                .build();
        this.gson = new Gson();
    }

    // ==========================================
    // AUTENTICACIÓN (LOGIN + JWT)
    // ==========================================

    /**
     * Inicia sesión con email y contraseña contra la API de Node.js.
     * Retorna el objeto Usuario con el JWT token obtenido.
     */
    public Usuario login(String email, String password) throws IOException, InterruptedException {
        JsonObject bodyJson = new JsonObject();
        bodyJson.addProperty("email", email);
        bodyJson.addProperty("password", password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonObject resObj = gson.fromJson(response.body(), JsonObject.class);
            String token = resObj.has("token") ? resObj.get("token").getAsString() : "";
            JsonObject userObj = resObj.getAsJsonObject("usuario");

            Usuario usuario = new Usuario();
            if (userObj != null) {
                usuario.setId(userObj.has("id") ? userObj.get("id").getAsInt() : 0);
                usuario.setEmail(userObj.has("email") ? userObj.get("email").getAsString() : email);
                usuario.setNombre(userObj.has("nombre") ? userObj.get("nombre").getAsString() : "");
                usuario.setRol(userObj.has("rol") ? userObj.get("rol").getAsString() : "user");
            }
            usuario.setToken(token);

            // Almacenar en la sesión en memoria
            Session.getInstance().setUsuarioActual(usuario);
            return usuario;
        } else {
            String errorMsg = "Error de autenticación (" + response.statusCode() + ")";
            try {
                JsonObject errorJson = gson.fromJson(response.body(), JsonObject.class);
                if (errorJson.has("mensaje")) {
                    errorMsg = errorJson.get("mensaje").getAsString();
                } else if (errorJson.has("error")) {
                    errorMsg = errorJson.get("error").getAsString();
                }
            } catch (Exception ignored) {}
            throw new IOException(errorMsg);
        }
    }

    // ==========================================
    // PRODUCTORES (GET, POST, PUT, DELETE)
    // ==========================================

    /**
     * Obtiene la lista de productores, opcionalmente filtrada por nombre o DNI/CUIT.
     */
    public List<Productor> getProductores(String busqueda) throws IOException, InterruptedException {
        String endpoint = BASE_URL + "/productores";
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            endpoint += "?q=" + URLEncoder.encode(busqueda.trim(), StandardCharsets.UTF_8);
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Accept", "application/json")
                .GET();

        agregarHeaderAuth(builder);

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            Type listType = new TypeToken<ArrayList<Productor>>(){}.getType();
            return gson.fromJson(response.body(), listType);
        } else {
            throw new IOException("Error al consultar productores: HTTP " + response.statusCode());
        }
    }

    /**
     * Da de alta un nuevo productor (solo perfil admin).
     */
    public Productor crearProductor(Productor productor) throws IOException, InterruptedException {
        String body = gson.toJson(productor);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/productores"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));

        agregarHeaderAuth(builder);

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return gson.fromJson(response.body(), Productor.class);
        } else {
            throw new IOException(obtenerMensajeError(response, "Error al crear productor"));
        }
    }

    /**
     * Modifica los datos de un productor existente (solo perfil admin).
     */
    public Productor actualizarProductor(Productor productor) throws IOException, InterruptedException {
        String body = gson.toJson(productor);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/productores/" + productor.getId()))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));

        agregarHeaderAuth(builder);

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Productor.class);
        } else {
            throw new IOException(obtenerMensajeError(response, "Error al actualizar productor"));
        }
    }

    /**
     * Elimina un productor por ID (solo perfil admin).
     */
    public boolean eliminarProductor(int productorId) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/productores/" + productorId))
                .DELETE();

        agregarHeaderAuth(builder);

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200 || response.statusCode() == 204) {
            return true;
        } else {
            throw new IOException(obtenerMensajeError(response, "Error al eliminar productor"));
        }
    }

    // ==========================================
    // LOTES (GET, POST)
    // ==========================================

    /**
     * Obtiene los lotes asociados a un productor específico.
     */
    public List<Lote> getLotesPorProductor(int productorId) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/productores/" + productorId + "/lotes"))
                .header("Accept", "application/json")
                .GET();

        agregarHeaderAuth(builder);

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            Type listType = new TypeToken<ArrayList<Lote>>(){}.getType();
            return gson.fromJson(response.body(), listType);
        } else {
            throw new IOException("Error al consultar lotes: HTTP " + response.statusCode());
        }
    }

    /**
     * Registra un nuevo lote asociado a un productor (user o admin).
     */
    public Lote crearLote(Lote lote) throws IOException, InterruptedException {
        String body = gson.toJson(lote);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/lotes"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));

        agregarHeaderAuth(builder);

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return gson.fromJson(response.body(), Lote.class);
        } else {
            throw new IOException(obtenerMensajeError(response, "Error al registrar lote"));
        }
    }

    // ==========================================
    // UTILIDADES PRIVADAS
    // ==========================================

    /**
     * Agrega el header Authorization con el token JWT si el usuario está autenticado.
     */
    private void agregarHeaderAuth(HttpRequest.Builder builder) {
        String token = Session.getInstance().getToken();
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
    }

    private String obtenerMensajeError(HttpResponse<String> response, String fallback) {
        try {
            JsonObject json = gson.fromJson(response.body(), JsonObject.class);
            if (json.has("mensaje")) return json.get("mensaje").getAsString();
            if (json.has("error")) return json.get("error").getAsString();
        } catch (Exception ignored) {}
        return fallback + " (HTTP " + response.statusCode() + ")";
    }
}

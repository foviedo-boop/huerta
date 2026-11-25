package com.semillaix.model;

/**
 * Mantiene la sesión del usuario actual en memoria durante la ejecución de la app.
 * Almacena el token JWT y los datos del perfil ('admin' o 'user').
 * Requisito del PDF: La aplicación Java no guarda datos localmente (salvo JWT en memoria).
 */
public class Session {
    private static Session instance;
    private Usuario usuarioActual;

    private Session() {}

    public static synchronized Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public String getToken() {
        return usuarioActual != null ? usuarioActual.getToken() : null;
    }

    public boolean isLoggedIn() {
        return usuarioActual != null && usuarioActual.getToken() != null && !usuarioActual.getToken().isEmpty();
    }

    public boolean isAdmin() {
        return usuarioActual != null && usuarioActual.isAdmin();
    }

    public void logout() {
        this.usuarioActual = null;
    }
}

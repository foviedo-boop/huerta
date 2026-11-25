package com.semillaix.model;

/**
 * Representa el usuario autenticado en el sistema.
 * El rol puede ser "admin" o "user".
 */
public class Usuario {
    private int id;
    private String email;
    private String nombre;
    private String rol; // "admin" o "user"
    private String token; // Token JWT entregado por la API

    public Usuario() {}

    public Usuario(int id, String email, String nombre, String rol, String token) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(this.rol);
    }
}

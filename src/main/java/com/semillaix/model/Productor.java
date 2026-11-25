package com.semillaix.model;

/**
 * Entidad principal Productor según los requisitos del PDF:
 * nombre, DNI o CUIT, localidad, estado.
 */
public class Productor {
    private int id;
    private String nombre;
    private String dniCuit;
    private String localidad;
    private String estado; // "ACTIVO", "INACTIVO"

    public Productor() {}

    public Productor(int id, String nombre, String dniCuit, String localidad, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.dniCuit = dniCuit;
        this.localidad = localidad;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDniCuit() {
        return dniCuit;
    }

    public void setDniCuit(String dniCuit) {
        this.dniCuit = dniCuit;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return nombre + " (" + dniCuit + ")";
    }
}

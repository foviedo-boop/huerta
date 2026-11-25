package com.semillaix.model;

/**
 * Entidad secundaria Lote o Producto según los requisitos del PDF:
 * productor, tipo, cantidad, fecha.
 */
public class Lote {
    private int id;
    private int productorId;
    private String productorNombre;
    private String tipo;      // Ej: Tomate Platense, Albahaca
    private double cantidad;  // Cantidad en unidades, kg o paquetes
    private String fecha;     // Formato YYYY-MM-DD

    public Lote() {}

    public Lote(int id, int productorId, String productorNombre, String tipo, double cantidad, String fecha) {
        this.id = id;
        this.productorId = productorId;
        this.productorNombre = productorNombre;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductorId() {
        return productorId;
    }

    public void setProductorId(int productorId) {
        this.productorId = productorId;
    }

    public String getProductorNombre() {
        return productorNombre;
    }

    public void setProductorNombre(String productorNombre) {
        this.productorNombre = productorNombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}

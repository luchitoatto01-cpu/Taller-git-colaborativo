package pe.edu.upeu.perfumeria.modelo;

import java.util.Locale;

public class Producto {
    private int idProducto;
    private String nombre;
    private String marca;
    private String tipo; // EDT, EDP, Parfum
    private int capacidadMl;
    private double precio;
    private int stock;

    public Producto() {}

    public Producto(int idProducto, String nombre, String marca, String tipo, int capacidadMl, double precio, int stock) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.marca = marca;
        this.tipo = tipo;
        this.capacidadMl = capacidadMl;
        this.precio = precio;
        this.stock = stock;
    }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public int getCapacidadMl() { return capacidadMl; }
    public void setCapacidadMl(int capacidadMl) { this.capacidadMl = capacidadMl; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        return nombre + " - " + marca + " " + tipo + " (" + capacidadMl + "ml) - "
                + String.format(Locale.US, "S/ %.2f", precio);
    }
}

package pe.edu.upeu.perfumeria.modelo;

import java.util.ArrayList;
import java.util.List;

public class Venta {
    private int idVenta;
    private Cliente cliente;
    private String fechaVenta;
    private double total;
    private List<DetalleVenta> detalles = new ArrayList<>();

    public Venta() {}

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public String getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(String fechaVenta) { this.fechaVenta = fechaVenta; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }
}

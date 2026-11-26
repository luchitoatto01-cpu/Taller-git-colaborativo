package pe.edu.upeu.perfumeria.interfaces;

import pe.edu.upeu.perfumeria.modelo.DetalleVenta;
import pe.edu.upeu.perfumeria.modelo.Venta;

import java.util.List;

public interface IVentaDao {
    boolean registrarVenta(Venta venta);
    List<Venta> listarVentas();
    List<DetalleVenta> listarDetalles(int idVenta);
}

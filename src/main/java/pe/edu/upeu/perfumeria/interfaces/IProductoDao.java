package pe.edu.upeu.perfumeria.interfaces;

import pe.edu.upeu.perfumeria.modelo.Producto;

import java.util.List;

public interface IProductoDao {
    boolean insertar(Producto producto);
    boolean actualizar(Producto producto);
    boolean eliminar(int idProducto);
    List<Producto> listar();
    /** Perfumes con stock cuyo nombre o marca contiene el texto (máx. {@code limite}). */
    List<Producto> buscar(String texto, int limite);
    Producto buscarPorId(int idProducto);
    boolean actualizarStock(int idProducto, int nuevoStock);
}

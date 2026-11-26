package pe.edu.upeu.perfumeria.test;

import pe.edu.upeu.perfumeria.implementacion.ClienteDaoImpl;
import pe.edu.upeu.perfumeria.implementacion.ProductoDaoImpl;
import pe.edu.upeu.perfumeria.implementacion.VentaDaoImpl;
import pe.edu.upeu.perfumeria.interfaces.IClienteDao;
import pe.edu.upeu.perfumeria.interfaces.IProductoDao;
import pe.edu.upeu.perfumeria.interfaces.IVentaDao;
import pe.edu.upeu.perfumeria.modelo.Cliente;
import pe.edu.upeu.perfumeria.modelo.DetalleVenta;
import pe.edu.upeu.perfumeria.modelo.Producto;
import pe.edu.upeu.perfumeria.modelo.Venta;

import java.time.LocalDate;
import java.util.List;

/**
 * Carga datos de ejemplo (solo si las tablas están vacías) para ver algo
 * en la aplicación y en DB Browser. Se puede ejecutar varias veces sin duplicar.
 */
public class TestDatos {

    public static void main(String[] args) {
        IClienteDao clienteDao = new ClienteDaoImpl();
        IProductoDao productoDao = new ProductoDaoImpl();
        IVentaDao ventaDao = new VentaDaoImpl();

        if (clienteDao.listar().isEmpty()) {
            clienteDao.insertar(new Cliente(0, "María", "Torres", "70123456", "987654321"));
            clienteDao.insertar(new Cliente(0, "Carlos", "Quispe", "45896321", "912345678"));
            clienteDao.insertar(new Cliente(0, "Lucía", "Ramos", "60234871", null));
            System.out.println("Clientes de ejemplo insertados");
        }

        if (productoDao.listar().isEmpty()) {
            productoDao.insertar(new Producto(0, "Sauvage", "Dior", "EDT", 100, 480.00, 12));
            productoDao.insertar(new Producto(0, "Bleu de Chanel", "Chanel", "EDP", 100, 560.00, 8));
            productoDao.insertar(new Producto(0, "Good Girl", "Carolina Herrera", "EDP", 80, 430.00, 10));
            productoDao.insertar(new Producto(0, "Light Blue", "Dolce & Gabbana", "EDT", 50, 290.00, 4));
            productoDao.insertar(new Producto(0, "La Vie Est Belle", "Lancôme", "EDP", 75, 410.00, 15));
            System.out.println("Perfumes de ejemplo insertados");
        }

        if (ventaDao.listarVentas().isEmpty()) {
            List<Cliente> clientes = clienteDao.listar();
            List<Producto> productos = productoDao.listar();
            Venta v = new Venta();
            v.setCliente(clientes.get(0));
            v.setFechaVenta(LocalDate.now().toString());
            DetalleVenta d1 = new DetalleVenta(productos.get(0), 1, productos.get(0).getPrecio());
            DetalleVenta d2 = new DetalleVenta(productos.get(1), 2, productos.get(1).getPrecio());
            v.getDetalles().add(d1);
            v.getDetalles().add(d2);
            v.setTotal(d1.getSubtotal() + d2.getSubtotal());
            System.out.println(ventaDao.registrarVenta(v) ? "Venta de ejemplo registrada" : "No se pudo registrar la venta");
        }
        System.out.println("Listo. Abre DB Browser para ver los datos.");
    }
}

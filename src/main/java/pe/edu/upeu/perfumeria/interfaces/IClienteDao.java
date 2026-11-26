package pe.edu.upeu.perfumeria.interfaces;

import pe.edu.upeu.perfumeria.modelo.Cliente;

import java.util.List;

public interface IClienteDao {
    boolean insertar(Cliente cliente);
    boolean actualizar(Cliente cliente);
    boolean eliminar(int idCliente);
    List<Cliente> listar();
    Cliente buscarPorId(int idCliente);
    Cliente buscarPorDni(String dni);
}

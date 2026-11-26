package pe.edu.upeu.perfumeria.test;

import pe.edu.upeu.perfumeria.utils.ConexionDB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class TestConexion {

    public static void testConexion() {
        try (Connection cn = ConexionDB.getConexion();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' ORDER BY name")) {
            System.out.println("Conexión correcta");
            System.out.println("Archivo de la BD: " + ConexionDB.getRutaAbsoluta());
            System.out.println("Tablas encontradas:");
            while (rs.next()) {
                System.out.println("  - " + rs.getString("name"));
            }
        } catch (Exception e) {
            System.out.println("Error de conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        testConexion();
    }
}

package dao;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL      = "jdbc:mysql://localhost:3306/logiRoute?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO  = "root";
    private static final String PASSWORD = "tu_contraseña_aqui"; // cambia esto

    public static Connection getConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado", e);
        }
    }
}
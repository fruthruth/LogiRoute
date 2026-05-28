package dao;

import model.Repartidor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepartidorDAO {

    // Listar todos
    public List<Repartidor> listarTodos() {
        List<Repartidor> lista = new ArrayList<>();
        String sql = "SELECT * FROM repartidores";
        try (Connection con = ConexionBD.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Repartidor r = new Repartidor();
                r.setId(rs.getInt("id"));
                r.setNombre(rs.getString("nombre"));
                r.setEmail(rs.getString("email"));
                r.setTelefono(rs.getString("telefono"));
                r.setVehiculo(rs.getString("vehiculo"));
                r.setEstado(rs.getString("estado"));
                lista.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Obtener primer repartidor disponible
    public Repartidor obtenerDisponible() {
        String sql = "SELECT * FROM repartidores WHERE estado = 'DISPONIBLE' LIMIT 1";
        try (Connection con = ConexionBD.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                Repartidor r = new Repartidor();
                r.setId(rs.getInt("id"));
                r.setNombre(rs.getString("nombre"));
                r.setEstado(rs.getString("estado"));
                return r;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Asignar pedido a repartidor
    public boolean asignarPedido(int pedidoId, int repartidorId) {
        String sqlPedido = "UPDATE pedidos SET repartidor_id = ?, estado = 'EN_TRANSITO' WHERE id = ?";
        String sqlRep    = "UPDATE repartidores SET estado = 'OCUPADO' WHERE id = ?";
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            PreparedStatement ps1 = con.prepareStatement(sqlPedido);
            ps1.setInt(1, repartidorId);
            ps1.setInt(2, pedidoId);
            ps1.executeUpdate();

            PreparedStatement ps2 = con.prepareStatement(sqlRep);
            ps2.setInt(1, repartidorId);
            ps2.executeUpdate();

            con.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Login repartidor
    public Repartidor login(String email, String password) {
        String sql = "SELECT * FROM repartidores WHERE email = ? AND password = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Repartidor r = new Repartidor();
                r.setId(rs.getInt("id"));
                r.setNombre(rs.getString("nombre"));
                r.setEmail(rs.getString("email"));
                r.setEstado(rs.getString("estado"));
                return r;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
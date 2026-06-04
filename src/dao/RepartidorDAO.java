package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Repartidor;

public class RepartidorDAO {

    public List<Repartidor> listarTodos() {
        List<Repartidor> lista = new ArrayList<>();
        String sql = "SELECT * FROM repartidores ORDER BY nombre";

        try (Connection con = ConexionBD.getConexion();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearRepartidor(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar repartidores: " + e.getMessage());
        }
        return lista;
    }

    public Repartidor obtenerDisponible() {
        String sql = "SELECT * FROM repartidores WHERE estado = 'DISPONIBLE' ORDER BY id LIMIT 1";

        try (Connection con = ConexionBD.getConexion();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) return mapearRepartidor(rs);
        } catch (SQLException e) {
            System.out.println("Error al obtener repartidor disponible: " + e.getMessage());
        }
        return null;
    }

    public boolean asignarPedido(int pedidoId, int repartidorId) {
        String sqlPedido = "UPDATE pedidos SET repartidor_id = ?, estado = 'EN_TRANSITO' WHERE id = ?";
        String sqlRep = "UPDATE repartidores SET estado = 'OCUPADO' WHERE id = ?";

        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps1 = con.prepareStatement(sqlPedido);
                PreparedStatement ps2 = con.prepareStatement(sqlRep)) {

                ps1.setInt(1, repartidorId);
                ps1.setInt(2, pedidoId);
                int pedidosActualizados = ps1.executeUpdate();

                ps2.setInt(1, repartidorId);
                int repartidoresActualizados = ps2.executeUpdate();

                if (pedidosActualizados > 0 && repartidoresActualizados > 0) {
                    con.commit();
                    return true;
                }
                con.rollback();
                return false;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Error al asignar pedido: " + e.getMessage());
            return false;
        }
    }

    public Repartidor login(String email, String password) {
        String sql = "SELECT * FROM repartidores WHERE email = ? AND password = ?";

        try (Connection con = ConexionBD.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearRepartidor(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error en login de repartidor: " + e.getMessage());
        }
        return null;
    }

    private Repartidor mapearRepartidor(ResultSet rs) throws SQLException {
        Repartidor r = new Repartidor();
        r.setId(rs.getInt("id"));
        r.setNombre(rs.getString("nombre"));
        r.setEmail(rs.getString("email"));
        r.setPassword(rs.getString("password"));
        r.setTelefono(rs.getString("telefono"));
        r.setVehiculo(rs.getString("vehiculo"));
        r.setEstado(rs.getString("estado"));
        return r;
    }
}

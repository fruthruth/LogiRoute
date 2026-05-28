package dao;

import model.Pedido;
import model.Repartidor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PedidoDAO {

    // Obtener todos los pedidos
    public List<Pedido> listarTodos() {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT p.*, r.nombre as repartidor_nombre FROM pedidos p " +
                     "LEFT JOIN repartidores r ON p.repartidor_id = r.id";
        try (Connection con = ConexionBD.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Pedido p = new Pedido();
                p.setId(rs.getInt("id"));
                p.setCodigoSeguimiento(rs.getString("codigo_seguimiento"));
                p.setNombreCliente(rs.getString("nombre_cliente"));
                p.setDireccionEntrega(rs.getString("direccion_entrega"));
                p.setTelefono(rs.getString("telefono"));
                p.setEstado(rs.getString("estado"));

                String nomRep = rs.getString("repartidor_nombre");
                if (nomRep != null) {
                    Repartidor r = new Repartidor();
                    r.setNombre(nomRep);
                    p.setRepartidor(r);
                }
                pedidos.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pedidos;
    }

    // Crear nuevo pedido
    public boolean crear(Pedido pedido) {
        String codigo = "LR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String sql = "INSERT INTO pedidos (codigo_seguimiento, nombre_cliente, direccion_entrega, telefono, estado) " +
                     "VALUES (?, ?, ?, ?, 'PENDIENTE')";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ps.setString(2, pedido.getNombreCliente());
            ps.setString(3, pedido.getDireccionEntrega());
            ps.setString(4, pedido.getTelefono());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Buscar por código de seguimiento
    public Pedido buscarPorCodigo(String codigo) {
        String sql = "SELECT p.*, r.nombre as repartidor_nombre FROM pedidos p " +
                     "LEFT JOIN repartidores r ON p.repartidor_id = r.id " +
                     "WHERE p.codigo_seguimiento = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Pedido p = new Pedido();
                p.setId(rs.getInt("id"));
                p.setCodigoSeguimiento(rs.getString("codigo_seguimiento"));
                p.setNombreCliente(rs.getString("nombre_cliente"));
                p.setDireccionEntrega(rs.getString("direccion_entrega"));
                p.setEstado(rs.getString("estado"));

                String nomRep = rs.getString("repartidor_nombre");
                if (nomRep != null) {
                    Repartidor r = new Repartidor();
                    r.setNombre(nomRep);
                    p.setRepartidor(r);
                }
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Cambiar estado
    public boolean cambiarEstado(int id, String nuevoEstado) {
        String sql = "UPDATE pedidos SET estado = ? WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Contar por estado (para el panel)
    public int contarPorEstado(String estado) {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE estado = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
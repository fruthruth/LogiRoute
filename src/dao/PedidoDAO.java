package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import model.Pedido;
import model.Repartidor;

public class PedidoDAO {

    public List<Pedido> listarTodos() {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT p.*, r.nombre AS repartidor_nombre FROM pedidos p LEFT JOIN repartidores r ON p.repartidor_id = r.id ORDER BY p.fecha_creacion DESC";

        try (Connection con = ConexionBD.getConexion();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                pedidos.add(mapearPedido(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar pedidos: " + e.getMessage());
        }
        return pedidos;
    }

    public boolean crear(Pedido pedido) {
        String codigo = "LR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String sql = "INSERT INTO pedidos (codigo_seguimiento, nombre_cliente, direccion_entrega, telefono, estado) VALUES (?, ?, ?, ?, 'PENDIENTE')";

        try (Connection con = ConexionBD.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ps.setString(2, pedido.getNombreCliente());
            ps.setString(3, pedido.getDireccionEntrega());
            ps.setString(4, pedido.getTelefono());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al crear pedido: " + e.getMessage());
            return false;
        }
    }

    public Pedido buscarPorCodigo(String codigo) {
        String sql = "SELECT p.*, r.nombre AS repartidor_nombre FROM pedidos p LEFT JOIN repartidores r ON p.repartidor_id = r.id WHERE p.codigo_seguimiento = ?";

        try (Connection con = ConexionBD.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearPedido(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar pedido: " + e.getMessage());
        }
        return null;
    }

    public boolean cambiarEstado(int id, String nuevoEstado) {
        String sql = "UPDATE pedidos SET estado = ? WHERE id = ?";

        try (Connection con = ConexionBD.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al cambiar estado: " + e.getMessage());
            return false;
        }
    }

    public int contarPorEstado(String estado) {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE estado = ?";

        try (Connection con = ConexionBD.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error al contar pedidos: " + e.getMessage());
        }
        return 0;
    }

    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setId(rs.getInt("id"));
        p.setCodigoSeguimiento(rs.getString("codigo_seguimiento"));
        p.setNombreCliente(rs.getString("nombre_cliente"));
        p.setDireccionEntrega(rs.getString("direccion_entrega"));
        p.setTelefono(rs.getString("telefono"));
        p.setEstado(rs.getString("estado"));

        Timestamp fecha = rs.getTimestamp("fecha_creacion");
        if (fecha != null) p.setFechaCreacion(fecha.toLocalDateTime());

        int repartidorId = rs.getInt("repartidor_id");
        String nombreRepartidor = rs.getString("repartidor_nombre");
        if (!rs.wasNull() && nombreRepartidor != null) {
            Repartidor r = new Repartidor();
            r.setId(repartidorId);
            r.setNombre(nombreRepartidor);
            p.setRepartidor(r);
        }
        return p;
    }
}

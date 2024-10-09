package pousada;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BancoDadosUtil {

    public static Connection getConnection() throws SQLException {
        return ConexaoBanco.getConnection();
    }

    // Método para obter a lista de clientes com status de check-in e check-out
    public static List<String[]> obterClientesComStatus() throws SQLException {
        List<String[]> clientes = new ArrayList<>();
        String sql = "SELECT u.id, u.nome, r.data_checkin, r.data_checkout " +
                     "FROM usuarios u " +
                     "JOIN reservas r ON u.id = r.id_usuario";

        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String[] cliente = new String[4];
                cliente[0] = String.valueOf(rs.getInt("id"));
                cliente[1] = rs.getString("nome");
                cliente[2] = rs.getDate("data_checkin") != null ? rs.getDate("data_checkin").toString() : "Não realizou Check-In";
                cliente[3] = rs.getDate("data_checkout") != null ? rs.getDate("data_checkout").toString() : "Ainda em Check-In";
                clientes.add(cliente);
            }
        }
        return clientes;
    }

    public static int obterIdReserva(int idCliente, int idAcomodacao) throws SQLException {
        String sql = "SELECT id FROM reservas WHERE id_usuario = ? AND id_acomodacao = ?";
        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idAcomodacao);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }

    public static void registrarCheckIn(int idReserva, int idCliente, Date dataCheckIn, Date dataCheckOut) throws SQLException {
        String sql = "INSERT INTO checkin_checkout (id_reserva, id_cliente, data_checkin, data_checkout) VALUES (?, ?, ?, ?)";
        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idReserva);
            stmt.setInt(2, idCliente);
            stmt.setDate(3, new java.sql.Date(dataCheckIn.getTime()));
            stmt.setDate(4, dataCheckOut == null ? null : new java.sql.Date(dataCheckOut.getTime()));
            stmt.executeUpdate();
        }
    }

    public static void registrarCheckOut(int idReserva, String dataCheckOut) throws SQLException {
        String sql = "UPDATE reservas SET data_checkout = ? WHERE id = ?";
        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, dataCheckOut);
            stmt.setInt(2, idReserva);
            stmt.executeUpdate();
        }
    }

    public static double obterTotalConsumo(int idReserva) throws SQLException {
        String sql = "SELECT SUM(valor_total) AS total FROM consumo WHERE id_reserva = ?";
        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idReserva);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0;
    }

    public static double obterValorDiaria(int idAcomodacao) throws SQLException {
        String sql = "SELECT valor_diaria FROM acomodacoes WHERE id = ?";
        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idAcomodacao);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("valor_diaria");
            }
        }
        return 0;
    }

    public static void carregarClientesComReservas(JComboBox<String> comboClientes, boolean apenasComCheckin) throws SQLException {
        String sql = "SELECT DISTINCT u.id, u.nome " +
                     "FROM usuarios u " +
                     "JOIN reservas r ON u.id = r.id_usuario " +
                     "WHERE r.data_checkin IS NOT NULL " +
                     (apenasComCheckin ? "AND r.data_checkout IS NOT NULL" : "");
        
        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            comboClientes.removeAllItems();

            while (rs.next()) {
                comboClientes.addItem(rs.getInt("id") + " - " + rs.getString("nome"));
            }

        } catch (SQLException e) {
            throw new SQLException("Erro ao carregar clientes com reservas: " + e.getMessage(), e);
        }
    }

    public static void registrarConsumo(int idReserva, String produto, int quantidade, double valorUnitario, double valorTotal) throws SQLException {
        String sql = "INSERT INTO consumo (id_reserva, produto, quantidade, valor_unitario, valor_total) VALUES (?, ?, ?, ?, ?)";
        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idReserva);
            stmt.setString(2, produto);
            stmt.setInt(3, quantidade);
            stmt.setDouble(4, valorUnitario);
            stmt.setDouble(5, valorTotal);
            stmt.executeUpdate();
        }
    }

    public static void carregarClientesComCheckin(JComboBox<String> comboClientes) throws SQLException {
        String sql = "SELECT u.id, u.nome FROM usuarios u " +
                     "JOIN reservas r ON u.id = r.id_usuario " +
                     "WHERE r.data_checkin IS NOT NULL AND r.data_checkout IS NOT NULL";

        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
             
            comboClientes.removeAllItems();
            
            while (rs.next()) {
                int idCliente = rs.getInt("id");
                String nomeCliente = rs.getString("nome");
                comboClientes.addItem(idCliente + " - " + nomeCliente);
            }
        }
    }

    public static void carregarAcomodacoesPorCliente(JComboBox<String> comboAcomodacoes, int idCliente) throws SQLException {
        String sql = "SELECT a.id, a.descricao FROM acomodacoes a " +
                     "JOIN reservas r ON a.id = r.id_acomodacao " +
                     "WHERE r.id_usuario = ? AND r.data_checkin IS NOT NULL AND r.data_checkout IS NULL";

        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
             
            stmt.setInt(1, idCliente);
            
            try (ResultSet rs = stmt.executeQuery()) {
                comboAcomodacoes.removeAllItems();
                
                while (rs.next()) {
                    int idAcomodacao = rs.getInt("id");
                    String descricaoAcomodacao = rs.getString("descricao");
                    comboAcomodacoes.addItem(idAcomodacao + " - " + descricaoAcomodacao);
                }
            }
        }
    }
}

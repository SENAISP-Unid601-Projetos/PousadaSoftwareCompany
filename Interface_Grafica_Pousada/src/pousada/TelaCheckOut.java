package pousada;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TelaCheckOut extends JFrame {
    private JTable tabelaCheckin;
    private DefaultTableModel modeloTabelaCheckin;
    private JTextField campoDataCheckout;

    public TelaCheckOut() {
        setTitle("Realizar Check-Out");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        configurarComponentes();
        carregarCheckIns();
    }

    private void configurarComponentes() {
        JLabel lblTitulo = new JLabel("Check-Out - Selecionar Check-In", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTitulo, BorderLayout.NORTH);

        modeloTabelaCheckin = new DefaultTableModel();
        modeloTabelaCheckin.addColumn("ID Reserva");
        modeloTabelaCheckin.addColumn("Cliente");
        modeloTabelaCheckin.addColumn("Acomodação");
        modeloTabelaCheckin.addColumn("Data Check-In");
        modeloTabelaCheckin.addColumn("Quantidade de Pessoas");
        modeloTabelaCheckin.addColumn("Valor Total");

        tabelaCheckin = new JTable(modeloTabelaCheckin);
        JScrollPane scrollPane = new JScrollPane(tabelaCheckin);
        add(scrollPane, BorderLayout.CENTER);

        JPanel painelInferior = new JPanel(new GridLayout(2, 1));
        JLabel lblDataCheckout = new JLabel("Data de Check-Out:");
        campoDataCheckout = new JTextField();
        painelInferior.add(lblDataCheckout);
        painelInferior.add(campoDataCheckout);

        JPanel painelBotoes = new JPanel();
        JButton btnConfirmar = new JButton("Confirmar Check-Out");
        btnConfirmar.addActionListener(e -> realizarCheckout());
        painelBotoes.add(btnConfirmar);

        JButton btnFecharConta = new JButton("Fechar Conta");
        btnFecharConta.addActionListener(e -> fecharConta());
        painelBotoes.add(btnFecharConta);

        painelInferior.add(painelBotoes);
        add(painelInferior, BorderLayout.SOUTH);
    }

    private void carregarCheckIns() {
        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "SELECT r.id, u.nome AS cliente, a.nome_quarto AS acomodacao, r.data_checkin, r.quantidade_pessoas, r.valor_total " +
                         "FROM reservas r " +
                         "JOIN usuarios u ON r.id_usuario = u.id " +
                         "JOIN acomodacoes a ON r.id_acomodacao = a.id " +
                         "WHERE r.data_checkin IS NOT NULL";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                modeloTabelaCheckin.addRow(new Object[] {
                    rs.getInt("id"),
                    rs.getString("cliente"),
                    rs.getString("acomodacao"),
                    rs.getDate("data_checkin"),
                    rs.getInt("quantidade_pessoas"),
                    rs.getDouble("valor_total")
                });
            }
        } catch (SQLException e) {
            showError("Erro ao carregar check-ins: " + e.getMessage());
        }
    }

    private void realizarCheckout() {
        int selectedRow = tabelaCheckin.getSelectedRow();
        String dataCheckout = campoDataCheckout.getText();

        if (selectedRow == -1 || dataCheckout.isEmpty()) {
            showError("Selecione uma reserva e preencha a data de check-out.");
            return;
        }

        int idReserva = (int) modeloTabelaCheckin.getValueAt(selectedRow, 0);

        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "UPDATE reservas SET data_checkout = ? WHERE id = ?";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, dataCheckout);
            stmt.setInt(2, idReserva);

            if (stmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Check-Out realizado com sucesso!");
                modeloTabelaCheckin.removeRow(selectedRow);
            } else {
                showError("Reserva não encontrada.");
            }
        } catch (SQLException e) {
            showError("Erro ao realizar Check-Out: " + e.getMessage());
        }
    }

    private void fecharConta() {
        int selectedRow = tabelaCheckin.getSelectedRow();

        if (selectedRow == -1) {
            showError("Selecione uma reserva para fechar a conta.");
            return;
        }

        int idReserva = (int) modeloTabelaCheckin.getValueAt(selectedRow, 0);

        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "SELECT valor_total, consumo FROM reservas WHERE id = ?";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, idReserva);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double valorTotal = rs.getDouble("valor_total");
                double consumo = rs.getDouble("consumo");
                double totalConta = valorTotal + consumo;

                JOptionPane.showMessageDialog(this, "Total da conta: R$ " + totalConta + "\n\nFechando a conta...", "Fechar Conta", JOptionPane.INFORMATION_MESSAGE);
                // Lógica para finalizar a conta pode ser implementada aqui
            } else {
                showError("Reserva não encontrada.");
            }
        } catch (SQLException e) {
            showError("Erro ao fechar a conta: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaCheckOut().setVisible(true));
    }
}


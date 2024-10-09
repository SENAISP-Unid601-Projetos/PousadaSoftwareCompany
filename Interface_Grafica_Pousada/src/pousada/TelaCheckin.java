package pousada;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class TelaCheckin extends JFrame {
    private JTable tabelaReservas;
    private DefaultTableModel modeloTabelaReservas;
    private JButton btnConfirmarCheckin;

    public TelaCheckin() {
        setTitle("Realizar Check-In");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela
        setLayout(new BorderLayout());
        configurarComponentes();
        carregarReservas();
    }

    private void configurarComponentes() {
        // Painel principal da tela
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Modelo da tabela de reservas
        modeloTabelaReservas = new DefaultTableModel();
        modeloTabelaReservas.addColumn("ID");
        modeloTabelaReservas.addColumn("Cliente");
        modeloTabelaReservas.addColumn("Acomodação");
        modeloTabelaReservas.addColumn("Data Reserva");
        modeloTabelaReservas.addColumn("Quantidade de Pessoas");
        modeloTabelaReservas.addColumn("Valor Total");

        // Tabela para exibir reservas
        tabelaReservas = new JTable(modeloTabelaReservas);
        JScrollPane scrollPane = new JScrollPane(tabelaReservas);
        painelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Botão para confirmar check-in
        btnConfirmarCheckin = new JButton("Confirmar Check-In");
        btnConfirmarCheckin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarCheckin();
            }
        });

        // Adicionando componentes ao painel
        painelPrincipal.add(btnConfirmarCheckin, BorderLayout.SOUTH);
        add(painelPrincipal, BorderLayout.CENTER);
    }

    private void carregarReservas() {
        // Conexão ao banco de dados para buscar as reservas
        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "SELECT r.id, u.nome AS cliente, a.nome_quarto AS acomodacao, r.data_checkin, r.data_checkout, r.quantidade_pessoas, r.valor_total " +
                         "FROM reservas r " +
                         "JOIN usuarios u ON r.id_usuario = u.id " +
                         "JOIN acomodacoes a ON r.id_acomodacao = a.id " +
                         "WHERE r.data_checkin IS NULL"; // Mostrar apenas reservas sem check-in realizado
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Limpar a tabela antes de carregar novos dados
            modeloTabelaReservas.setRowCount(0); 

            // Carregar os dados na tabela
            while (rs.next()) {
                modeloTabelaReservas.addRow(new Object[] {
                    rs.getInt("id"),
                    rs.getString("cliente"),
                    rs.getString("acomodacao"),
                    rs.getDate("data_checkin"),
                    rs.getInt("quantidade_pessoas"),
                    rs.getDouble("valor_total")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar reservas: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void realizarCheckin() {
        int linhaSelecionada = tabelaReservas.getSelectedRow();
        if (linhaSelecionada != -1) {
            int idReserva = (int) modeloTabelaReservas.getValueAt(linhaSelecionada, 0); // Obter o ID da reserva selecionada

            // Confirmar check-in
            int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja confirmar o Check-In para esta reserva?", "Confirmar Check-In", JOptionPane.YES_NO_OPTION);
            if (confirmacao == JOptionPane.YES_OPTION) {
                try (Connection conexao = ConexaoBanco.getConnection()) {
                    // Inserir registro na tabela checkin_checkout
                    String sqlInsert = "INSERT INTO checkin_checkout (id_reserva, id_cliente, data_checkin) VALUES (?, ?, NOW())";
                    PreparedStatement stmtInsert = conexao.prepareStatement(sqlInsert);
                    stmtInsert.setInt(1, idReserva);
                    stmtInsert.setInt(2, (int) modeloTabelaReservas.getValueAt(linhaSelecionada, 0)); // ID do cliente (assumindo que é o mesmo da reserva)

                    stmtInsert.executeUpdate();

                    // Atualizar a reserva para incluir a data de check-in
                    String sqlUpdate = "UPDATE reservas SET data_checkin = NOW() WHERE id = ?";
                    PreparedStatement stmtUpdate = conexao.prepareStatement(sqlUpdate);
                    stmtUpdate.setInt(1, idReserva);

                    stmtUpdate.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Check-In realizado com sucesso!");
                    modeloTabelaReservas.removeRow(linhaSelecionada); // Remove a linha da tabela após o check-in
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erro ao realizar Check-In: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para fazer o Check-In.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaCheckin().setVisible(true));
    }
}



package pousada;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TelaFecharConta extends JFrame {
    private JComboBox<String> comboClientes;
    private JTextArea txtAreaResumo;
    private JButton btnFecharConta;

    public TelaFecharConta() {
        setTitle("Fechar Conta");
        setSize(400, 300); // Tamanho padrão da janela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.DARK_GRAY);  
        setLayout(new BorderLayout());

        configurarComponentes();
        carregarClientesComReservas();
    }

    private void configurarComponentes() {
        JPanel painelPrincipal = new JPanel(new GridLayout(4, 1, 10, 10)); // Alterado para 4 linhas
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Margem interna

        JLabel lblCliente = new JLabel("Selecionar Cliente:");
        comboClientes = new JComboBox<>();

        txtAreaResumo = new JTextArea();
        txtAreaResumo.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtAreaResumo);

        btnFecharConta = new JButton("Fechar Conta");
        btnFecharConta.addActionListener(e -> fecharConta());

        // Adicionando os componentes ao painel
        painelPrincipal.add(lblCliente);
        painelPrincipal.add(comboClientes);
        painelPrincipal.add(scrollPane);
        painelPrincipal.add(btnFecharConta);

        add(painelPrincipal, BorderLayout.CENTER);
    }

    private void carregarClientesComReservas() {
        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "SELECT DISTINCT u.id, u.nome FROM usuarios u JOIN reservas r ON u.id = r.id_usuario";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                comboClientes.addItem(id + " - " + nome);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar os clientes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fecharConta() {
        String clienteSelecionado = (String) comboClientes.getSelectedItem();
        if (clienteSelecionado != null) {
            int idCliente = Integer.parseInt(clienteSelecionado.split(" - ")[0]);

            double valorTotalGeral = calcularValorTotal(idCliente);
            txtAreaResumo.setText("Total a pagar: R$ " + valorTotalGeral);

            // Você pode registrar o fechamento da conta aqui, se necessário.
            JOptionPane.showMessageDialog(this, "Conta fechada com sucesso! Total: R$ " + valorTotalGeral);
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double calcularValorTotal(int idCliente) {
        double valorTotal = 0.0;
        try (Connection conexao = ConexaoBanco.getConnection()) {
            // Obter valor da acomodação
            String sqlAcomodacao = "SELECT valor_total FROM reservas WHERE id_usuario = ? AND data_checkout IS NULL";
            PreparedStatement stmtAcomodacao = conexao.prepareStatement(sqlAcomodacao);
            stmtAcomodacao.setInt(1, idCliente);
            ResultSet rsAcomodacao = stmtAcomodacao.executeQuery();

            if (rsAcomodacao.next()) {
                valorTotal += rsAcomodacao.getDouble("valor_total");
            }

            // Obter valor dos consumos
            String sqlConsumo = "SELECT SUM(valor_total) AS total_consumo FROM consumo WHERE id_reserva IN (SELECT id FROM reservas WHERE id_usuario = ? AND data_checkout IS NULL)";
            PreparedStatement stmtConsumo = conexao.prepareStatement(sqlConsumo);
            stmtConsumo.setInt(1, idCliente);
            ResultSet rsConsumo = stmtConsumo.executeQuery();

            if (rsConsumo.next()) {
                valorTotal += rsConsumo.getDouble("total_consumo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return valorTotal;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaFecharConta().setVisible(true));
    }
}

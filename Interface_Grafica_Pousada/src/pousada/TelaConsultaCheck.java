package pousada;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TelaConsultaCheck extends JFrame {
    private JTable tabelaClientes;
    private JButton btnConsultar;

    public TelaConsultaCheck() {
        setTitle("Consulta de Check-In/Check-Out");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        btnConsultar = new JButton("Consultar");
        btnConsultar.addActionListener(e -> consultarClientes());

        JPanel painelBotoes = new JPanel();
        painelBotoes.add(btnConsultar);
        add(painelBotoes, BorderLayout.NORTH);

        tabelaClientes = new JTable();
        add(new JScrollPane(tabelaClientes), BorderLayout.CENTER);
    }

    private void consultarClientes() {
        try {
            List<String[]> clientes = BancoDadosUtil.obterClientesComStatus();
            String[] colunas = {"ID", "Nome", "Data Check-In", "Data Check-Out"};
            String[][] dados = clientes.toArray(new String[0][]);
            tabelaClientes.setModel(new javax.swing.table.DefaultTableModel(dados, colunas));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao consultar clientes: " + ex.getMessage());
        }
    }
}


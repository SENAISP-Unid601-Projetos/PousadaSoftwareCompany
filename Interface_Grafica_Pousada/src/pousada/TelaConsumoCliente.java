package pousada;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class TelaConsumoCliente extends JFrame {
    private JComboBox<String> comboClientes;
    private JComboBox<String> comboAcomodacoes;
    private JTextField txtProduto;
    private JTextField txtQuantidade;
    private JTextField txtValorUnitario;
    private JButton btnRegistrarConsumo;

    public TelaConsumoCliente() {
        setTitle("Registrar Consumo");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        configurarComponentes();
        carregarClientesComCheckin();
    }

    private void configurarComponentes() {
        JPanel painelFormulario = new JPanel(new GridLayout(6, 2, 10, 10));
        painelFormulario.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        comboClientes = new JComboBox<>();
        comboAcomodacoes = new JComboBox<>();
        txtProduto = new JTextField();
        txtQuantidade = new JTextField();
        txtValorUnitario = new JTextField();
        btnRegistrarConsumo = new JButton("Registrar Consumo");
        btnRegistrarConsumo.addActionListener(e -> registrarConsumo());

        painelFormulario.add(new JLabel("Cliente:"));
        painelFormulario.add(comboClientes);
        painelFormulario.add(new JLabel("Acomodação:"));
        painelFormulario.add(comboAcomodacoes);
        painelFormulario.add(new JLabel("Produto:"));
        painelFormulario.add(txtProduto);
        painelFormulario.add(new JLabel("Quantidade:"));
        painelFormulario.add(txtQuantidade);
        painelFormulario.add(new JLabel("Valor Unitário:"));
        painelFormulario.add(txtValorUnitario);
        painelFormulario.add(new JLabel());
        painelFormulario.add(btnRegistrarConsumo);

        add(painelFormulario, BorderLayout.CENTER);
        
        // Adiciona um listener para atualizar acomodações ao selecionar um cliente
        comboClientes.addActionListener(e -> carregarAcomodacoes());
    }

    private void carregarClientesComCheckin() {
        try {
            // Modifique este método para carregar apenas clientes que têm check-ins realizados
            BancoDadosUtil.carregarClientesComCheckin(comboClientes);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + ex.getMessage());
        }
    }

    private void carregarAcomodacoes() {
        comboAcomodacoes.removeAllItems(); // Limpa as acomodações anteriores
        int idCliente = Integer.parseInt(comboClientes.getSelectedItem().toString().split(" - ")[0]);
        
        try {
            BancoDadosUtil.carregarAcomodacoesPorCliente(comboAcomodacoes, idCliente);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar acomodações: " + ex.getMessage());
        }
    }

    private void registrarConsumo() {
        if (comboClientes.getSelectedItem() == null || comboAcomodacoes.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente e uma acomodação.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idCliente = Integer.parseInt(comboClientes.getSelectedItem().toString().split(" - ")[0]);
        int idAcomodacao = Integer.parseInt(comboAcomodacoes.getSelectedItem().toString().split(" - ")[0]);
        String produto = txtProduto.getText();
        
        // Verifica se a quantidade e o valor unitário foram preenchidos
        if (txtQuantidade.getText().isEmpty() || txtValorUnitario.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha a quantidade e o valor unitário.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int quantidade = Integer.parseInt(txtQuantidade.getText());
        double valorUnitario = Double.parseDouble(txtValorUnitario.getText());
        double valorTotal = quantidade * valorUnitario;

        try {
            int idReserva = BancoDadosUtil.obterIdReserva(idCliente, idAcomodacao);
            BancoDadosUtil.registrarConsumo(idReserva, produto, quantidade, valorUnitario, valorTotal);
            JOptionPane.showMessageDialog(this, "Consumo registrado com sucesso!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao registrar consumo: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaConsumoCliente().setVisible(true));
    }
}

package pousada;

import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;

public class TelaInicial extends JFrame {
    public TelaInicial() {
        setTitle("Tela Inicial - Gerenciamento de Pousada");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        configurarComponentes();
    }

    private void configurarComponentes() {
        // Título
        JLabel lblTitulo = new JLabel("Bem-vindo ao Sistema de Gerenciamento da Pousada", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 30));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBackground(Color.GRAY);
        lblTitulo.setOpaque(true);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Painel dos botões principais
        JPanel painelBotoes = new JPanel(new GridLayout(11, 1, 10, 10)); // Aumentado para 11 linhas
        painelBotoes.setBackground(Color.DARK_GRAY);
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Botões da tela inicial
        JButton btnCadastrarClientes = criarBotao("Cadastrar Clientes", e -> abrirTelaCadastroClientes());
        JButton btnCadastrarAcomodacoes = criarBotao("Cadastrar Acomodações", e -> abrirTelaCadastroAcomodacoes());
        JButton btnConsultaAcomodacoes = criarBotao("Consultar Acomodações Cadastradas", e -> abrirTelaConsultaAcomodacoes());
        JButton btnConsultaClientes = criarBotao("Consultar Clientes Cadastrados", e -> abrirTelaConsultaClientes());
        JButton btnConsultaCheck = criarBotao("Consultar Check-In/Check-Out", e -> abrirTelaConsultaCheck());
        JButton btnCheckIn = criarBotao("Realizar Check-In", e -> abrirTelaCheckin());
        JButton btnCheckOut = criarBotao("Realizar Check-Out", e -> abrirTelaCheckOut());

        JButton btnAdicionarReservas = criarBotao("Adicionar Reservas", e -> abrirTelaAdicionarReservas());
        JButton btnConsumoCliente = criarBotao("Consumo Cliente", e -> abrirTelaConsumoCliente());
        JButton btnFecharConta = criarBotao("Fechar Conta", e -> abrirTelaFecharConta());

        // Adicionando os botões ao painel
        painelBotoes.add(btnCadastrarClientes);
        painelBotoes.add(btnCadastrarAcomodacoes);
        painelBotoes.add(btnConsultaAcomodacoes);
        painelBotoes.add(btnConsultaClientes);
        painelBotoes.add(btnConsultaCheck); // Adicionando botão de consulta de check
        painelBotoes.add(btnCheckIn);
        painelBotoes.add(btnCheckOut);

        painelBotoes.add(btnAdicionarReservas);
        painelBotoes.add(btnConsumoCliente);
        painelBotoes.add(btnFecharConta);

        add(painelBotoes, BorderLayout.CENTER);

        // Rodapé com informações
        JLabel lblRodape = new JLabel("Sistema desenvolvido para Pousadas no Brasil", JLabel.CENTER);
        lblRodape.setFont(new Font("Arial", Font.ITALIC, 14));
        lblRodape.setForeground(Color.LIGHT_GRAY);
        lblRodape.setBackground(Color.DARK_GRAY);
        lblRodape.setOpaque(true);
        lblRodape.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblRodape, BorderLayout.SOUTH);
    }

    private JButton criarBotao(String texto, ActionListener acao) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Tahoma", Font.PLAIN, 28));
        botao.setForeground(Color.WHITE);
        botao.setBackground(new Color(70, 130, 180)); // Cor azul para consistência
        botao.setFocusPainted(false);
        botao.addActionListener(acao);
        return botao;
    }

    // Métodos para abrir as telas correspondentes
    private void abrirTelaCadastroClientes() {
        new TelaCadastroClientes().setVisible(true);
    }

    private void abrirTelaCadastroAcomodacoes() {
        new TelaCadastroAcomodacoes().setVisible(true);
    }

    private void abrirTelaConsultaAcomodacoes() {
        new TelaConsultaAcomodacoes().setVisible(true);
    }

    private void abrirTelaConsultaClientes() {
        new TelaClientesCadastrados().setVisible(true);
    }

    private void abrirTelaConsultaCheck() {
        new TelaConsultaCheck().setVisible(true); 
    }

    private void abrirTelaCheckin() {
        new TelaCheckin().setVisible(true);
    }

    private void abrirTelaCheckOut() {
        new TelaCheckOut().setVisible(true);
    }

    private void abrirTelaAdicionarReservas() {
        new TelaAdicionarReservas().setVisible(true);  
    }

    private void abrirTelaConsumoCliente() {
        new TelaConsumoCliente().setVisible(true);  
    }

    private void abrirTelaFecharConta() {
        new TelaFecharConta().setVisible(true);  
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaInicial().setVisible(true));
    }
}

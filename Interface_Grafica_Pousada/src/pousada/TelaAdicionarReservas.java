package pousada;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.text.SimpleDateFormat;

public class TelaAdicionarReservas extends JFrame {
    private JComboBox<String> comboClientes;
    private JComboBox<String> comboAcomodacoes;
    private JSpinner spinnerCheckIn;
    private JSpinner spinnerCheckOut;
    private JSpinner spinnerQuantidadePessoas;
    private JComboBox<String> comboReservas;

    public TelaAdicionarReservas() {
        setTitle("Adicionar Reservas");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());

        configurarComponentes();
        carregarClientes();
        carregarAcomodacoes();
        carregarReservas();
    }

    private void configurarComponentes() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Campos da reserva
        JLabel lblCliente = new JLabel("Cliente:");
        gbc.gridx = 0; gbc.gridy = 0;
        add(lblCliente, gbc);

        comboClientes = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 0;
        add(comboClientes, gbc);

        JLabel lblAcomodacao = new JLabel("Acomodação:");
        gbc.gridx = 0; gbc.gridy = 1;
        add(lblAcomodacao, gbc);

        comboAcomodacoes = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 1;
        add(comboAcomodacoes, gbc);

        JLabel lblCheckIn = new JLabel("Data de Check-In:");
        gbc.gridx = 0; gbc.gridy = 2;
        add(lblCheckIn, gbc);

        spinnerCheckIn = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorCheckIn = new JSpinner.DateEditor(spinnerCheckIn, "yyyy-MM-dd");
        spinnerCheckIn.setEditor(editorCheckIn);
        gbc.gridx = 1; gbc.gridy = 2;
        add(spinnerCheckIn, gbc);

        JLabel lblCheckOut = new JLabel("Data de Check-Out:");
        gbc.gridx = 0; gbc.gridy = 3;
        add(lblCheckOut, gbc);

        spinnerCheckOut = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorCheckOut = new JSpinner.DateEditor(spinnerCheckOut, "yyyy-MM-dd");
        spinnerCheckOut.setEditor(editorCheckOut);
        gbc.gridx = 1; gbc.gridy = 3;
        add(spinnerCheckOut, gbc);

        JLabel lblQuantidadePessoas = new JLabel("Quantidade de Pessoas:");
        gbc.gridx = 0; gbc.gridy = 4;
        add(lblQuantidadePessoas, gbc);

        spinnerQuantidadePessoas = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        gbc.gridx = 1; gbc.gridy = 4;
        add(spinnerQuantidadePessoas, gbc);

        JLabel lblReservas = new JLabel("Reservas Existentes:");
        gbc.gridx = 0; gbc.gridy = 5;
        add(lblReservas, gbc);

        comboReservas = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 5;
        add(comboReservas, gbc);

        // Painel para os botões
        JPanel painelBotoes = new JPanel(new FlowLayout());

        JButton btnRegistrarReserva = new JButton("Registrar Reserva");
        btnRegistrarReserva.addActionListener(e -> registrarReserva());
        painelBotoes.add(btnRegistrarReserva);

        JButton btnCancelarReserva = new JButton("Cancelar Reserva");
        btnCancelarReserva.addActionListener(e -> cancelarReserva());
        painelBotoes.add(btnCancelarReserva);

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(e -> voltarTelaInicial());
        painelBotoes.add(btnVoltar);

        // Adicionar o painel de botões à tela
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        add(painelBotoes, gbc);
    }

    private void carregarClientes() {
        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "SELECT id, nome FROM usuarios";
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

    private void carregarAcomodacoes() {
        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "SELECT id, nome_quarto FROM acomodacoes WHERE status_disponibilidade = 'Disponível'";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nomeQuarto = rs.getString("nome_quarto");
                comboAcomodacoes.addItem(id + " - " + nomeQuarto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar as acomodações: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarReservas() {
        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "SELECT id, id_usuario, id_acomodacao FROM reservas";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idReserva = rs.getInt("id");
                int idUsuario = rs.getInt("id_usuario");
                String nomeCliente = obterNomeCliente(idUsuario);
                comboReservas.addItem(idReserva + " - " + nomeCliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar as reservas: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String obterNomeCliente(int idUsuario) {
        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "SELECT nome FROM usuarios WHERE id = ?";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("nome");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Cliente Desconhecido";
    }

    private void registrarReserva() {
        String clienteSelecionado = (String) comboClientes.getSelectedItem();
        String acomodacaoSelecionada = (String) comboAcomodacoes.getSelectedItem();
        Date dataCheckIn = (Date) spinnerCheckIn.getValue();
        Date dataCheckOut = (Date) spinnerCheckOut.getValue();
        int quantidadePessoas = (int) spinnerQuantidadePessoas.getValue();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dataCheckInFormatada = sdf.format(dataCheckIn);
        String dataCheckOutFormatada = sdf.format(dataCheckOut);

        if (clienteSelecionado != null && acomodacaoSelecionada != null && dataCheckIn != null && dataCheckOut != null) {
            int idCliente = Integer.parseInt(clienteSelecionado.split(" - ")[0]);
            int idAcomodacao = Integer.parseInt(acomodacaoSelecionada.split(" - ")[0]);

            // Verificar se a acomodação já está reservada para as mesmas datas
            if (verificarDisponibilidade(idAcomodacao, dataCheckIn, dataCheckOut)) {
                try (Connection conexao = ConexaoBanco.getConnection()) {
                    String sql = "INSERT INTO reservas (id_usuario, id_acomodacao, data_checkin, data_checkout, quantidade_pessoas, valor_total) " +
                                 "VALUES (?, ?, ?, ?, ?, ?)";

                    double valorTotal = calcularValorTotal(idAcomodacao, quantidadePessoas, dataCheckIn, dataCheckOut);

                    try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                        stmt.setInt(1, idCliente);
                        stmt.setInt(2, idAcomodacao);
                        stmt.setString(3, dataCheckInFormatada);
                        stmt.setString(4, dataCheckOutFormatada);
                        stmt.setInt(5, quantidadePessoas);
                        stmt.setDouble(6, valorTotal);

                        stmt.executeUpdate();

                        // Atualizar status da acomodação para Indisponível
                        atualizarStatusAcomodacao(idAcomodacao, "Indisponível");

                        JOptionPane.showMessageDialog(this, "Reserva registrada com sucesso!");
                        dispose(); // Fecha a tela após a confirmação
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erro ao registrar a reserva: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Acomodação já está reservada para as datas selecionadas.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos corretamente.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean verificarDisponibilidade(int idAcomodacao, Date dataCheckIn, Date dataCheckOut) {
        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "SELECT COUNT(*) FROM reservas WHERE id_acomodacao = ? " +
                         "AND ((data_checkin <= ? AND data_checkout >= ?) OR " +
                         "(data_checkin <= ? AND data_checkout >= ?))";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            stmt.setInt(1, idAcomodacao);
            stmt.setString(2, sdf.format(dataCheckIn));
            stmt.setString(3, sdf.format(dataCheckIn));
            stmt.setString(4, sdf.format(dataCheckOut));
            stmt.setString(5, sdf.format(dataCheckOut));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // Retorna true se não houver reservas conflitantes
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Considera não disponível em caso de erro
    }

    private void atualizarStatusAcomodacao(int idAcomodacao, String novoStatus) {
        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "UPDATE acomodacoes SET status_disponibilidade = ? WHERE id = ?";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, novoStatus);
            stmt.setInt(2, idAcomodacao);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao atualizar o status da acomodação: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double calcularValorTotal(int idAcomodacao, int quantidadePessoas, Date dataCheckIn, Date dataCheckOut) {
        long dias = (dataCheckOut.getTime() - dataCheckIn.getTime()) / (1000 * 60 * 60 * 24);
        if (dias == 0) dias = 1; // Se for o mesmo dia, conta 1 diária

        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "SELECT valor_diaria FROM acomodacoes WHERE id = ?";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, idAcomodacao);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double valorDiaria = rs.getDouble("valor_diaria");
                return valorDiaria * quantidadePessoas * dias;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void cancelarReserva() {
        int reservaSelecionada = comboReservas.getSelectedIndex();
        if (reservaSelecionada != -1) {
            String reservaInfo = (String) comboReservas.getSelectedItem();
            int idReserva = Integer.parseInt(reservaInfo.split(" - ")[0]); // Obter o ID da reserva

            int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja cancelar esta reserva?", "Confirmar Cancelamento", JOptionPane.YES_NO_OPTION);
            if (confirmacao == JOptionPane.YES_OPTION) {
                try (Connection conexao = ConexaoBanco.getConnection()) {
                    // Obter a acomodação associada à reserva
                    String sqlAcomodacao = "SELECT id_acomodacao FROM reservas WHERE id = ?";
                    PreparedStatement stmtAcomodacao = conexao.prepareStatement(sqlAcomodacao);
                    stmtAcomodacao.setInt(1, idReserva);
                    ResultSet rsAcomodacao = stmtAcomodacao.executeQuery();

                    int idAcomodacao = 0;
                    if (rsAcomodacao.next()) {
                        idAcomodacao = rsAcomodacao.getInt("id_acomodacao");
                    }

                    // Cancelar a reserva
                    String sql = "DELETE FROM reservas WHERE id = ?";
                    PreparedStatement stmt = conexao.prepareStatement(sql);
                    stmt.setInt(1, idReserva);
                    stmt.executeUpdate();

                    // Atualizar o status da acomodação para Disponível
                    atualizarStatusAcomodacao(idAcomodacao, "Disponível");

                    // Remover a reserva do comboBox
                    comboReservas.removeItemAt(reservaSelecionada);
                    JOptionPane.showMessageDialog(this, "Reserva cancelada com sucesso!");
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erro ao cancelar a reserva: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para cancelar.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void voltarTelaInicial() {
        new TelaInicial().setVisible(true);
        dispose();  // Fecha a tela atual
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaAdicionarReservas().setVisible(true));
    }
}


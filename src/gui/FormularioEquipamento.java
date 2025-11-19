package gui;

import servicos.EquipamentoServico;
import javax.swing.*;
import java.awt.*;

/**
 * Formulário para cadastrar novos Equipamentos, usando GridBagLayout para um
 * alinhamento limpo (NetBeans-like).
 */
public class FormularioEquipamento extends JDialog {

    private EquipamentoServico equipamentoServico;
    private JTextField txtId, txtNome, txtTipo;

    public FormularioEquipamento(JFrame parent, EquipamentoServico equipamentoServico) {
        super(parent, "Cadastrar Equipamento", true);
        this.equipamentoServico = equipamentoServico;

        setSize(350, 220);
        setLocationRelativeTo(parent);

        // GridBagLayout para controle preciso de alinhamento
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Margem entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Campo ID ---
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("ID:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0; // Faz o campo de texto esticar
        txtId = new JTextField(15);
        add(txtId, gbc);

        // --- Campo Nome ---
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        txtNome = new JTextField(15);
        add(txtNome, gbc);

        // --- Campo Tipo ---
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        add(new JLabel("Tipo:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        txtTipo = new JTextField(15);
        add(txtTipo, gbc);

        // --- Botão Salvar ---
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2; // Ocupa duas colunas
        gbc.fill = GridBagConstraints.NONE; // Não estica
        gbc.anchor = GridBagConstraints.CENTER; // Centraliza
        gbc.insets = new Insets(15, 10, 5, 10); // Margem extra no topo
        JButton btnSalvar = new JButton("Salvar Equipamento");
        add(btnSalvar, gbc);

        btnSalvar.addActionListener(e -> cadastrarEquipamento());
    }

    private void cadastrarEquipamento() {
        try {
            Integer id = Integer.parseInt(txtId.getText());
            String nome = txtNome.getText();
            String tipo = txtTipo.getText();

            if (nome.trim().isEmpty() || tipo.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e Tipo não podem ser vazios.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Chamada ao Serviço (Tratamento de Exceção ocorre aqui se ID for duplicado)
            equipamentoServico.cadastrar(id, nome, tipo);

            JOptionPane.showMessageDialog(this, "Equipamento cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (NumberFormatException ex) {
            // Tratamento de exceção
            JOptionPane.showMessageDialog(this, "O ID deve ser um número inteiro válido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            // Tratamento de exceção: ID duplicado capturado
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
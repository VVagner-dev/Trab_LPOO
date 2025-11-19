package gui;

import servicos.EquipamentoServico;
import javax.swing.*;
import java.awt.*;

public class FormularioEquipamento extends JDialog {

    private EquipamentoServico equipamentoServico;
    private JTextField txtId, txtNome, txtTipo;

    public FormularioEquipamento(JFrame parent, EquipamentoServico equipamentoServico) {
        super(parent, "Cadastrar Equipamento", true);
        this.equipamentoServico = equipamentoServico;

        setSize(300, 250);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(4, 2, 10, 10));

        // Componentes do formulário
        add(new JLabel("ID:"));
        txtId = new JTextField();
        add(txtId);

        add(new JLabel("Nome:"));
        txtNome = new JTextField();
        add(txtNome);

        add(new JLabel("Tipo:"));
        txtTipo = new JTextField();
        add(txtTipo);

        JButton btnSalvar = new JButton("Salvar");
        add(btnSalvar);

        btnSalvar.addActionListener(e -> cadastrarEquipamento());
    }

    private void cadastrarEquipamento() {
        try {
            // 1. Coleta e validação básica
            Integer id = Integer.parseInt(txtId.getText());
            String nome = txtNome.getText();
            String tipo = txtTipo.getText();

            if (nome.trim().isEmpty() || tipo.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e Tipo não podem ser vazios.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Chama o Serviço
            equipamentoServico.cadastrar(id, nome, tipo);

            // 3. Feedback e Fechamento
            JOptionPane.showMessageDialog(this, "Equipamento cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (NumberFormatException ex) {
            // Tratamento de exceção
            JOptionPane.showMessageDialog(this, "O ID deve ser um número inteiro válido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            // Tratamento de exceção: Captura a exceção de ID duplicado do Serviço
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
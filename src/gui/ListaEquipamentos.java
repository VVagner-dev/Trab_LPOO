package gui;

import entidades.Equipamento;
import servicos.EquipamentoServico;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tela para exibir e gerenciar a lista de Equipamentos, com funcionalidade de remoção.
 */
public class ListaEquipamentos extends JDialog {

    private EquipamentoServico equipamentoServico;
    private JTable table;
    private DefaultTableModel model;

    public ListaEquipamentos(JFrame parent, EquipamentoServico equipamentoServico) {
        super(parent, "Lista de Equipamentos", true);
        this.equipamentoServico = equipamentoServico;

        setSize(500, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10)); // Adiciona margem na borda

        // Título estilizado no topo
        JLabel lblTitulo = new JLabel("Equipamentos Cadastrados", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblTitulo, BorderLayout.NORTH);

        // Configuração da tabela
        String[] colunas = {"ID", "Nome", "Tipo"};
        model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Painel de botões centralizado
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnRemover = new JButton("Remover Selecionado");
        btnRemover.setBackground(new Color(220, 50, 50)); // Cor vermelha para indicar exclusão
        btnRemover.setForeground(Color.WHITE);
        painelBotoes.add(btnRemover);
        add(painelBotoes, BorderLayout.SOUTH);

        // Carregar e exibir os dados
        carregarDados();

        // Ação do botão remover
        btnRemover.addActionListener(e -> removerEquipamento());
    }

    /**
     * Carrega os dados de Equipamentos do serviço e exibe na tabela.
     */
    private void carregarDados() {
        model.setRowCount(0); // Limpa as linhas existentes
        List<Equipamento> equipamentos = equipamentoServico.listar();

        for (Equipamento e : equipamentos) {
            model.addRow(new Object[]{
                    e.getId(),
                    e.getNome(),
                    e.getTipo()
            });
        }
    }

    /**
     * Remove o Equipamento selecionado da tabela, chamando o serviço.
     */
    private void removerEquipamento() {
        int linhaSelecionada = table.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um equipamento para remover.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer id = (Integer) model.getValueAt(linhaSelecionada, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja remover o equipamento ID: " + id + "?",
                "Confirmação de Remoção", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Tratamento de exceção no serviço
                equipamentoServico.removerPorId(id);
                JOptionPane.showMessageDialog(this, "Equipamento removido com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarDados(); // Recarrega a lista
            } catch (RuntimeException ex) {
                // Tratamento de exceção: Captura erros como "Equipamento não encontrado"
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Remoção", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
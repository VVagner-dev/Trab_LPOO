package gui;

import entidades.Manutencao;
import servicos.ManutencaoServico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ListaManutencoes extends JDialog {

    private ManutencaoServico manutencaoServico;
    private JTable table;
    private DefaultTableModel model;
    private boolean listarTodas;

    // O parâmetro listarTodas define se lista todas (true) ou apenas pendentes (false)
    public ListaManutencoes(JFrame parent, ManutencaoServico manutencaoServico, boolean listarTodas) {
        super(parent, listarTodas ? "Lista de Todas as Manutenções" : "Lista de Manutenções Pendentes", true);
        this.manutencaoServico = manutencaoServico;
        this.listarTodas = listarTodas;

        setSize(800, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Configuração da tabela
        String[] colunas = {"ID", "Equipamento", "Descrição", "Data", "Valor Base", "Tipo", "Concluída", "Custo Total"};
        model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel painelBotoes = new JPanel();
        JButton btnConcluir = new JButton("Marcar como Concluída");
        painelBotoes.add(btnConcluir);
        add(painelBotoes, BorderLayout.SOUTH);

        // Carregar e exibir os dados
        carregarDados();

        // Ação do botão concluir
        btnConcluir.addActionListener(e -> concluirManutencao());

        // O botão só faz sentido se a manutenção não estiver concluída
        btnConcluir.setEnabled(!listarTodas); // Desabilita se estiver listando todas
    }

    private void carregarDados() {
        model.setRowCount(0); // Limpa as linhas existentes
        List<Manutencao> manutencoes = listarTodas
                ? manutencaoServico.listar()
                : manutencaoServico.listarPendentes();

        for (Manutencao m : manutencoes) {
            String tipo = m.getClass().getSimpleName().replace("Manutencao", ""); // Ex: Simples, Urgente, Recorrente

            model.addRow(new Object[]{
                    m.getId(),
                    m.getEquipamento().getNome(),
                    m.getDescricao(),
                    m.getData(),
                    String.format("R$ %.2f", m.getValorBase()),
                    tipo,
                    m.isConcluida() ? "Sim" : "Não",
                    // Polimorfismo em ação: o método calcularCusto correto é chamado
                    String.format("R$ %.2f", m.calcularCusto())
            });
        }
    }

    private void concluirManutencao() {
        int linhaSelecionada = table.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma manutenção para concluir.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer id = (Integer) model.getValueAt(linhaSelecionada, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja marcar a Manutenção ID: " + id + " como CONCLUÍDA?",
                "Confirmação", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                manutencaoServico.concluirManutencao(id);
                JOptionPane.showMessageDialog(this, "Manutenção concluída com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarDados(); // Recarrega a lista para remover a concluída (se for lista Pendentes)
            } catch (RuntimeException ex) {
                // Captura a exceção de manutenção não encontrada
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Conclusão", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
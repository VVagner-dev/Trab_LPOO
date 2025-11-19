package gui;

import entidades.Manutencao;
import servicos.ManutencaoServico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tela para exibir a lista de Manutenções e permitir a conclusão de pendentes.
 * Demonstra o Polimorfismo ao chamar calcularCusto().
 */
public class ListaManutencoes extends JDialog {

    private ManutencaoServico manutencaoServico;
    private JTable table;
    private DefaultTableModel model;
    private boolean listarTodas;

    /**
     * @param parent O frame pai.
     * @param manutencaoServico O serviço de manutenção para buscar os dados.
     * @param listarTodas Se for true, lista todas; se for false, lista apenas pendentes.
     */
    public ListaManutencoes(JFrame parent, ManutencaoServico manutencaoServico, boolean listarTodas) {
        super(parent, listarTodas ? "Lista de Todas as Manutenções" : "Lista de Manutenções Pendentes", true);
        this.manutencaoServico = manutencaoServico;
        this.listarTodas = listarTodas;

        setSize(800, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Título no topo
        JLabel lblTitulo = new JLabel(listarTodas ? "Todas as Manutenções Registradas" : "Manutenções Pendentes", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblTitulo, BorderLayout.NORTH);


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
        btnConcluir.setBackground(new Color(50, 150, 220)); // Cor azul
        btnConcluir.setForeground(Color.WHITE);
        painelBotoes.add(btnConcluir);
        add(painelBotoes, BorderLayout.SOUTH);

        carregarDados();

        // Ação do botão concluir
        btnConcluir.addActionListener(e -> concluirManutencao());

        // O botão só aparece e é habilitado na lista de Pendentes
        btnConcluir.setVisible(!listarTodas);
    }

    /**
     * Carrega os dados de Manutenções do serviço e exibe na tabela.
     * Usa o método calcularCusto() polimórfico.
     */
    private void carregarDados() {
        model.setRowCount(0); // Limpa as linhas existentes
        List<Manutencao> manutencoes = listarTodas
                ? manutencaoServico.listar()
                : manutencaoServico.listarPendentes();

        for (Manutencao m : manutencoes) {
            // Usa o nome simples da classe (ex: Simples, Urgente, Recorrente) para exibir o tipo
            String tipo = m.getClass().getSimpleName().replace("Manutencao", "");

            model.addRow(new Object[]{
                    m.getId(),
                    m.getEquipamento().getNome(),
                    m.getDescricao(),
                    m.getData(),
                    String.format("R$ %.2f", m.getValorBase()),
                    tipo,
                    m.isConcluida() ? "Sim" : "Não",
                    // Polimorfismo: O método calcularCusto correto é chamado automaticamente
                    String.format("R$ %.2f", m.calcularCusto())
            });
        }
    }

    /**
     * Marca a manutenção selecionada como concluída, se aplicável (apenas na lista de Pendentes).
     */
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
                // Chama o serviço (Tratamento de Exceção se o item for nulo)
                manutencaoServico.concluirManutencao(id);
                JOptionPane.showMessageDialog(this, "Manutenção concluída com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarDados(); // Recarrega a lista
            } catch (RuntimeException ex) {
                // Tratamento de exceção
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Conclusão", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
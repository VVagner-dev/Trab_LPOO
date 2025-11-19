package gui;

import entidades.Equipamento;
import servicos.EquipamentoServico;
import servicos.ManutencaoServico;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Formulário para cadastrar Manutenções (Simples, Recorrente, Urgente),
 * demonstrando Polimorfismo e Herança na GUI.
 */
public class FormularioManutencao extends JDialog {

    private EquipamentoServico equipamentoServico;
    private ManutencaoServico manutencaoServico;

    // Campos comuns a todas as manutenções
    private JTextField txtId, txtDescricao, txtData, txtValorBase;
    private JComboBox<Equipamento> cmbEquipamento;
    private JComboBox<String> cmbTipoManutencao;

    // Painéis e campos específicos de subclasses (Herança)
    private JPanel painelExtra;
    private JTextField txtValorExtra; // Para desconto ou taxa
    private JLabel lblValorExtra; // Rótulo dinâmico

    public FormularioManutencao(JFrame parent, EquipamentoServico equipamentoServico, ManutencaoServico manutencaoServico) {
        super(parent, "Cadastrar Manutenção", true);
        this.equipamentoServico = equipamentoServico;
        this.manutencaoServico = manutencaoServico;

        setSize(450, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Painel central com GridLayout para organização de 2 colunas
        JPanel painelCentral = new JPanel(new GridLayout(7, 2, 10, 10));
        painelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. ID
        painelCentral.add(new JLabel("ID da Manutenção:"));
        txtId = new JTextField();
        painelCentral.add(txtId);

        // 2. Equipamento (JCombobox) - Associa a manutenção ao equipamento
        painelCentral.add(new JLabel("Equipamento:"));
        cmbEquipamento = new JComboBox<>();
        carregarEquipamentos();
        painelCentral.add(cmbEquipamento);

        // 3. Descrição
        painelCentral.add(new JLabel("Descrição:"));
        txtDescricao = new JTextField();
        painelCentral.add(txtDescricao);

        // 4. Data
        painelCentral.add(new JLabel("Data (AAAA-MM-DD):"));
        txtData = new JTextField(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        painelCentral.add(txtData);

        // 5. Valor Base
        painelCentral.add(new JLabel("Valor Base (R$):"));
        txtValorBase = new JTextField();
        painelCentral.add(txtValorBase);

        // 6. Tipo de Manutenção (determina a subclasse a ser criada)
        painelCentral.add(new JLabel("Tipo de Manutenção:"));
        String[] tipos = {"Simples", "Recorrente", "Urgente"};
        cmbTipoManutencao = new JComboBox<>(tipos);
        cmbTipoManutencao.addActionListener(e -> atualizarCamposExtras());
        painelCentral.add(cmbTipoManutencao);

        // Painel Extra para Desconto/Taxa (só aparece se o tipo for Recorrente/Urgente)
        painelExtra = new JPanel(new GridLayout(1, 2, 10, 10));
        lblValorExtra = new JLabel("");
        txtValorExtra = new JTextField();
        painelExtra.add(lblValorExtra);
        painelExtra.add(txtValorExtra);

        // Painel para organizar o painel extra e o botão Salvar
        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.add(painelExtra, BorderLayout.NORTH);

        JButton btnSalvar = new JButton("Salvar Manutenção");
        btnSalvar.addActionListener(e -> cadastrarManutencao());

        painelSul.add(btnSalvar, BorderLayout.SOUTH);

        add(painelCentral, BorderLayout.CENTER);
        add(painelSul, BorderLayout.SOUTH);

        atualizarCamposExtras();
    }

    /**
     * Carrega a lista de equipamentos disponíveis para seleção.
     */
    private void carregarEquipamentos() {
        List<Equipamento> equipamentos = equipamentoServico.listar();
        if (equipamentos.isEmpty()) {
            cmbEquipamento.addItem(new Equipamento(0, "Nenhum Equipamento Cadastrado", ""));
            cmbEquipamento.setEnabled(false);
            // Mensagem de aviso já é tratada na TelaPrincipal.main.
            return;
        }
        for (Equipamento e : equipamentos) {
            cmbEquipamento.addItem(e);
        }
    }

    /**
     * Atualiza os campos de Desconto ou Taxa dependendo do tipo de manutenção selecionado.
     */
    private void atualizarCamposExtras() {
        String tipoSelecionado = (String) cmbTipoManutencao.getSelectedItem();

        if ("Recorrente".equals(tipoSelecionado)) {
            lblValorExtra.setText("Desconto (%):");
            painelExtra.setVisible(true);
        } else if ("Urgente".equals(tipoSelecionado)) {
            lblValorExtra.setText("Taxa Urgência (%):");
            painelExtra.setVisible(true);
        } else {
            painelExtra.setVisible(false);
        }
        revalidate(); // Força o layout a recalcular o tamanho
        repaint();
    }

    /**
     * Coleta os dados do formulário e chama o método de cadastro polimórfico no serviço.
     */
    private void cadastrarManutencao() {
        try {
            // 1. Coleta e Validação (Tratamento de Exceção para formatos)
            Integer id = Integer.parseInt(txtId.getText());
            Equipamento equipamento = (Equipamento) cmbEquipamento.getSelectedItem();
            if (equipamento == null || equipamento.getId() == 0) {
                JOptionPane.showMessageDialog(this, "Selecione um equipamento válido.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String descricao = txtDescricao.getText();
            LocalDate data = LocalDate.parse(txtData.getText(), DateTimeFormatter.ISO_LOCAL_DATE);
            double valorBase = Double.parseDouble(txtValorBase.getText().replace(',', '.'));

            boolean concluida = false;
            String tipoSelecionado = (String) cmbTipoManutencao.getSelectedItem();

            // 2. Chamada polimórfica ao serviço (instancia a subclasse correta)
            switch (tipoSelecionado) {
                case "Simples":
                    manutencaoServico.cadastrarSimples(id, equipamento, descricao, data, valorBase, concluida);
                    break;
                case "Recorrente":
                    double desconto = Double.parseDouble(txtValorExtra.getText().replace(',', '.'));
                    manutencaoServico.cadastrarRecorrente(id, equipamento, descricao, data, valorBase, concluida, desconto);
                    break;
                case "Urgente":
                    double taxa = Double.parseDouble(txtValorExtra.getText().replace(',', '.'));
                    manutencaoServico.cadastrarUrgente(id, equipamento, descricao, data, valorBase, concluida, taxa);
                    break;
            }

            // 3. Feedback e Fechamento
            JOptionPane.showMessageDialog(this, tipoSelecionado + " cadastrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID, Valor Base, Taxa ou Desconto devem ser números válidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use AAAA-MM-DD.", "Erro de Data", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            // Captura exceções do serviço (ID Duplicado)
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
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

public class FormularioManutencao extends JDialog {

    private EquipamentoServico equipamentoServico;
    private ManutencaoServico manutencaoServico;

    // Campos comuns a todas as manutenções
    private JTextField txtId, txtDescricao, txtData, txtValorBase;
    private JComboBox<Equipamento> cmbEquipamento;
    private JComboBox<String> cmbTipoManutencao;

    // Painéis e campos específicos de subclasses
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

        JPanel painelCentral = new JPanel(new GridLayout(7, 2, 10, 10));
        painelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. ID
        painelCentral.add(new JLabel("ID da Manutenção:"));
        txtId = new JTextField();
        painelCentral.add(txtId);

        // 2. Equipamento (JCombobox)
        painelCentral.add(new JLabel("Equipamento:"));
        cmbEquipamento = new JComboBox<>();
        carregarEquipamentos(); // Popula o combobox
        painelCentral.add(cmbEquipamento);

        // 3. Descrição
        painelCentral.add(new JLabel("Descrição:"));
        txtDescricao = new JTextField();
        painelCentral.add(txtDescricao);

        // 4. Data
        painelCentral.add(new JLabel("Data (AAAA-MM-DD):"));
        txtData = new JTextField(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)); // Data padrão
        painelCentral.add(txtData);

        // 5. Valor Base
        painelCentral.add(new JLabel("Valor Base (R$):"));
        txtValorBase = new JTextField();
        painelCentral.add(txtValorBase);

        // 6. Tipo de Manutenção (Simples, Recorrente, Urgente)
        painelCentral.add(new JLabel("Tipo de Manutenção:"));
        String[] tipos = {"Simples", "Recorrente", "Urgente"};
        cmbTipoManutencao = new JComboBox<>(tipos);
        cmbTipoManutencao.addActionListener(e -> atualizarCamposExtras());
        painelCentral.add(cmbTipoManutencao);

        // Painel Extra para Desconto/Taxa
        painelExtra = new JPanel(new GridLayout(1, 2, 10, 10));
        lblValorExtra = new JLabel("");
        txtValorExtra = new JTextField();
        painelExtra.add(lblValorExtra);
        painelExtra.add(txtValorExtra);

        // Colocamos o painel extra no final, por fora do GridLayout principal
        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.add(painelExtra, BorderLayout.NORTH);

        JButton btnSalvar = new JButton("Salvar Manutenção");
        btnSalvar.addActionListener(e -> cadastrarManutencao());

        painelSul.add(btnSalvar, BorderLayout.SOUTH);

        add(painelCentral, BorderLayout.CENTER);
        add(painelSul, BorderLayout.SOUTH);

        atualizarCamposExtras(); // Inicializa os campos extras corretamente
    }

    private void carregarEquipamentos() {
        List<Equipamento> equipamentos = equipamentoServico.listar();
        if (equipamentos.isEmpty()) {
            cmbEquipamento.addItem(new Equipamento(0, "Nenhum Equipamento Cadastrado", ""));
            cmbEquipamento.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Cadastre um equipamento antes de cadastrar manutenções.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        for (Equipamento e : equipamentos) {
            cmbEquipamento.addItem(e);
        }
    }

    // Mostra/Esconde e configura os campos de Taxa/Desconto
    private void atualizarCamposExtras() {
        String tipoSelecionado = (String) cmbTipoManutencao.getSelectedItem();

        // Define o layout para mostrar ou esconder o painel extra
        if ("Recorrente".equals(tipoSelecionado)) {
            lblValorExtra.setText("Desconto (%):");
            painelExtra.setVisible(true);
        } else if ("Urgente".equals(tipoSelecionado)) {
            lblValorExtra.setText("Taxa Urgência (%):");
            painelExtra.setVisible(true);
        } else {
            painelExtra.setVisible(false);
        }
        revalidate();
        repaint();
    }

    private void cadastrarManutencao() {
        try {
            // 1. Validação dos campos comuns
            Integer id = Integer.parseInt(txtId.getText());
            Equipamento equipamento = (Equipamento) cmbEquipamento.getSelectedItem();
            if (equipamento == null || equipamento.getId() == 0) {
                throw new RuntimeException("Selecione um equipamento válido.");
            }
            String descricao = txtDescricao.getText();

            // Tratamento de exceção: Data
            LocalDate data = LocalDate.parse(txtData.getText(), DateTimeFormatter.ISO_LOCAL_DATE);

            // Tratamento de exceção: Valor Base
            double valorBase = Double.parseDouble(txtValorBase.getText().replace(',', '.'));

            // A manutenção começa como pendente
            boolean concluida = false;

            String tipoSelecionado = (String) cmbTipoManutencao.getSelectedItem();

            // 2. Chamada polimórfica ao serviço com base no tipo
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
            // Captura exceções do serviço (ID Duplicado, Equipamento inválido)
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
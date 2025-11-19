package gui;

import servicos.EquipamentoServico;
import servicos.ManutencaoServico;
import javax.swing.*;
import java.awt.*;

public class TelaPrincipal extends JFrame {

    private EquipamentoServico equipamentoServico;
    private ManutencaoServico manutencaoServico;

    public TelaPrincipal(EquipamentoServico equipamentoServico, ManutencaoServico manutencaoServico) {
        this.equipamentoServico = equipamentoServico;
        this.manutencaoServico = manutencaoServico;

        setTitle("Sistema de Gestão de Manutenção de Equipamentos (LPOO)");
        setSize(500, 350);
        // Não fecha imediatamente, espera pelo método sairEsalvar
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        // GridLayout para alinhar os botões do menu principal de forma organizada
        setLayout(new GridLayout(6, 1, 10, 10));

        // Botões para as funcionalidades
        JButton btnCadastrarEquipamento = new JButton("1. Cadastrar Equipamento");
        JButton btnListarEquipamentos = new JButton("2. Listar e Remover Equipamentos");
        JButton btnCadastrarManutencao = new JButton("3. Cadastrar Manutenção");
        JButton btnListarManutencoes = new JButton("4. Listar Todas as Manutenções");
        JButton btnListarPendentes = new JButton("5. Listar Manutenções Pendentes (e Concluir)");
        JButton btnSair = new JButton("6. Salvar e Sair");

        add(btnCadastrarEquipamento);
        add(btnListarEquipamentos);
        add(btnCadastrarManutencao);
        add(btnListarManutencoes);
        add(btnListarPendentes);
        add(btnSair);

        // Ações dos botões
        btnCadastrarEquipamento.addActionListener(e -> new FormularioEquipamento(this, equipamentoServico).setVisible(true));
        btnListarEquipamentos.addActionListener(e -> new ListaEquipamentos(this, equipamentoServico).setVisible(true));

        btnCadastrarManutencao.addActionListener(e -> new FormularioManutencao(this, equipamentoServico, manutencaoServico).setVisible(true));
        // true: lista todas (sem botão de concluir)
        btnListarManutencoes.addActionListener(e -> new ListaManutencoes(this, manutencaoServico, true).setVisible(true));
        // false: lista pendentes (com botão de concluir)
        btnListarPendentes.addActionListener(e -> new ListaManutencoes(this, manutencaoServico, false).setVisible(true));

        // Ação de Salvar e Sair (Leitura e Gravação)
        btnSair.addActionListener(e -> sairEsalvar());
        // Adiciona um listener para a ação de fechar a janela, garantindo que os dados sejam salvos
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                sairEsalvar();
            }
        });
    }

    /**
     * Salva todos os dados usando os serviços e encerra a aplicação.
     */
    private void sairEsalvar() {
        // Chamando os métodos de persistência para salvar nos arquivos CSV
        equipamentoServico.salvar();
        manutencaoServico.salvar();
        JOptionPane.showMessageDialog(this, "Dados salvos com sucesso nos arquivos CSV!", "Salvar", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    /**
     * Ponto de entrada principal da aplicação.
     */
    public static void main(String[] args) {
        // Inicialização dos serviços e injeção de dependência
        EquipamentoServico equipamentoServico = new EquipamentoServico();
        ManutencaoServico manutencaoServico = new ManutencaoServico(equipamentoServico);

        // Carregar dados ao iniciar o programa (Leitura e Gravação)
        equipamentoServico.carregar();
        manutencaoServico.carregar();

        // Garante que a GUI seja iniciada na thread correta do Swing
        SwingUtilities.invokeLater(() -> new TelaPrincipal(equipamentoServico, manutencaoServico).setVisible(true));
    }
}
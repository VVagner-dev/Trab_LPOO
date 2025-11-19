package servicos;

import entidades.*;
import repositorio.ManutencaoRepositorioArquivo;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManutencaoServico {

    private List<Manutencao> manutencoes = new ArrayList<>();
    private ManutencaoRepositorioArquivo repositorio;
    private EquipamentoServico equipamentoServico;

    // O serviço de Manutenção precisa do serviço de Equipamento para buscar o Equipamento no método carregar().
    public ManutencaoServico(EquipamentoServico equipamentoServico) {
        this.repositorio = new ManutencaoRepositorioArquivo();
        this.equipamentoServico = equipamentoServico;
    }

    public void cadastrarSimples(Integer id,
                                 Equipamento equipamento,
                                 String descricao,
                                 LocalDate data,
                                 double valorBase,
                                 boolean concluida) {

        validarId(id);
        // Polimorfismo: Instancia ManutencaoSimples
        manutencoes.add(new ManutencaoSimples(id, equipamento, descricao, data, valorBase, concluida));
    }

    public void cadastrarUrgente(Integer id,
                                 Equipamento equipamento,
                                 String descricao,
                                 LocalDate data,
                                 double valorBase,
                                 boolean concluida,
                                 double taxaUrgencia) {

        validarId(id);
        // Polimorfismo: Instancia ManutencaoUrgente
        manutencoes.add(new ManutencaoUrgente(id, equipamento, descricao, data, valorBase, concluida, taxaUrgencia));
    }

    public void cadastrarRecorrente(Integer id,
                                    Equipamento equipamento,
                                    String descricao,
                                    LocalDate data,
                                    double valorBase,
                                    boolean concluida,
                                    double desconto) {

        validarId(id);
        // Polimorfismo: Instancia ManutencaoRecorrente
        manutencoes.add(new ManutencaoRecorrente(id, equipamento, descricao, data, valorBase, concluida, desconto));
    }

    private void validarId(Integer id) {
        for (Manutencao m : manutencoes) {
            if (m.getId().equals(id)) {
                // Tratamento de exceção: ID duplicado
                throw new RuntimeException("ID de manutenção já existe: (" + id + ")");
            }
        }
    }

    public Manutencao buscarPorId(Integer id) {
        for (Manutencao m : manutencoes) {
            if (m.getId().equals(id)) {
                return m;
            }
        }
        return null;
    }

    public List<Manutencao> listar() {
        return manutencoes;
    }

    public List<Manutencao> listarPorEquipamento(Integer equipamentoId) {
        List<Manutencao> result = new ArrayList<>();
        for (Manutencao m : manutencoes) {
            if (m.getEquipamento().getId().equals(equipamentoId)) {
                result.add(m);
            }
        }
        return result;
    }

    public List<Manutencao> listarConcluidas() {
        List<Manutencao> result = new ArrayList<>();
        for (Manutencao m : manutencoes) {
            if (m.isConcluida()) {
                result.add(m);
            }
        }
        return result;
    }

    public List<Manutencao> listarPendentes() {
        List<Manutencao> result = new ArrayList<>();
        for (Manutencao m : manutencoes) {
            if (!m.isConcluida()) {
                result.add(m);
            }
        }
        return result;
    }

    public void removerPorId(Integer id) {
        Manutencao encontrada = buscarPorId(id);
        if (encontrada == null) {
            // Tratamento de exceção: Manutenção não encontrada
            throw new RuntimeException("Manutenção não encontrada: (" + id + ")");
        }
        manutencoes.remove(encontrada);
    }

    // Método para concluir uma manutenção
    public void concluirManutencao(Integer id) {
        Manutencao m = buscarPorId(id);
        if (m == null) {
            throw new RuntimeException("Manutenção não encontrada: (" + id + ")");
        }
        m.setConcluida(true);
    }

    // Método para carregar os dados do arquivo CSV
    public void carregar() {
        // Leitura e gravação: O repositório precisa da lista de Equipamentos para carregar as Manutenções corretamente.
        this.manutencoes = repositorio.carregar(equipamentoServico.listar());
    }

    // Método para salvar os dados no arquivo CSV
    public void salvar() {
        try {
            // Leitura e gravação: Chama o repositório para persistir a coleção.
            repositorio.salvar(this.manutencoes);
        } catch (RuntimeException e) {
            // Tratamento de exceção: Erro ao salvar o arquivo (captura a exceção propagada do Repositório)
            System.err.println("Erro ao salvar manutenções: " + e.getMessage());
        }
    }
}
package servicos;

import entidades.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManutencaoServico {

    private List<Manutencao> manutencoes = new ArrayList<>();

    public void cadastrarSimples(Integer id,
                                 Equipamento equipamento,
                                 String descricao,
                                 LocalDate data,
                                 double valorBase,
                                 boolean concluida) {

        validarId(id);
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
        manutencoes.add(new ManutencaoRecorrente(id, equipamento, descricao, data, valorBase, concluida, desconto));
    }

    private void validarId(Integer id) {
        for (Manutencao m : manutencoes) {
            if (m.getId().equals(id)) {
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
            throw new RuntimeException("Manutenção não encontrada: (" + id + ")");
        }
        manutencoes.remove(encontrada);
    }
}

package entidades;

import java.time.LocalDate;

/**
 * Representa uma manutenção simples sem custos adicionais ou descontos.
 */
public class ManutencaoSimples extends Manutencao {

    public ManutencaoSimples(Integer id, Equipamento equipamento, String descricao, LocalDate data, double valorBase, boolean concluida) {
        super(id, equipamento, descricao, data, valorBase, concluida);
    }

    /**
     * Implementação polimórfica:
     * O custo é igual ao valor base, sem alteração.
     */
    @Override
    public double calcularCusto() {
        return getValorBase();
    }
}
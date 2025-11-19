package entidades;

import java.time.LocalDate;

public class ManutencaoSimples extends Manutencao {

    public ManutencaoSimples(Integer id, Equipamento equipamento, String descricao, LocalDate data, double valorBase, boolean concluida) {
        super(id, equipamento, descricao, data, valorBase, concluida);
    }

    @Override
    public double calcularCusto() {
    return getValorBase();
    }
}

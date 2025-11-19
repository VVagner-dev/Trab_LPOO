package entidades;

import java.time.LocalDate;

public class ManutencaoRecorrente extends Manutencao {
    private double desconto;

    public ManutencaoRecorrente(Integer id, Equipamento equipamento, String descricao, LocalDate data, double valorBase, boolean concluida, double desconto) {
        super(id, equipamento, descricao, data, valorBase, concluida);
        this.desconto = desconto;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }

    @Override
    public double calcularCusto() {
        return getValorBase()-(getValorBase()*(desconto/100));
    }
}

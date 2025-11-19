package entidades;

import java.time.LocalDate;

public class ManutencaoUrgente extends Manutencao {
    private double taxaUrgencia;

    public ManutencaoUrgente(Integer id, Equipamento equipamento, String descricao, LocalDate data, double valorBase, boolean concluida, double taxaUrgencia) {
        super(id, equipamento, descricao, data, valorBase, concluida);
        this.taxaUrgencia = taxaUrgencia;
    }

    public double getTaxaUrgencia() {
        return taxaUrgencia;
    }

    public void setTaxaUrgencia(double taxaUrgencia) {
        this.taxaUrgencia = taxaUrgencia;
    }

    @Override
    public double calcularCusto() {
        return getValorBase()+(getValorBase()*(taxaUrgencia/100));
    }
}


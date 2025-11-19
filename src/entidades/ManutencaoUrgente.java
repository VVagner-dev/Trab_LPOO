package entidades;

import java.time.LocalDate;

/**
 * Representa uma manutenção urgente com uma taxa extra aplicada sobre o valor base.
 */
public class ManutencaoUrgente extends Manutencao {
    private double taxaUrgencia; // em percentual

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

    /**
     * Implementação polimórfica:
     * Custo = Valor Base + (Valor Base * Taxa Urgência / 100)
     */
    @Override
    public double calcularCusto() {
        return getValorBase() + (getValorBase() * (taxaUrgencia / 100.0));
    }
}
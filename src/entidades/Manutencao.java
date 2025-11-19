package entidades;

import java.time.LocalDate;

public abstract class Manutencao {

    private Integer id;
    private Equipamento equipamento;
    private String descricao;
    private LocalDate data;
    private double valorBase;
    private boolean concluida;

    public Manutencao(Integer id, Equipamento equipamento, String descricao, LocalDate data, double valorBase, boolean concluida) {
        this.id = id;
        this.equipamento = equipamento;
        this.descricao = descricao;
        this.data = data;
        this.valorBase = valorBase;
        this.concluida = concluida;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Equipamento getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(Equipamento equipamento) {
        this.equipamento = equipamento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public double getValorBase() {
        return valorBase;
    }

    public void setValorBase(double valorBase) {
        this.valorBase = valorBase;
    }

    public boolean isConcluida() {
        return concluida;
    }

    public void setConcluida(boolean concluida) {
        this.concluida = concluida;
    }

    public void concluir(){

    }

    public boolean getStatus(){
        return status
    }

    public abstract double calcularCusto();

    @Override
    public String toString() {
        return "Manutencao{" +
                "id=" + id +
                ", equipamento=" + equipamento +
                ", descricao='" + descricao + '\'' +
                ", data=" + data +
                ", valorBase=" + valorBase +
                ", concluida=" + concluida +
                '}';
    }
}

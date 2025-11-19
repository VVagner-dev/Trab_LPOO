package entidades;

/**
 * Representa um Equipamento que necessita de manutenção.
 */
public class Equipamento {
    private Integer id;
    private String nome;
    private String tipo;

    public Equipamento(Integer id, String nome, String tipo) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Retorna uma representação em string do objeto.
     * Necessário para exibir o Equipamento corretamente no JComboBox da GUI.
     */
    @Override
    public String toString() {
        return id + " - " + nome + " (" + tipo + ")";
    }
}
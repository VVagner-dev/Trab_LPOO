package servicos;

import entidades.Equipamento;
import repositorio.EquipamentoRepositorioArquivo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EquipamentoServico {

    private List<Equipamento> equipamentos = new ArrayList<>();
    private EquipamentoRepositorioArquivo repositorio;

    public EquipamentoServico() {
        this.repositorio = new EquipamentoRepositorioArquivo();
    }

    public void cadastrar(Integer id, String nome, String tipo){
        for (Equipamento e : equipamentos){
            if(e.getId().equals(id)){
                // Tratamento de exceção: ID duplicado
                throw new RuntimeException("ID já cadastrado: ("+id+")");
            }
        }
        equipamentos.add(new Equipamento(id,nome,tipo));
    }

    public Equipamento buscarPorId(Integer id){
        for (Equipamento e : equipamentos){
            if(e.getId().equals(id)){
                return e;
            }
        }
        return null;
    }

    public List<Equipamento> listar(){
        return equipamentos;
    }

    public void removerPorId(Integer id){
        Equipamento encontrado = buscarPorId(id);
        if(encontrado == null){
            // Tratamento de exceção: Equipamento não encontrado
            throw new RuntimeException("Equipamento não encontrado: ("+id+")");
        }else {
            equipamentos.remove(encontrado);
        }
    }

    // Método para carregar os dados do arquivo CSV
    public void carregar() {
        // Coleções: O repositório carrega a lista de equipamentos.
        this.equipamentos = repositorio.carregar();
    }

    // Método para salvar os dados no arquivo CSV
    public void salvar() {
        try {
            // Leitura e gravação: Chama o repositório para persistir a coleção.
            repositorio.salvar(this.equipamentos);
        } catch (IOException e) {
            // Tratamento de exceção: Erro ao salvar o arquivo
            System.err.println("Erro ao salvar equipamentos: " + e.getMessage());
        }
    }
}
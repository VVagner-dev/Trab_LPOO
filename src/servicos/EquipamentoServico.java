package servicos;

import entidades.Equipamento;

import java.util.ArrayList;
import java.util.List;

public class EquipamentoServico {

    private List<Equipamento> equipamentos = new ArrayList<>();

    public void cadastrar(Integer id, String nome, String tipo){
        for (Equipamento e : equipamentos){
            if(e.getId().equals(id)){
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
        entidades.Equipamento encontrado = buscarPorId(id);
        if(encontrado == null){
            throw new RuntimeException("Equipamento não encontrado: ("+id+")");
        }else {
            equipamentos.remove(encontrado);
        }


    }




}

package repositorio;

import entidades.Equipamento;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EquipamentoRepositorioArquivo {

    private final String caminhoArquivo = "equipamentos.csv";

    public void salvar(List<Equipamento> equipamentos) throws IOException {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo))){
            for(Equipamento e : equipamentos){
                bw.write(e.getId()+ ";"
                        + e.getNome()+ ";"
                        + e.getTipo());
                bw.newLine();
            }
        } catch (IOException e){
            throw new RuntimeException("Erro ao salvar equipamentos: " + e.getMessage());
        }
    }

    public List<Equipamento> carregar(){
        List<Equipamento> lista = new ArrayList<>();

        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()){
            return lista;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))){
            String linha;

            while ((linha = br.readLine()) != null){
                String[] dados = linha.split(";");

                Integer id = Integer.parseInt(dados[0]);
                String nome = dados[1];
                String tipo = dados[2];

                lista.add(new Equipamento(id, nome,tipo));

            }} catch (IOException e){
            throw new RuntimeException("Erro ao carregar equipamentos: "+ e.getMessage());
        }
        return lista;
    }
}



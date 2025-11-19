package repositorio;

import entidades.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManutencaoRepositorioArquivo {

    private final String caminhoArquivo = "manutencoes.csv";

    public void salvar(List<Manutencao> manutencoes) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            for (Manutencao m : manutencoes) {
                String tipo = m.getClass().getSimpleName();
                String linha = m.getId() + ";" +
                        m.getEquipamento().getId() + ";" +
                        m.getDescricao() + ";" +
                        m.getData() + ";" +
                        m.getValorBase() + ";" +
                        m.isConcluida() + ";" +
                        tipo;

                // Campos extras para subclasses
                if (m instanceof ManutencaoRecorrente mr) {
                    linha += ";" + mr.getDesconto();
                } else if (m instanceof ManutencaoUrgente mu) {
                    linha += ";" + mu.getTaxaUrgencia();
                }

                bw.write(linha);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar manutenções: " + e.getMessage());
        }
    }

    public List<Manutencao> carregar(List<Equipamento> equipamentos) {
        List<Manutencao> lista = new ArrayList<>();
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");
                Integer id = Integer.parseInt(dados[0]);
                Integer equipamentoId = Integer.parseInt(dados[1]);
                Equipamento equipamento = equipamentos.stream()
                        .filter(e -> e.getId().equals(equipamentoId))
                        .findFirst()
                        .orElse(null);

                String descricao = dados[2];
                LocalDate data = LocalDate.parse(dados[3]);
                double valorBase = Double.parseDouble(dados[4]);
                boolean concluida = Boolean.parseBoolean(dados[5]);
                String tipo = dados[6];

                switch (tipo) {
                    case "ManutencaoSimples" -> lista.add(new ManutencaoSimples(id, equipamento, descricao, data, valorBase, concluida));
                    case "ManutencaoRecorrente" -> {
                        double desconto = Double.parseDouble(dados[7]);
                        lista.add(new ManutencaoRecorrente(id, equipamento, descricao, data, valorBase, concluida, desconto));
                    }
                    case "ManutencaoUrgente" -> {
                        double taxaUrgencia = Double.parseDouble(dados[7]);
                        lista.add(new ManutencaoUrgente(id, equipamento, descricao, data, valorBase, concluida, taxaUrgencia));
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar manutenções: " + e.getMessage());
        }

        return lista;
    }
}

package repositorio;

import entidades.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por gerenciar a leitura e gravação da coleção de Manutenções
 * em um arquivo CSV, implementando o requisito de persistência de dados.
 */
public class ManutencaoRepositorioArquivo {

    private final String caminhoArquivo = "manutencoes.csv";

    /**
     * Salva a lista de manutenções no arquivo CSV.
     * O método usa o tipo da classe (polimorfismo) para incluir campos extras.
     * @param manutencoes A lista de manutenções a ser salva.
     */
    public void salvar(List<Manutencao> manutencoes) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            for (Manutencao m : manutencoes) {
                // Salva o nome da classe para saber qual tipo reconstruir depois
                String tipo = m.getClass().getSimpleName();

                // Campos base comuns a todas as subclasses
                String linha = m.getId() + ";" +
                        m.getEquipamento().getId() + ";" + // Salva apenas o ID do Equipamento
                        m.getDescricao() + ";" +
                        m.getData() + ";" +
                        m.getValorBase() + ";" +
                        m.isConcluida() + ";" +
                        tipo;

                // Salva campos extras específicos das classes filhas (Herança)
                if (m instanceof ManutencaoRecorrente mr) {
                    linha += ";" + mr.getDesconto();
                } else if (m instanceof ManutencaoUrgente mu) {
                    linha += ";" + mu.getTaxaUrgencia();
                }

                bw.write(linha);
                bw.newLine();
            }
        } catch (IOException e) {
            // Tratamento de exceção: erro de I/O ao salvar
            throw new RuntimeException("Erro ao salvar manutenções: " + e.getMessage());
        }
    }

    /**
     * Carrega a lista de manutenções a partir do arquivo CSV.
     * É necessário receber a lista de Equipamentos para associar cada manutenção ao seu Equipamento correto.
     * @param equipamentos Lista de Equipamentos carregados previamente.
     * @return A lista de Manutenções reconstruída.
     */
    public List<Manutencao> carregar(List<Equipamento> equipamentos) {
        List<Manutencao> lista = new ArrayList<>();
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");

                // 1. Campos base
                Integer id = Integer.parseInt(dados[0]);
                Integer equipamentoId = Integer.parseInt(dados[1]);

                // Busca o objeto Equipamento na lista carregada
                Equipamento equipamento = equipamentos.stream()
                        .filter(e -> e.getId().equals(equipamentoId))
                        .findFirst()
                        .orElse(null);

                // O equipamento deve existir para que a manutenção seja válida
                if (equipamento == null) {
                    System.err.println("Aviso: Equipamento ID " + equipamentoId + " não encontrado para a manutenção " + id);
                    continue; // Pula esta linha
                }

                String descricao = dados[2];
                LocalDate data = LocalDate.parse(dados[3]);
                double valorBase = Double.parseDouble(dados[4]);
                boolean concluida = Boolean.parseBoolean(dados[5]);
                String tipo = dados[6];

                // 2. Reconstrução da classe correta (Herança e Polimorfismo)
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
            // Tratamento de exceção: erro de I/O ao carregar
            throw new RuntimeException("Erro ao carregar manutenções: " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            // Tratamento de exceção: Se o arquivo CSV estiver corrompido ou mal formatado
            throw new RuntimeException("Erro ao analisar linha do arquivo CSV. Verifique o formato: " + e.getMessage());
        }

        return lista;
    }
}
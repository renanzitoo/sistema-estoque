package br.com.estoque.dao;
import br.com.estoque.model.Produto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {
    private static final String CAMINHO_ARQUIVO = "data/produtos.txt";
    private static final String CABECALHO = "# Estrutura: id;nome;categoria;custo;validade;estoque;estoqueDeSeguranca;tempoDeReposicao;consumoMedio;pontoDePedido";

    public ProdutoDAO() {
        garantirArquivoExiste();
    }

    private void garantirArquivoExiste() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        File diretorio = arquivo.getParentFile();

        try {
            if (diretorio != null && !diretorio.exists()) {
                diretorio.mkdirs();
            }
            if (!arquivo.exists()) {
                arquivo.createNewFile();
                salvarProdutos(new ArrayList<>());
            }
        } catch (IOException e) {
            System.err.println("Erro ao criar o arquivo de dados: " + e.getMessage());
        }
    }

    public List<Produto> carregarProdutos() {
        List<Produto> produtos = new ArrayList<>();
        File arquivo = new File(CAMINHO_ARQUIVO);
        
        if (!arquivo.exists()) {
            return produtos;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;

            while ((linha = br.readLine()) != null) {
                linha = linha.trim();

                if (linha.isEmpty() || linha.startsWith("#")) {
                    continue;
                }

                String[] campos = linha.split(";");

                try {
                    if (campos.length >= 7) {
                        int id = Integer.parseInt(campos[0]);
                        String nome = campos[1];
                        String categoria = campos[2];
                        
                        double custo;
                        int estoque;
                        int tempoReposicao = 5; 
                        int consumoMedio = 2;   

                        if (campos.length >= 9) {
                            custo = Double.parseDouble(campos[3].replace(",", "."));
                            int validade = Integer.parseInt(campos[4]);
                            estoque = Integer.parseInt(campos[5]);
                            tempoReposicao = Integer.parseInt(campos[7]);
                            consumoMedio = Integer.parseInt(campos[8]);
                            
                            Produto p = new Produto(id, nome, categoria, custo, validade, estoque, tempoReposicao, consumoMedio);
                            produtos.add(p);
                        } else {
                            custo = Double.parseDouble(campos[4].replace(",", "."));
                            estoque = Integer.parseInt(campos[6]);
                            Produto p = new Produto(id, nome, categoria, custo, 365, estoque, tempoReposicao, consumoMedio);
                            produtos.add(p);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao processar linha: " + linha + " -> " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo de produtos: " + e.getMessage());
        }

        return produtos;
    }

    public boolean salvarProdutos(List<Produto> produtos) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CAMINHO_ARQUIVO))) {
            bw.write(CABECALHO);
            bw.newLine();
            for (Produto produto : produtos) {
                bw.write(produto.toString());
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo de produtos: " + e.getMessage());
            return false;
        }
    }

    public void atualizarProduto(Produto produtoEditado) {
        List<Produto> produtos = carregarProdutos();
        for (int i = 0; i < produtos.size(); i++) {
            if (produtos.get(i).getId() == produtoEditado.getId()) {
                produtos.set(i, produtoEditado);
                break;
            }
        }
        salvarProdutos(produtos);
    }

    public int gerarProximoId(List<Produto> produtos) {
        int maiorId = 0;
        for (Produto p : produtos) {
            if (p.getId() > maiorId) {
                maiorId = p.getId();
            }
        }
        return maiorId + 1;
    }
}

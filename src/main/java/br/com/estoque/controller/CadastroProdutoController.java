package br.com.estoque.controller;

import br.com.estoque.dao.ProdutoDAO;
import br.com.estoque.model.Produto;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CadastroProdutoController implements Initializable {

    @FXML private TextField txtNome;
    @FXML private ComboBox<String> comboCategoria;
    @FXML private TextField txtCusto;
    @FXML private TextField txtValidade;
    @FXML private TextField txtEstoque;
    @FXML private TextField txtTempoReposicao;
    @FXML private TextField txtConsumoMedio;

    private ProdutoDAO produtoDAO;
    private Produto produtoParaEditar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        produtoDAO = new ProdutoDAO();
        comboCategoria.getItems().addAll("Alimentos", "Bebidas", "Limpeza", "Higiene", "Outros", "Padaria", "Salgado", "Confeitaria", "Encomendados", "Diversos");
        comboCategoria.getSelectionModel().selectFirst();
    }

    public void setProdutoParaEditar(Produto produto) {
        this.produtoParaEditar = produto;
        if (produto != null) {
            txtNome.setText(produto.getNome());
            comboCategoria.setValue(produto.getCategoria());
            txtCusto.setText(String.valueOf(produto.getCusto()));
            txtValidade.setText(String.valueOf(produto.getValidade()));
            txtEstoque.setText(String.valueOf(produto.getEstoque()));
            txtTempoReposicao.setText(String.valueOf(produto.getTempoDeReposicaoEmDias()));
            txtConsumoMedio.setText(String.valueOf(produto.getConsumoMedioDiario()));
        }
    }

    @FXML
    private void handleSalvar() {
        if (validarCampos()) {
            try {
                String nome = txtNome.getText();
                String categoria = comboCategoria.getValue();
                double custo = Double.parseDouble(txtCusto.getText().replace(",", "."));
                int validade = Integer.parseInt(txtValidade.getText());
                int estoque = Integer.parseInt(txtEstoque.getText());
                int tempoReposicao = Integer.parseInt(txtTempoReposicao.getText());
                int consumoMedio = Integer.parseInt(txtConsumoMedio.getText());

                if (produtoParaEditar == null) {
                    // Novo Produto
                    List<Produto> produtos = produtoDAO.carregarProdutos();
                    int id = produtoDAO.gerarProximoId(produtos);
                    Produto novo = new Produto(id, nome, categoria, custo, validade, estoque, tempoReposicao, consumoMedio);
                    produtos.add(novo);
                    produtoDAO.salvarProdutos(produtos);
                } else {
                    // Editando Produto
                    produtoParaEditar.setNome(nome);
                    produtoParaEditar.setCategoria(categoria);
                    produtoParaEditar.setCusto(custo);
                    produtoParaEditar.setValidade(validade);
                    produtoParaEditar.setEstoque(estoque);
                    produtoParaEditar.setTempoDeReposicaoEmDias(tempoReposicao);
                    produtoParaEditar.setConsumoMedioDiario(consumoMedio);
                    
                    // Recalcula campos dependentes
                    produtoParaEditar.calcularEstoqueDeSeguranca();
                    produtoParaEditar.calcularPontoDePedido();
                    
                    produtoDAO.atualizarProduto(produtoParaEditar);
                }

                mostrarInformacao("Produto salvo com sucesso!");
                fecharJanela();

            } catch (NumberFormatException e) {
                mostrarErro("Por favor, insira valores numéricos válidos.");
            }
        }
    }

    @FXML
    private void handleCancelar() {
        fecharJanela();
    }

    private boolean validarCampos() {
        if (txtNome.getText().isEmpty() || txtCusto.getText().isEmpty() || txtValidade.getText().isEmpty() ||
                txtEstoque.getText().isEmpty() || txtTempoReposicao.getText().isEmpty() || txtConsumoMedio.getText().isEmpty()) {
            mostrarAviso("Preencha todos os campos!");
            return false;
        }
        return true;
    }

    private void fecharJanela() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }

    private void mostrarErro(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).show();
    }

    private void mostrarAviso(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).show();
    }

    private void mostrarInformacao(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).show();
    }
}

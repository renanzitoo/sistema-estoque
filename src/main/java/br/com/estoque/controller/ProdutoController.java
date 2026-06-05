package br.com.estoque.controller;

import br.com.estoque.dao.ProdutoDAO;
import br.com.estoque.model.Produto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProdutoController implements Initializable {

    @FXML private TableView<Produto> tabelaProdutos;
    @FXML private TableColumn<Produto, Integer> colunaId;
    @FXML private TableColumn<Produto, String> colunaNome;
    @FXML private TableColumn<Produto, String> colunaCategoria;
    @FXML private TableColumn<Produto, Double> colunaCusto;
    @FXML private TableColumn<Produto, Integer> colunaEstoque;
    @FXML private TableColumn<Produto, Integer> colunaEstoqueSeguranca;
    @FXML private TableColumn<Produto, Integer> colunaPontoPedido;
    @FXML private TableColumn<Produto, Integer> colunaConsumoMedio;

    private ProdutoDAO produtoDAO;
    private ObservableList<Produto> listaProdutosObservable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        produtoDAO = new ProdutoDAO();
        configurarColunas();
        carregarDados();
    }

    private void configurarColunas() {
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colunaCusto.setCellValueFactory(new PropertyValueFactory<>("custo"));
        colunaEstoque.setCellValueFactory(new PropertyValueFactory<>("estoque"));
        colunaEstoqueSeguranca.setCellValueFactory(new PropertyValueFactory<>("estoqueDeSeguranca"));
        colunaPontoPedido.setCellValueFactory(new PropertyValueFactory<>("pontoDePedido"));
        colunaConsumoMedio.setCellValueFactory(new PropertyValueFactory<>("consumoMedioDiario"));
    }

    @FXML
    private void carregarDados() {
        List<Produto> produtos = produtoDAO.carregarProdutos();
        listaProdutosObservable = FXCollections.observableArrayList(produtos);
        tabelaProdutos.setItems(listaProdutosObservable);
    }

    @FXML
    private void handleAdicionar() {
        abrirTelaCadastro(null);
    }

    @FXML
    private void handleEditar() {
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAviso("Selecione um produto para editar.");
            return;
        }
        abrirTelaCadastro(selecionado);
    }

    private void abrirTelaCadastro(Produto produto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CadastroProdutoView.fxml"));
            Parent root = loader.load();

            CadastroProdutoController controller = loader.getController();
            controller.setProdutoParaEditar(produto);

            Stage stage = new Stage();
            stage.setTitle(produto == null ? "Cadastrar Produto" : "Editar Produto");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            carregarDados();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarErro("Erro ao abrir tela de cadastro.");
        }
    }

    @FXML
    private void handleAddEstoque() {
        ajustarEstoque(true);
    }

    @FXML
    private void handleRemoverEstoque() {
        ajustarEstoque(false);
    }

    private void ajustarEstoque(boolean isAdicao) {
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAviso("Selecione um produto na tabela.");
            return;
        }

        String titulo = isAdicao ? "Entrada de Estoque" : "Saída de Estoque";
        String msg = isAdicao ? "Quantidade a adicionar:" : "Quantidade a remover:";

        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle(titulo);
        dialog.setHeaderText("Produto: " + selecionado.getNome() + " (Atual: " + selecionado.getEstoque() + ")");
        dialog.setContentText(msg);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(qtdStr -> {
            try {
                int qtd = Integer.parseInt(qtdStr);
                if (qtd > 0) {
                    if (isAdicao) {
                        selecionado.setEstoque(selecionado.getEstoque() + qtd);
                    } else {
                        if (selecionado.getEstoque() >= qtd) {
                            selecionado.setEstoque(selecionado.getEstoque() - qtd);
                        } else {
                            mostrarErro("Quantidade insuficiente no estoque!");
                            return;
                        }
                    }
                    produtoDAO.atualizarProduto(selecionado);
                    carregarDados();
                    mostrarInformacao("Estoque atualizado com sucesso!");
                } else {
                    mostrarAviso("Insira um valor maior que zero.");
                }
            } catch (NumberFormatException e) {
                mostrarErro("Valor inválido.");
            }
        });
    }

    @FXML
    private void handleRemover() {
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAviso("Nenhum produto selecionado.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, "Deseja realmente excluir o produto: " + selecionado.getNome() + "?", ButtonType.YES, ButtonType.NO);
        confirmacao.showAndWait();

        if (confirmacao.getResult() == ButtonType.YES) {
            listaProdutosObservable.remove(selecionado);
            produtoDAO.salvarProdutos(listaProdutosObservable);
            mostrarInformacao("Produto removido com sucesso!");
        }
    }

    @FXML
    private void handleAtualizar() {
        carregarDados();
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

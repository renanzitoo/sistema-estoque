package br.com.estoque.controller;

import br.com.estoque.dao.ProdutoDAO;
import br.com.estoque.model.Produto;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AlertasController implements Initializable {

    @FXML private TableView<Produto> tabelaAlertas;
    @FXML private TableColumn<Produto, Integer> colunaId;
    @FXML private TableColumn<Produto, String> colunaNome;
    @FXML private TableColumn<Produto, Integer> colunaEstoque;
    @FXML private TableColumn<Produto, Integer> colunaPontoPedido;
    @FXML private TableColumn<Produto, Integer> colunaSugestaoCompra;
    @FXML private TableColumn<Produto, String> colunaCategoria;
    @FXML private Label lblContagemAlertas;

    private ProdutoDAO produtoDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        produtoDAO = new ProdutoDAO();
        configurarColunas();
        carregarAlertas();
    }

    private void configurarColunas() {
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaEstoque.setCellValueFactory(new PropertyValueFactory<>("estoque"));
        colunaPontoPedido.setCellValueFactory(new PropertyValueFactory<>("pontoDePedido"));
        colunaCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        // Cálculo dinâmico para sugestão de compra: (Ponto de Pedido - Estoque Atual) + Margem
        colunaSugestaoCompra.setCellValueFactory(cellData -> {
            Produto p = cellData.getValue();
            int sugestao = p.getPontoDePedido() - p.getEstoque();
            return new SimpleIntegerProperty(Math.max(sugestao, 0)).asObject();
        });
    }

    @FXML
    private void carregarAlertas() {
        List<Produto> todos = produtoDAO.carregarProdutos();
        
        // Filtra apenas produtos onde estoque <= ponto de pedido
        List<Produto> filtrados = todos.stream()
                .filter(p -> p.getEstoque() <= p.getPontoDePedido())
                .collect(Collectors.toList());

        ObservableList<Produto> listaAlertas = FXCollections.observableArrayList(filtrados);
        tabelaAlertas.setItems(listaAlertas);
        
        lblContagemAlertas.setText(filtrados.size() + " itens em alerta");
    }
}

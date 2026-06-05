package br.com.estoque.controller;

import br.com.estoque.dao.ProdutoDAO;
import br.com.estoque.model.Produto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private StackPane contentArea;
    @FXML private VBox homeView;
    @FXML private Label lblTotalProdutos;
    @FXML private Label lblProdutosAlerta;
    @FXML private Button btnDashboard;
    @FXML private Button btnProdutos;
    @FXML private Button btnAlertas;

    private ProdutoDAO produtoDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        produtoDAO = new ProdutoDAO();
        atualizarResumo();
    }

    private void atualizarResumo() {
        List<Produto> produtos = produtoDAO.carregarProdutos();
        lblTotalProdutos.setText(String.valueOf(produtos.size()));

        long alerta = produtos.stream()
                .filter(p -> p.getEstoque() <= p.getPontoDePedido())
                .count();
        lblProdutosAlerta.setText(String.valueOf(alerta));
    }

    @FXML
    private void showDashboard() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(homeView);
        atualizarResumo();
        setActiveButton(btnDashboard);
    }

    @FXML
    private void showProdutos() {
        loadView("/fxml/MainView.fxml", btnProdutos);
    }

    @FXML
    private void showAlertas() {
        loadView("/fxml/AlertasView.fxml", btnAlertas);
    }

    private void loadView(String fxmlPath, Button targetButton) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(node);
            setActiveButton(targetButton);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setActiveButton(Button active) {
        btnDashboard.getStyleClass().remove("nav-button-active");
        btnProdutos.getStyleClass().remove("nav-button-active");
        btnAlertas.getStyleClass().remove("nav-button-active");
        active.getStyleClass().add("nav-button-active");
    }

    @FXML
    private void handleSair() {
        System.exit(0);
    }
}

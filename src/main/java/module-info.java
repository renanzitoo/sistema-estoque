module br.com.estoque {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens br.com.estoque.controller to javafx.fxml;
    opens br.com.estoque.model to javafx.base;
    
    exports br.com.estoque;
}

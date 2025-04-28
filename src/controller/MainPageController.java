package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Menu;
import javafx.scene.layout.BorderPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;

public class MainPageController {

    @FXML
    private Menu accueilMenu;

    @FXML
    private Menu panierMenu;

    @FXML
    private Menu compteMenu;

    @FXML
    private AnchorPane contentArea;

    @FXML
    public void initialize() {

        accueilMenu.setOnMenuValidation(event -> loadView("/view/AccueilView.fxml"));

        panierMenu.setOnMenuValidation(event -> loadView("/view/PanierView.fxml"));

        compteMenu.setOnMenuValidation(event -> loadView("/view/CompteView.fxml"));

        // charger par defaut page accueil
        loadView("/view/AccueilView.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            AnchorPane view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

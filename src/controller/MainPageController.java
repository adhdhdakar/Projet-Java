package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.MenuItem;

import java.io.IOException;

public class MainPageController {

    @FXML
    private MenuItem accueilItem;

    @FXML
    private MenuItem panierItem;

    @FXML
    private MenuItem compteItem;

    @FXML
    private AnchorPane contentArea;

    @FXML
    public void initialize() {
        // Charger directement la page d'accueil au début
        loadView("/view/AccueilView.fxml");
    }

    @FXML
    private void goAccueil() {
        loadView("/view/AccueilView.fxml");
    }

    @FXML
    private void goPanier() {
        loadView("/view/PanierView.fxml");
    }

    @FXML
    private void goCompte() {
        loadView("/view/CompteView.fxml");
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

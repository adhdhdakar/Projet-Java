package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainPageController {

    @FXML
    private BorderPane mainPane;

    @FXML
    private void initialize() {
        showAccueil();
    }

    @FXML
    private void showAccueil() {
        loadPage("AccueilView.fxml");
    }

    @FXML
    private void showCart() {
        loadPage("CartContent.fxml");
    }

    @FXML
    private void showCompte() {
        loadPage("CompteView.fxml");
    }

    private void loadPage(String page) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/view/" + page));
            mainPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package controller;

import dao.ClientDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Client;

import java.io.IOException;

public class SignUpController {

    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField mdpField;
    @FXML private Button signUpButton;

    @FXML
    private void handleSignUp(ActionEvent event) {
        String prenom = prenomField.getText();
        String email  = emailField.getText();
        String mdp    = mdpField.getText();

        if (prenom.isEmpty() || email.isEmpty() || mdp.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        Client client = new Client(0, prenom, "", email, mdp, "nouveau");
        boolean success = new ClientDAO().create(client);

        if (!success) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Email déjà utilisé ?");
            return;
        }

        // Inscription OK → on bascule sur la MainPage
        try {
            // 1) Récupère la fenêtre actuelle
            Stage stage = (Stage) signUpButton.getScene().getWindow();

            // 2) Charge le FXML de la page principale
            Parent mainRoot = FXMLLoader.load(
                    getClass().getResource("/MainPage.fxml")
            );

            // 3) Remplace la scène
            stage.setScene(new Scene(mainRoot, 800, 600));
            stage.setTitle("Accueil - Boutique");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Erreur interne",
                    "Impossible de charger la page principale.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
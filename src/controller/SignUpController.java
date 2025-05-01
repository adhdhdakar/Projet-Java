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

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField mdpField;

    @FXML
    private Button signUpButton;

    @FXML
    private void handleSignUp(ActionEvent event) {
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String mdp = mdpField.getText();

        if (prenom.isEmpty() || email.isEmpty() || mdp.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        Client client = new Client(0, prenom, "", email, mdp, "nouveau");
        ClientDAO dao = new ClientDAO();
        boolean success = dao.create(client);

        if (success) {

            redirectToLogin();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Email déjà utilisé ?");
        }
    }

    private void redirectToLogin() {
        try {
            // Fermer la fenêtre actuelle (signup)
            signUpButton.getScene().getWindow().hide();

            // Charger et afficher la fenêtre de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Page de connexion");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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

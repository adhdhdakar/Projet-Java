package controller;

import dao.ClientDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
    private TextField nomField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField mdpField;

    @FXML
    private Button signUpButton;

    @FXML
    private void handleSignUp(ActionEvent event) {
        String prenom = prenomField.getText();
        String nom = nomField.getText();
        String email = emailField.getText();
        String mdp = mdpField.getText();

        if (prenom.isEmpty() || email.isEmpty() || mdp.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        Client client = new Client(0, nom, prenom, email, mdp, "client");
        ClientDAO dao = new ClientDAO();
        boolean success = dao.create(client);

        if (success) {
            redirectToLogin(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Email déjà utilisé ?");
        }
    }

    private void redirectToLogin(ActionEvent event) {
        try {
            // Fermer la fenêtre actuelle
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Page de connexion");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
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

    @FXML
    private void handleRetour(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("Page de connexion");
        stage.setMaximized(true);
        stage.show();
    }
}

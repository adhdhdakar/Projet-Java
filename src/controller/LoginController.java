package controller;

import dao.ClientDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Client;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button signupButton;

    @FXML
    private Label messageLabel;

    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> handleLogin());
    }

    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        ClientDAO clientDAO = new ClientDAO();
        Client client = clientDAO.findByEmailAndPassword(email, password);

        if (client != null) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Bienvenue " + client.getPrenom() + "!");
            System.out.println("Bienvenue " + client.getPrenom() + " " + client.getNom());
        } else {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Erreur d'identifiants !");
        }
    }

    @FXML
    private void openSignupWindow() {
        try {
            // Fermer la fenêtre de connexion
            signupButton.getScene().getWindow().hide();

            // Charger la page d'inscription
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/signup.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Inscription");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

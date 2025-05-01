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
    public void initialize() {
        loginButton.setOnAction(this::handleLogin);
        signupButton.setOnAction(this::handleSignup);
    }

    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        ClientDAO dao = new ClientDAO();
        Client client = dao.findByEmailAndPassword(email, password);

        if (client != null) {
            System.out.println("Bienvenue " + client.getPrenom() + " " + client.getNom());

            // Fermer la fenêtre de login
            loginButton.getScene().getWindow().hide();

            // Charger et ouvrir MainPage.fxml
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainPage.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Accueil");
                stage.setScene(new Scene(root, 600, 400));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            messageLabel.setText("Erreur d'identifiants !");
        }
    }

    private void handleSignup(ActionEvent event) {
        // Fermer la fenêtre actuelle
        signupButton.getScene().getWindow().hide();

        // Ouvrir Signup.fxml
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SignUp.fxml"));
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

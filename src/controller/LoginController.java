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

    private void openInSameWindow(Parent root, String title) {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.show();
    }

    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        ClientDAO dao = new ClientDAO();
        Client client = dao.findByEmailAndPassword(email, password);

        if (client != null) {
            System.out.println("Bienvenue " + client.getPrenom() + " " + client.getNom());

            // 🔥 SAUVEGARDER LE CLIENT DANS LA SESSION ICI !
            model.Session.getInstance().setClient(client);

            // Fermer la fenêtre de login
            loginButton.getScene().getWindow().hide();

            //System.out.println(client.getTypeClient());

            // Charger et ouvrir MainPage.fxml si c'est un client
            if(client.getTypeClient().equals("client")) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainPage.fxml"));
                    Parent root = loader.load();
                    openInSameWindow(root, "Accueil");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else { // sinon on lance Admin.fxml (car typeClient = admin)
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Admin.fxml"));
                    Parent root = loader.load();
                    Stage stage = new Stage();
                    stage.setTitle("Panneau d'administration");
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.sizeToScene();
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            openInSameWindow(root, "Inscription");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

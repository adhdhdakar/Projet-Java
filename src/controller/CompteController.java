package controller;

import dao.ClientDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import model.Client;
import model.Session;

import java.io.IOException;
import java.sql.SQLException;

public class CompteController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField mdpField;
    @FXML private StackPane contentPane;

    private final ClientDAO clientDAO = new ClientDAO();

    @FXML
    public void initialize() {
        Client client = Session.getInstance().getClient();
        if (client != null) {
            nomField.setText(client.getNom());
            prenomField.setText(client.getPrenom());
            emailField.setText(client.getEmail());
            mdpField.setText(client.getMotDePasse());
        }
    }

    @FXML
    private void handleSave() {
        Client client = Session.getInstance().getClient();
        if (client == null) return;

        client.setNom(nomField.getText());
        client.setPrenom(prenomField.getText());
        client.setEmail(emailField.getText());
        client.setMotDePasse(mdpField.getText());

        try {
            clientDAO.update(client);
            showAlert("Informations mises à jour avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur lors de la mise à jour.");
        }
    }

    @FXML
    private void handleRetour(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainPage.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.setTitle("Accueil");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    @FXML
    private void handleHistorique(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/HistoriqueView.fxml"));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 800, 600));
        stage.setTitle("Historique des commandes");
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        Session.getInstance().setClient(null); // Réinitialise la session

        Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.setTitle("Connexion");
    }


}

package controller;

import dao.ClientDAO;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Client;
import javafx.scene.layout.GridPane;

import java.util.List;

public class ClientsAdminController {

    @FXML
    private TableView<Client> tableClients;

    @FXML
    private TableColumn<Client, String> colNom, colPrenom, colEmail;

    @FXML
    private TextField searchClientField, nomField, prenomField, emailField;

    @FXML
    private Button btnModifierClient, btnSupprimerClient;

    private FilteredList<Client> filteredClients;
    private ClientDAO clientDAO = new ClientDAO();

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
        colPrenom.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPrenom()));
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        loadClients();

        // Filtrage
        searchClientField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredClients.setPredicate(client -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();
                return client.getNom().toLowerCase().contains(lower)
                        || client.getPrenom().toLowerCase().contains(lower)
                        || client.getEmail().toLowerCase().contains(lower);
            });
        });

        btnModifierClient.setOnAction(e -> handleModifierClient());
        btnSupprimerClient.setOnAction(e -> handleSupprimerClient());
    }

    private void loadClients() {
        List<Client> clients = clientDAO.findAll();
        filteredClients = new FilteredList<>(FXCollections.observableArrayList(clients), p -> true);
        tableClients.setItems(filteredClients);
    }

    @FXML
    private void handleModifierClient() {
        Client selected = tableClients.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aucun client sélectionné", "Veuillez sélectionner un client à modifier.");
            return;
        }

        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Modifier un client");
        dialog.setHeaderText("Modifier les informations du client");

        ButtonType modifierButtonType = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(modifierButtonType, ButtonType.CANCEL);

        // Champs du formulaire dans le Dialog
        TextField nomField = new TextField(selected.getNom());
        TextField prenomField = new TextField(selected.getPrenom());
        TextField emailField = new TextField(selected.getEmail());

        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("client", "admin");
        typeComboBox.setValue(selected.getTypeClient());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Nom :"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom :"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Email :"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Type :"), 0, 3);
        grid.add(typeComboBox, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == modifierButtonType) {
                selected.setNom(nomField.getText());
                selected.setPrenom(prenomField.getText());
                selected.setEmail(emailField.getText());
                selected.setTypeClient(typeComboBox.getValue());

                boolean success = clientDAO.updateClientAdmin(selected);
                if (success) {
                    loadClients();
                } else {
                    showAlert("Erreur", "La modification a échoué.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleSupprimerClient() {
        Client selected = tableClients.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aucun client sélectionné", "Veuillez sélectionner un client.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le client " + selected.getNom() + " " + selected.getPrenom() +  " ?");
        alert.setContentText("Cette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = clientDAO.delete(selected.getIdClient());
                if (success) {
                    loadClients();
                } else {
                    showAlert("Erreur", "La suppression a échoué.");
                }
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
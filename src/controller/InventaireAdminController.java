package controller;

import dao.AchatDAO;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Achat;

import java.util.List;

public class InventaireAdminController {

    @FXML
    private TableView<Achat> tableAchats;

    @FXML
    private TableColumn<Achat, String> colArticle;

    @FXML
    private TableColumn<Achat, String> colClient;

    @FXML
    private TableColumn<Achat, Integer> colQuantite;

    @FXML
    private TableColumn<Achat, String> colDate;

    @FXML
    private TableColumn<Achat, Integer> colNum;

    @FXML
    private TextField searchField;

    private FilteredList<Achat> filteredData;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnSupprimer;

    @FXML
    public void initialize() {
        colArticle.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNomArticle()));
        colClient.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNomClient()));
        colQuantite.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getQuantite()).asObject());
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDateAchat()));
        colNum.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getNumCommande()).asObject());

        loadAchats();

        // Filtrage dynamique
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(achat -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lower = newValue.toLowerCase();

                // On convertit le num de la commande en string pour comparer et filtrer
                String numCommandeString = String.valueOf(achat.getNumCommande());

                return achat.getNomArticle().toLowerCase().contains(lower) || achat.getNomClient().toLowerCase().contains(lower) || numCommandeString.contains(lower);
            });
        });
    }

    private void loadAchats() {
        AchatDAO achatDAO = new AchatDAO();
        List<Achat> achats = achatDAO.findAll();
        filteredData = new FilteredList<>(FXCollections.observableArrayList(achats), p -> true);
        tableAchats.setItems(filteredData);
    }

    // + ajouter bouton ajouter supprimer et modifier
    @FXML
    private void handleSupprimer() {
        Achat selected = tableAchats.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aucun achat sélectionné", "Veuillez sélectionner un achat à supprimer.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'achat " + "'" + selected.getNomArticle() + "' (x" + selected.getQuantite() + ") de " + selected.getNomClient() + " ?");
        alert.setContentText("Cette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                AchatDAO dao = new AchatDAO();
                boolean success = dao.delete(selected.getNumCommande(), selected.getIdArticle()); //Suppression dans la base

                if (success) {
                    filteredData.getSource().remove(selected); // on supprime localement
                } else {
                    showAlert("Erreur", "La suppression a échoué.");
                }
            }
        });
    }

    @FXML
    private void handleModifier() {

    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
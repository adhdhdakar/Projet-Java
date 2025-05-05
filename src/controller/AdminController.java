package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class AdminController {

    @FXML
    private StackPane contentPane;

    // Méthode appelée lorsque l'utilisateur clique sur "Inventaire"
    @FXML
    private void handleInventaire() {
        loadView("/view/InventaireAdmin.fxml");
    }

    // Méthode appelée lorsque l'utilisateur clique sur "Offres"
    @FXML
    private void handleOffres() {
        loadView("/view/OffresAdmin.fxml");
    }

    // Méthode appelée lorsque l'utilisateur clique sur "Clients"
    @FXML
    private void handleClients() {
        loadView("/view/ClientsAdmin.fxml");
    }

    // Méthode appelée lorsque l'utilisateur clique sur "Articles"
    @FXML
    private void handleArticles() {
        loadView("/view/ArticlesAdmin.fxml");
    }

    // Méthode appelée lorsque l'utilisateur clique sur "Statistiques"
    @FXML
    private void handleStats() {
        loadView("/view/StatsAdmin.fxml");
    }

    /*

    // Méthode appelée lorsque l'utilisateur clique sur "Déconnexion"
    @FXML
    private void handleLogout() {
        loadView("/path/to/logout.fxml");
    }

    */

    // Méthode générique pour charger une vue dans le StackPane
    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath)); // Charge le fichier FXML
            contentPane.getChildren().setAll(view); // Remplace le contenu du StackPane avec la nouvelle vue
        } catch (IOException e) {
            e.printStackTrace(); // Affiche une erreur en cas de problème de chargement
        }
    }
}
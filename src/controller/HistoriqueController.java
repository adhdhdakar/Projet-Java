package controller;

import dao.CommandeDAO;
import dao.ArticleDAO;
import dao.LigneCommandeDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import model.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class HistoriqueController {

    @FXML private ListView<String> commandeList;
    private final CommandeDAO commandeDAO = new CommandeDAO();
    private final LigneCommandeDAO ligneDAO = new LigneCommandeDAO();
    private final ArticleDAO artDAO         = new ArticleDAO();

    @FXML
    public void initialize() {
        int idClient = Session.getInstance().getClient().getIdClient();
        try {
            List<Commande> commandes = commandeDAO.findValideByClient(idClient);
            for (Commande c : commandes) {
                // 1) récupérer les lignes
                List<LigneCommande> lignes = ligneDAO.findByCommande(c.getIdCommande());
                double totalRecalc = 0;
                for (LigneCommande l : lignes) {
                    Article art = artDAO.findById(l.getIdArticle());
                    ArticleInCart aic = new ArticleInCart(art, l.getQuantite());
                    totalRecalc += aic.getTotal();
                }
                String text = String.format(
                        "Commande #%d  |  Date : %s  |  Total : %.2f €",
                        c.getIdCommande(),
                        c.getDateCommande(),
                        totalRecalc
                );
                commandeList.getItems().add(text);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            commandeList.getItems().add("Erreur de chargement des commandes.");
        }
    }

    @FXML
    private void handleRetour(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/CompteView.fxml"));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.setTitle("Mon Compte");
    }
}

package controller;

import dao.ArticleDAO;
import dao.LigneCommandeDAO;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.Article;
import model.Session;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

public class HomeController {

    @FXML
    private FlowPane productContainer;

    private final ArticleDAO articleDAO = new ArticleDAO();
    private final LigneCommandeDAO ligneDAO = new LigneCommandeDAO();

    @FXML
    public void initialize() {
        List<Article> articles = articleDAO.findAll();
        for (Article a : articles) {
            productContainer.getChildren().add(createCard(a));
        }
    }

    private VBox createCard(Article article) {
        String path = "/images/" + article.getIdArticle() + ".png";
        InputStream is = getClass().getResourceAsStream(path);
        Image img = (is != null)
                ? new Image(is)
                : new Image(getClass().getResourceAsStream("/images/default.png"));

        ImageView iv = new ImageView(img);
        iv.setFitWidth(120);
        iv.setPreserveRatio(true);

        Label lblName = new Label(article.getNom());
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblPrice = new Label(String.format("%.2f €", article.getPrixUnitaire()));
        lblPrice.setStyle("-fx-font-size: 12px;");

        Button btnAdd = new Button("Ajouter au panier");
        btnAdd.setStyle("-fx-background-color: #ff6600; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 8 20;");
        btnAdd.setOnAction(e -> {
            if (Session.getInstance().getClient() == null) {
                showAlert("Veuillez vous connecter.");
                return;
            }

            int idCommande;
            try {
                idCommande = articleDAO.getOrCreatePanierCommande(Session.getInstance().getClient().getIdClient());
                ligneDAO.createOrIncrement(idCommande, article.getIdArticle(), 1);
                showAlert("Ajouté au panier !");
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox card = new VBox(iv, lblName, lblPrice, spacer, btnAdd);
        card.setSpacing(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-background-radius: 10; -fx-border-radius: 10;");
        card.setPrefWidth(160);
        card.setPrefHeight(260);

        return card;
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }
}

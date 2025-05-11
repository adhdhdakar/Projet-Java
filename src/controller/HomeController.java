package controller;

import dao.ArticleDAO;
import dao.LigneCommandeDAO;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
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
import java.util.Optional;

public class HomeController {

    @FXML
    private FlowPane productContainer;
    @FXML
    private ComboBox<String> typeComboBox;

    private final ArticleDAO articleDAO = new ArticleDAO();
    private final LigneCommandeDAO ligneDAO = new LigneCommandeDAO();

    @FXML
    public void initialize() {
        // chargement initial des articles
        List<Article> articles = articleDAO.findAll();
        populateCards(articles);

        // Remplir la ComboBox des types d'article
        List<String> types = articleDAO.findAllTypes();
        typeComboBox.getItems().add("Tous les types");
        typeComboBox.getItems().addAll(types);
        typeComboBox.setValue("Tous les types");

        typeComboBox.setOnAction(e -> filterByType());
    }

    private Image loadImageForArticle(int articleId) {
        String[] exts = { ".png", ".jpg", ".jpeg" };
        for (String ext : exts) {
            String path = "/images/" + articleId + ext;
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) {
                return new Image(is);
            }
        }
        return new Image(getClass().getResourceAsStream("/images/default.png"));
    }


    private VBox createCard(Article article) {
        // Chargement de l’image
        Image img = loadImageForArticle(article.getIdArticle());
        ImageView iv = new ImageView(img);
        iv.setFitWidth(120);
        iv.setPreserveRatio(true);

        // Infos produit
        Label lblName  = new Label(article.getNom());
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label lblPrice = new Label(String.format("%.2f €", article.getPrixUnitaire()));
        lblPrice.setStyle("-fx-font-size: 12px;");

        // Bouton Ajouter au panier
        Button btnAdd = new Button("Ajouter au panier");
        btnAdd.setStyle("-fx-font-size: 14px; -fx-padding: 8 15; -fx-background-color: #ff6600; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-weight: bold;");

        btnAdd.setOnAction(e -> {
            // Vérif connexion
            if (Session.getInstance().getClient() == null) {
                showAlert("Veuillez vous connecter.");
                return;
            }

            // Saisie de la quantité
            TextInputDialog quantityDialog = new TextInputDialog("1");
            quantityDialog.setTitle("Quantité");
            quantityDialog.setHeaderText("Choisir la quantité");
            quantityDialog.setContentText("Quantité :");
            Optional<String> result = quantityDialog.showAndWait();

            if (result.isPresent()) {
                try {
                    int quantity = Integer.parseInt(result.get());

                    // Quantité positive
                    if (quantity <= 0) {
                        showAlert("Veuillez entrer un nombre entier positif.");
                        return;
                    }

                    // Vérif stock dispo
                    int stockDispo = articleDAO.getStock(article.getIdArticle());
                    if (quantity > stockDispo) {
                        showAlert("Stock insuffisant. Disponible : " + stockDispo);
                        return;
                    }

                    // Récupère/crée la commande panier
                    int idCommande = articleDAO.getOrCreatePanierCommande(
                            Session.getInstance().getClient().getIdClient()
                    );

                    // Ajoute/incrémente la ligne
                    ligneDAO.createOrIncrement(idCommande, article.getIdArticle(), quantity);

                    // Décrémente le stock en base
                    articleDAO.decrementStock(article.getIdArticle(), quantity);

                    // Confirmation
                    showAlert("Ajouté au panier !");

                } catch (NumberFormatException ex) {
                    showAlert("Quantité invalide : veuillez entrer un entier.");
                } catch (SQLException ex) {
                    showAlert("Erreur lors de l’ajout au panier : " + ex.getMessage());
                }
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
        card.setAlignment(Pos.CENTER);

        return card;
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }

    @FXML
    private void filterByType() {
        String selected = typeComboBox.getValue();
        List<Article> articles;
        if ("Tous les types".equals(selected)) {
            articles = articleDAO.findAll();
        } else {
            articles = articleDAO.findByType(selected);
        }
        populateCards(articles);
    }

    private void populateCards(List<Article> articles) {
        productContainer.getChildren().clear();
        for (Article a : articles) {
            productContainer.getChildren().add(createCard(a));
        }
    }


}

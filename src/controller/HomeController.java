package controller;

import dao.ArticleDAO;
import dao.LigneCommandeDAO;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import model.Article;
import model.Session;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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
        btnAdd.setOnAction(e -> {
            if (Session.getInstance().getClient() == null) {
                showAlert("Veuillez vous connecter.");
                return;
            }

            // Demande de la quantité à ajouter
            TextInputDialog quantityDialog = new TextInputDialog("1");
            quantityDialog.setTitle("Quantité");
            quantityDialog.setHeaderText("Choisir la quantité");
            quantityDialog.setContentText("Quantité :");
            Optional<String> result = quantityDialog.showAndWait();

            if (result.isPresent()) {
                try {
                    int quantity = Integer.parseInt(result.get());
                    if (quantity <= 0) {
                        showAlert("Veuillez entrer un nombre entier positif.");
                        return;
                    }
                    int idCommande = articleDAO.getOrCreatePanierCommande(
                            Session.getInstance().getClient().getIdClient()
                    );
                    ligneDAO.createOrIncrement(idCommande, article.getIdArticle(), quantity);
                    showAlert("Ajouté au panier !");
                } catch (NumberFormatException ex) {
                    showAlert("Quantité invalide !");
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        VBox card = new VBox(iv, lblName, lblPrice, btnAdd);
        card.setSpacing(8);
        card.setPadding(new Insets(10));
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #ddd; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"
        );
        card.setPrefWidth(165);
        card.setAlignment(Pos.CENTER);
        return card;
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }
}

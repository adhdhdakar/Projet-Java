package controller;

import dao.ArticleDAO;
import dao.CartDAO;
import dao.CommandeDAO;
import dao.LigneCommandeDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.ArticleInCart;
import model.Session;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CartController {

    @FXML private FlowPane cardContainer;
    @FXML private Label totalLabel;

    private final CartDAO cartDAO = new CartDAO();

    @FXML
    public void initialize() {
        refreshPanier();
    }

    private void refreshPanier() {
        cardContainer.getChildren().clear();
        double total = 0;

        int idClient = Session.getInstance().getClient().getIdClient();
        List<ArticleInCart> items = cartDAO.findPanierByClient(idClient);

        for (ArticleInCart item : items) {
            cardContainer.getChildren().add(createCard(item));
            total += item.getTotal();
        }

        totalLabel.setText(String.format("Total : %.2f €", total));
    }

    private VBox createCard(ArticleInCart item) {
        String path = "/images/" + item.getArticle().getIdArticle() + ".png";
        InputStream is = getClass().getResourceAsStream(path);
        Image img = (is != null)
                ? new Image(is)
                : new Image(getClass().getResourceAsStream("/images/default.png"));
        ImageView iv = new ImageView(img);
        iv.setFitWidth(120);
        iv.setPreserveRatio(true);

        Label name = new Label(item.getArticle().getNom());
        Label price = new Label(String.format("Prix : %.2f €", item.getArticle().getPrixUnitaire()));
        Label qty = new Label("Quantité : " + item.getQuantite());
        Label lineTotal = new Label(String.format("Total : %.2f €", item.getTotal()));

        name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        price.setStyle("-fx-font-size: 12px;");
        qty.setStyle("-fx-font-size: 12px;");
        lineTotal.setStyle("-fx-font-size: 12px;");

        VBox card = new VBox(iv, name, price, qty, lineTotal);
        card.setSpacing(8);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5;");
        card.setPrefWidth(160);
        return card;
    }

    @FXML
    private void handleCheckout() {
        try {
            int idClient = Session.getInstance().getClient().getIdClient();
            int idCommande = new ArticleDAO().getOrCreatePanierCommande(idClient);

            List<ArticleInCart> items = cartDAO.findPanierByClient(idClient);

            LigneCommandeDAO ligneDAO = new LigneCommandeDAO();
            ArticleDAO artDAO = new ArticleDAO();
            for (ArticleInCart item : items) {
                int idArticle = item.getArticle().getIdArticle();
                int quantite = item.getQuantite();
                ligneDAO.create(idCommande, idArticle, quantite);
                int newStock = item.getArticle().getStock() - quantite;
                artDAO.updateStock(idArticle, newStock);
            }

            new CommandeDAO().validerCommande(idCommande);

            refreshPanier(); // on recharge depuis la BDD

            new Alert(Alert.AlertType.INFORMATION, "Commande validée !").showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de la validation").showAndWait();
        }
    }

    @FXML
    private void handleReturn(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainPage.fxml"));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 600, 400));
        stage.setTitle("Page Principale");
    }
}

package controller;

import dao.CartDAO;
import dao.CommandeDAO;
import dao.ArticleDAO;
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

    private CartDAO cartDAO = new CartDAO();
    private List<ArticleInCart> items;

    @FXML
    public void initialize() {
        if (Session.getInstance().getClient() == null) {
            System.out.println("Aucun client connecté !");
            return;
        }

        int idClient = Session.getInstance().getClient().getIdClient();
        items = cartDAO.findByClient(idClient);
        for (ArticleInCart it : items) {
            cardContainer.getChildren().add(createCard(it));
        }
        updateTotal();
    }

    private Node createCard(ArticleInCart it) {
        String path = "/images/" + it.getArticle().getIdArticle() + ".png";
        InputStream is = getClass().getResourceAsStream(path);
        Image img = (is != null)
                ? new Image(is)
                : new Image(getClass().getResourceAsStream("/images/default.png"));
        ImageView iv = new ImageView(img);
        iv.setFitWidth(120);
        iv.setPreserveRatio(true);

        Label lblName  = new Label(it.getArticle().getNom());
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblPrice = new Label(
                String.format("Prix unitaire : %.2f €", it.getArticle().getPrixUnitaire())
        );
        lblPrice.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

        Label lblQty   = new Label("Quantité : " + it.getQuantite());
        lblQty.setStyle("-fx-font-size: 12px;");

        Label lblTotal = new Label(
                String.format("Total : %.2f €", it.getTotal())
        );
        lblTotal.setStyle("-fx-font-size: 12px;");

        VBox card = new VBox(iv, lblName, lblPrice, lblQty, lblTotal);
        card.setSpacing(8);
        card.setPadding(new Insets(10));
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #ddd; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"
        );
        card.setPrefWidth(160);
        return card;
    }

    private void updateTotal() {
        double sum = items.stream()
                .mapToDouble(ArticleInCart::getTotal)
                .sum();
        totalLabel.setText(String.format("Total : %.2f €", sum));
    }

    @FXML
    private void handleCheckout() {
        try {
            int idClient = Session.getInstance().getClient().getIdClient();
            CommandeDAO cmdDAO = new CommandeDAO();
            int idCommande = cmdDAO.create(LocalDate.now(), idClient);

            LigneCommandeDAO ligneDAO = new LigneCommandeDAO();
            ArticleDAO artDAO = new ArticleDAO();
            for (ArticleInCart item : items) {
                int idArticle = item.getArticle().getIdArticle();
                int quantite  = item.getQuantite();
                ligneDAO.create(idCommande, idArticle, quantite);
                int newStock = item.getArticle().getStock() - quantite;
                artDAO.updateStock(idArticle, newStock);
            }

            items.clear();
            cardContainer.getChildren().clear();
            updateTotal();

            new Alert(Alert.AlertType.INFORMATION,
                    "✅ Votre commande n°" + idCommande + " a bien été prise en compte !")
                    .showAndWait();

        } catch (SQLException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "❌ Erreur lors de la validation de la commande").showAndWait();
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
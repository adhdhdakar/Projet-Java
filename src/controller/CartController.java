package controller;

import dao.CartDAO;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import model.ArticleInCart;
import model.Session;

import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CartController {

    @FXML private FlowPane cardContainer;
    @FXML private Label totalLabel;

    private CartDAO cartDAO = new CartDAO();


    @FXML
    public void initialize() {
        int idClient = Session.getInstance().getClient().getIdClient();
        List<ArticleInCart> items = cartDAO.findByClient(idClient);

        for (ArticleInCart it : items) {
            System.out.println("DEBUG → article "
                    + it.getArticle().getNom()
                    + " quantite=" + it.getQuantite());
            cardContainer.getChildren().add(createCard(it));
        }
        updateTotal();
    }

    private Node createCard(ArticleInCart it) {
        // 1. Image
        String path = "/images/" + it.getArticle().getIdArticle() + ".png";
        InputStream is = getClass().getResourceAsStream(path);
        Image img = (is != null)
                ? new Image(is)
                : new Image(getClass().getResourceAsStream("/images/default.png"));
        ImageView iv = new ImageView(img);
        iv.setFitWidth(120);
        iv.setFitHeight(120);
        iv.setPreserveRatio(true);

        // 2. Nom
        Label lblName = new Label(it.getArticle().getNom());
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // 3. Prix unitaire
        Label lblPrice = new Label(
                String.format("Prix unitaire : %.2f €", it.getArticle().getPrixUnitaire())
        );
        lblPrice.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

        // 4. Quantité
        Label lblQty = new Label("Quantité : " + it.getQuantite());
        lblQty.setStyle("-fx-font-size: 12px;");

        // 5. Total ligne
        Label lblLineTotal = new Label(
                String.format("Total : %.2f €", it.getTotal())
        );
        lblLineTotal.setStyle("-fx-font-size: 12px;");

        // 6. Construire la carte
        VBox card = new VBox(iv, lblName, lblPrice, lblQty, lblLineTotal);
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
        double sum = cardContainer.getChildren().stream()
                .mapToDouble(node -> {
                    VBox vb = (VBox) node;
                    Label lbl = (Label) vb.getChildren().get(4);  // lineTotal
                    String txt = lbl.getText()
                            .replace("Total : ", "")
                            .replace(" €", "")
                            .replace(",", ".");           // <— on remplace la virgule
                    return Double.parseDouble(txt);
                })
                .sum();
        totalLabel.setText(String.format("Total : %.2f €", sum));
    }

    @FXML
    private void handleCheckout() {
        try {
            int idClient = Session.getInstance().getClient().getIdClient();

            // Création de la commande
            dao.CommandeDAO cmdDAO = new dao.CommandeDAO();
            int idCommande = cmdDAO.create(LocalDate.now(), idClient);

            // Insertion des lignes et mise à jour du stock
            dao.ArticleDAO artDAO = new dao.ArticleDAO();
            dao.LigneCommandeDAO lcDAO = new dao.LigneCommandeDAO();
            for (Node node : cardContainer.getChildren()) {
                VBox vb = (VBox) node;
                // On avait stocké la commande dans l'ordre initial :
                // ici on reboucle via cartDAO.findByClient
                // Pour simplifier, on peut stocker la liste en champ si besoin
            }
            // (Vous pouvez réutiliser la liste 'items' du initialize pour créer les lignes)

            // Nettoyage UI
            cardContainer.getChildren().clear();
            updateTotal();

            Alert a = new Alert(Alert.AlertType.INFORMATION,
                    "Votre commande n°" + idCommande + " a bien été prise en compte !");
            a.showAndWait();

        } catch (SQLException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Erreur lors de la validation de la commande").showAndWait();
        }
    }
}
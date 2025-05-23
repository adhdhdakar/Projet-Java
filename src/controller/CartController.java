package controller;

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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.ArticleInCart;
import model.Commande;
import model.LigneCommande;
import model.Session;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.geometry.Pos;

public class CartController {

    @FXML private FlowPane cardContainer;
    @FXML private Label totalLabel;

    private final CommandeDAO cmdDAO        = new CommandeDAO();
    private final LigneCommandeDAO ligneDAO = new LigneCommandeDAO();
    private final ArticleDAO artDAO         = new ArticleDAO();

    private final List<ArticleInCart> items = new ArrayList<>();
    private Commande cmdEnCours;

    @FXML
    public void initialize() {
        if (Session.getInstance().getClient() == null) {
            System.out.println("Aucun client connecté !");
            return;
        }
        int idClient = Session.getInstance().getClient().getIdClient();

        try {
            cmdEnCours = cmdDAO.findByClientAndStatus(idClient, "EC");
            cmdEnCours = cmdDAO.findByClientAndStatus(idClient, "EC");
            if (cmdEnCours == null) {
                items.clear();
                updateTotal();
                return;
            }

            List<LigneCommande> lignes = ligneDAO.findByCommande(cmdEnCours.getIdCommande());
            for (LigneCommande l : lignes) {
                var art = artDAO.findById(l.getIdArticle());
                items.add(new ArticleInCart(art, l.getQuantite()));
            }

            for (ArticleInCart it : items) {
                cardContainer.getChildren().add(createCard(it));
            }
            updateTotal();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Image loadImageForArticle(int articleId) {
        // Liste des extensions à tester, dans l'ordre de préférence
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

    private Node createCard(ArticleInCart it) {
        // Crée la carte
        VBox card = new VBox();
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

        // Image du produit
        Image img = loadImageForArticle(it.getArticle().getIdArticle());
        ImageView iv = new ImageView(img);
        iv.setFitWidth(120);
        iv.setPreserveRatio(true);

        // Labels
        Label lblName  = new Label(it.getArticle().getNom());
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label lblPrice = new Label(String.format("Prix unitaire : %.2f €", it.getArticle().getPrixUnitaire()));
        lblPrice.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
        Label lblQty   = new Label("Quantité : " + it.getQuantite());
        lblQty.setStyle("-fx-font-size: 12px;");
        Label lblTotal = new Label(String.format("Total : %.2f €", it.getTotal()));
        lblTotal.setStyle("-fx-font-size: 12px;");

        // Bouton Modifier
        Button btnEdit = new Button("Modifier");
        btnEdit.setOnAction(e -> {
            TextInputDialog dlg = new TextInputDialog(String.valueOf(it.getQuantite()));
            dlg.setTitle("Modifier quantité");
            dlg.setHeaderText("Entrez la nouvelle quantité");
            dlg.setContentText("Quantité :");
            Optional<String> res = dlg.showAndWait();
            if (res.isPresent()) {
                try {
                    int oldQty = it.getQuantite();
                    int newQty = Integer.parseInt(res.get());
                    if (newQty <= 0) {
                        showAlert("Veuillez entrer un entier positif.");
                        return;
                    }

                    int diff = newQty - oldQty;
                    // Si on veut augmenter la quantité
                    if (diff > 0) {
                        // 1) Vérifier le stock dispo
                        int stockDispo = artDAO.getStock(it.getArticle().getIdArticle());
                        if (diff > stockDispo) {
                            showAlert("Stock insuffisant. Disponible : " + stockDispo);
                            return;
                        }
                        // 2) Décrémenter le stock de la différence
                        artDAO.decrementStock(it.getArticle().getIdArticle(), diff);
                    }
                    // Si on réduit la quantité, on rend du stock
                    else if (diff < 0) {
                        artDAO.incrementStock(it.getArticle().getIdArticle(), -diff);
                    }

                    // Mise à jour de la ligne en base
                    ligneDAO.updateQuantite(
                            cmdEnCours.getIdCommande(),
                            it.getArticle().getIdArticle(),
                            newQty
                    );

                    // Mise à jour du modèle et de l’affichage
                    it.setQuantite(newQty);
                    lblQty.setText("Quantité : " + newQty);
                    lblTotal.setText(String.format("Total : %.2f €", it.getTotal()));
                    updateTotal();

                } catch (NumberFormatException ex) {
                    showAlert("Quantité invalide !");
                } catch (SQLException ex) {
                    showAlert("Erreur SQL : " + ex.getMessage());
                }
            }
        });

        // Bouton Supprimer
        Button btnDelete = new Button("Supprimer");
        btnDelete.setOnAction(e -> {
            try {
                int qtyToReturn = it.getQuantite();
                // On réinjecte toute la quantité dans le stock
                artDAO.incrementStock(it.getArticle().getIdArticle(), qtyToReturn);

                // On supprime la ligne de commande
                ligneDAO.delete(
                        cmdEnCours.getIdCommande(),
                        it.getArticle().getIdArticle()
                );

                // On retire la carte et l'item du modèle
                cardContainer.getChildren().remove(card);
                items.remove(it);
                updateTotal();

            } catch (SQLException ex) {
                showAlert("Erreur lors de la suppression : " + ex.getMessage());
            }
        });

        HBox actionBox = new HBox(5, btnEdit, btnDelete);
        actionBox.setPadding(new Insets(5, 0, 0, 0));

        // Assemble la carte
        card.getChildren().addAll(iv, lblName, lblPrice, lblQty, lblTotal, actionBox);
        return card;
    }

    private void updateTotal() {
        double sum = items.stream().mapToDouble(ArticleInCart::getTotal).sum();
        totalLabel.setText(String.format("Total : %.2f €", sum));
    }

    @FXML
    private void handleCheckout() {
        if (cmdEnCours == null) {
            new Alert(Alert.AlertType.WARNING, "Aucune commande en cours à valider.").showAndWait();
            return;
        }

        // 1) Demande du code CB
        TextInputDialog cbDialog = new TextInputDialog();
        cbDialog.setTitle("Validation carte bancaire");
        cbDialog.setHeaderText("Veuillez saisir votre code CB");
        cbDialog.setContentText("Code CB :");
        Optional<String> saisieOpt = cbDialog.showAndWait();
        if (saisieOpt.isEmpty()) {
            // L'utilisateur a annulé la saisie
            return;
        }
        String saisieCode = saisieOpt.get().trim();

        // 2) Récupération du code enregistré pour le client
        String codeEnregistre = Session.getInstance().getClient().getCodeCb();
        // (vérifiez que getCodeCb() existe dans votre modèle Client)

        // 3) Vérification
        if (!saisieCode.equals(codeEnregistre)) {
            new Alert(Alert.AlertType.ERROR, "Code CB invalide !").showAndWait();
            return;
        }

        // 4) Si tout est OK, on peut procéder à la validation
        try {
            cmdDAO.updateStatut(cmdEnCours.getIdCommande(), "VA");
            new Alert(Alert.AlertType.INFORMATION,
                    "Votre commande n°" + cmdEnCours.getIdCommande() + " a été validée !").showAndWait();

            items.clear();
            cardContainer.getChildren().clear();
            updateTotal();
            cmdEnCours = null;
        } catch (SQLException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de la validation de la commande.").showAndWait();
        }
    }

    @FXML
    private void handleReturn(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainPage.fxml"));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.setTitle("Page Principale");
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}

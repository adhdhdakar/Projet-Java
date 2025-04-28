package view;

import dao.ArticleDAO;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Article;
import model.PanierItem;

public class CatalogueApp extends Application {

    private final ObservableList<PanierItem> panier = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Catalogue des Articles");

        // TABLE DES ARTICLES
        TableView<Article> tableCatalogue = new TableView<>();

        TableColumn<Article, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<Article, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Article, Double> prixUnitCol = new TableColumn<>("Prix Unitaire");
        prixUnitCol.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));

        TableColumn<Article, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));

        TableColumn<Article, Void> actionCol = new TableColumn<>("Ajouter");

        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button addButton = new Button("Ajouter");

            {
                addButton.setOnAction(event -> {
                    Article article = getTableView().getItems().get(getIndex());
                    ajouterAuPanier(article);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : addButton);
            }
        });

        tableCatalogue.getColumns().addAll(nomCol, descCol, prixUnitCol, stockCol, actionCol);
        ObservableList<Article> articles = FXCollections.observableArrayList(new ArticleDAO().findAll());
        tableCatalogue.setItems(articles);

        // TABLE PANIER
        Label panierLabel = new Label(" Votre Panier :");

        TableView<PanierItem> tablePanier = new TableView<>();

        TableColumn<PanierItem, String> panierNomCol = new TableColumn<>("Nom");
        panierNomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<PanierItem, Double> panierPrixCol = new TableColumn<>("Prix Unitaire");
        panierPrixCol.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));

        TableColumn<PanierItem, Integer> panierQuantiteCol = new TableColumn<>("Quantité");
        panierQuantiteCol.setCellValueFactory(new PropertyValueFactory<>("quantite"));

        TableColumn<PanierItem, Double> panierTotalCol = new TableColumn<>("Total");
        panierTotalCol.setCellValueFactory(new PropertyValueFactory<>("total"));

        TableColumn<PanierItem, Void> panierActionCol = new TableColumn<>("Modifier");

        panierActionCol.setCellFactory(param -> new TableCell<>() {
            private final Button plusButton = new Button("+");
            private final Button minusButton = new Button("-");
            private final HBox box = new HBox(5, minusButton, plusButton);

            {
                plusButton.setOnAction(event -> {
                    PanierItem item = getTableView().getItems().get(getIndex());
                    item.ajouter1();
                    panier.set(getIndex(), item); // met à jour l'affichage
                });

                minusButton.setOnAction(event -> {
                    PanierItem item = getTableView().getItems().get(getIndex());
                    item.retirer1();
                    if (item.getQuantite() <= 0) {
                        panier.remove(item);
                    } else {
                        panier.set(getIndex(), item); // mise à jour
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        tablePanier.getColumns().addAll(panierNomCol, panierPrixCol, panierQuantiteCol, panierTotalCol, panierActionCol);
        tablePanier.setItems(panier);

        VBox root = new VBox(15, tableCatalogue, panierLabel, tablePanier);
        Scene scene = new Scene(root, 750, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void ajouterAuPanier(Article article) {
        for (PanierItem item : panier) {
            if (item.getArticle().getIdArticle() == article.getIdArticle()) {
                item.ajouter1();
                panier.set(panier.indexOf(item), item);
                return;
            }
        }
        panier.add(new PanierItem(article, 1));
    }

    public static void main(String[] args) {
        launch(args);
    }
}

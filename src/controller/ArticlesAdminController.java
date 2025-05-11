package controller;

import dao.ArticleDAO;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.Article;
import javafx.stage.FileChooser;
import javafx.beans.property.SimpleStringProperty;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ArticlesAdminController {

    @FXML private TableView<Article> tableAchats;
    @FXML private TableColumn<Article, String> colArticle, colDescription, colType;
    @FXML private TableColumn<Article, Number> colPrix, colQuantiteVrac, colStock, colPrixVrac;
    @FXML private TextField searchField;

    private final ArticleDAO articleDAO = new ArticleDAO();
    private FilteredList<Article> filteredArticles;
    private File selectedImage = null;
    private String selectedTypeName = null;

    @FXML
    public void initialize() {
        colArticle.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
        colDescription.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));
        colPrix.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrixUnitaire()));
        colPrixVrac.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrixVrac()));
        colQuantiteVrac.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getQuantiteVrac()));
        colStock.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getStock()));

        colType.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getType() != null
                                ? data.getValue().getType()
                                : "—"
                )
        );

        loadArticles();

        searchField.textProperty().addListener((obs, old, newVal) -> {
            filteredArticles.setPredicate(article -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();
                return article.getNom().toLowerCase().contains(lower)
                        || article.getDescription().toLowerCase().contains(lower);
            });
        });
    }

    private void loadArticles() {
        List<Article> list = articleDAO.findAll();
        filteredArticles = new FilteredList<>(FXCollections.observableArrayList(list), p -> true);
        tableAchats.setItems(filteredArticles);
    }

    @FXML
    private void handleAjouter() {
        selectedImage    = null;
        selectedTypeName = null;
        Article article = showArticleDialog(null);
        if (article != null) {
            boolean success = articleDAO.create(article);
            if (success) {
                int newId = articleDAO.findIdByNom(article.getNom());

                // 1) importer l'image si besoin
                if (selectedImage != null) {
                    importImageForArticle(newId, selectedImage);
                }

                // 2) insérer la liaison article ↔ type
                if (selectedTypeName != null) {
                    int idType = articleDAO.findTypeIdByName(selectedTypeName);
                    articleDAO.createArticleType(newId, idType);
                }

                loadArticles();
            } else {
                showAlert("Erreur", "L'ajout a échoué.");
            }
        }
    }

    private void importImageForArticle(int idArticle, File imageFile) {
        try {
            String ext = getFileExtension(imageFile);
            Path dest = Path.of("src/images", idArticle + ext);
            Files.copy(imageFile.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de copier l'image.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleModifier() {
        Article sel = tableAchats.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert("Aucun article sélectionné", "Veuillez sélectionner un article.");
            return;
        }

        // On prépare la fenêtre avec le type existant
        selectedImage    = null;
        selectedTypeName = sel.getType();

        Article modifie = showArticleDialog(sel);
        if (modifie != null) {
            modifie.setIdArticle(sel.getIdArticle());
            boolean success = articleDAO.update(modifie);
            if (success) {
                // 1) image
                if (selectedImage != null) {
                    importImageForArticle(sel.getIdArticle(), selectedImage);
                }
                // 2) mise à jour du type
                articleDAO.deleteArticleType(sel.getIdArticle());
                if (selectedTypeName != null) {
                    int idType = articleDAO.findTypeIdByName(selectedTypeName);
                    articleDAO.createArticleType(sel.getIdArticle(), idType);
                }
                loadArticles();
            } else {
                showAlert("Erreur", "La modification a échoué.");
            }
        }
    }

    @FXML
    private void handleSupprimer() {
        Article selected = tableAchats.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aucun article sélectionné", "Veuillez sélectionner un article.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'article '" + selected.getNom() + "'? ");
        alert.setContentText("Cette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = articleDAO.delete(selected.getIdArticle());
                if (success) {
                    filteredArticles.getSource().remove(selected);
                } else {
                    showAlert("Erreur", "La suppression a échoué.");
                }
            }
        });
    }

    private Article showArticleDialog(Article existing) {
        Dialog<Article> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Ajouter un article" : "Modifier un article");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField nomField      = new TextField();
        TextField descField     = new TextField();
        TextField prixField     = new TextField();
        TextField prixVracField = new TextField();
        TextField vracField     = new TextField();
        TextField stockField    = new TextField();

        // Choix du type
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(articleDAO.findAllTypes());
        typeCombo.setPromptText("Choisir un type");
        typeCombo.valueProperty().addListener((obs, o, n) -> selectedTypeName = n);

        // Bouton image…
        Button imageButton = new Button("Choisir une image");
        Label  imageLabel  = new Label("Aucune image sélectionnée");
        imageButton.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Choisir une image (PNG, JPG, JPEG)");
            fc.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Images", "*.png","*.jpg","*.jpeg")
            );
            File f = fc.showOpenDialog(null);
            if (f != null) {
                selectedImage = f;
                imageLabel.setText(f.getName());
            }
        });

        // Pré-remplissage en mode modification
        if (existing != null) {
            nomField.setText(existing.getNom());
            descField.setText(existing.getDescription());
            prixField.setText(String.valueOf(existing.getPrixUnitaire()));
            prixVracField.setText(String.valueOf(existing.getPrixVrac()));
            vracField.setText(String.valueOf(existing.getQuantiteVrac()));
            stockField.setText(String.valueOf(existing.getStock()));
            typeCombo.setValue(existing.getType());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Nom :"),           nomField);
        grid.addRow(1, new Label("Description :"),   descField);
        grid.addRow(2, new Label("Prix unitaire :"), prixField);
        grid.addRow(3, new Label("Prix vrac :"),     prixVracField);
        grid.addRow(4, new Label("Quantité vrac :"), vracField);
        grid.addRow(5, new Label("Stock :"),         stockField);
        grid.addRow(6, new Label("Type :"),          typeCombo);
        grid.addRow(7, imageButton,                  imageLabel);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    Article a = new Article(
                            0,
                            nomField.getText(),
                            descField.getText(),
                            Double.parseDouble(prixField.getText()),
                            Double.parseDouble(prixVracField.getText()),
                            Integer.parseInt(vracField.getText()),
                            Integer.parseInt(stockField.getText()),
                            selectedTypeName       // on passe le type choisi
                    );
                    return a;
                } catch (NumberFormatException ex) {
                    showAlert("Erreur de saisie", "Veuillez entrer des valeurs numériques valides.");
                }
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }


    private String getFileExtension(File file) {
        String name = file.getName().toLowerCase();
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 0 && lastDot < name.length() - 1) {
            return name.substring(lastDot);
        }
        return ".png"; // fallback si aucune extension trouvée
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

package controller;

import dao.ArticleDAO;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.Article;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ArticlesAdminController {

    @FXML private TableView<Article> tableAchats;
    @FXML private TableColumn<Article, String> colArticle, colDescription;
    @FXML private TableColumn<Article, Number> colPrix, colQuantiteVrac, colStock, colPrixVrac;
    @FXML private TextField searchField;

    private final ArticleDAO articleDAO = new ArticleDAO();
    private FilteredList<Article> filteredArticles;
    private File selectedImage = null;

    @FXML
    public void initialize() {
        colArticle.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
        colDescription.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));
        colPrix.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrixUnitaire()));
        colPrixVrac.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrixVrac()));
        colQuantiteVrac.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getQuantiteVrac()));
        colStock.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getStock()));

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
        selectedImage = null;
        Article article = showArticleDialog(null);
        if (article != null) {
            boolean success = articleDAO.create(article);
            if (success) {
                int newId = articleDAO.findIdByNom(article.getNom());
                if (selectedImage != null) {
                    importImageForArticle(newId, selectedImage);
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
        Article selected = tableAchats.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aucun article sélectionné", "Veuillez sélectionner un article.");
            return;
        }

        selectedImage = null;
        Article modifie = showArticleDialog(selected);
        if (modifie != null) {
            modifie.setIdArticle(selected.getIdArticle());
            boolean success = articleDAO.update(modifie);
            if (success) {
                if (selectedImage != null) {
                    importImageForArticle(selected.getIdArticle(), selectedImage);
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

        TextField nomField = new TextField();
        TextField descField = new TextField();
        TextField prixField = new TextField();
        TextField prixVracField = new TextField();
        TextField vracField = new TextField();
        TextField stockField = new TextField();

        Button imageButton = new Button("Choisir une image");
        Label imageLabel = new Label("Aucune image sélectionnée");
        imageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir une image (PNG, JPG, JPEG)");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
            );
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                selectedImage = file;
                imageLabel.setText(file.getName());
            }
        });

        if (existing != null) {
            nomField.setText(existing.getNom());
            descField.setText(existing.getDescription());
            prixField.setText(String.valueOf(existing.getPrixUnitaire()));
            prixVracField.setText(String.valueOf(existing.getPrixVrac()));
            vracField.setText(String.valueOf(existing.getQuantiteVrac()));
            stockField.setText(String.valueOf(existing.getStock()));
        }

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.addRow(0, new Label("Nom :"), nomField);
        grid.addRow(1, new Label("Description :"), descField);
        grid.addRow(2, new Label("Prix unitaire :"), prixField);
        grid.addRow(3, new Label("Prix vrac :"), prixVracField);
        grid.addRow(4, new Label("Quantité vrac :"), vracField);
        grid.addRow(5, new Label("Stock :"), stockField);
        grid.addRow(6, imageButton, imageLabel);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    return new Article(0,
                            nomField.getText(),
                            descField.getText(),
                            Double.parseDouble(prixField.getText()),
                            Double.parseDouble(prixVracField.getText()),
                            Integer.parseInt(vracField.getText()),
                            Integer.parseInt(stockField.getText()));
                } catch (NumberFormatException e) {
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

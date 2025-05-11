package controller;

import dao.TypeDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Article;
import model.Type;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class TypesAdminController {

    @FXML private TableView<Type> tableTypes;
    @FXML private TableColumn<Type, String> colNomType;

    private final TypeDAO typeDAO = new TypeDAO();
    private ObservableList<Type> data;

    @FXML
    public void initialize() {

        colNomType.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getNomType()));

        loadData();
    }

    private void loadData() {
        data = FXCollections.observableArrayList(typeDAO.findAll());
        tableTypes.setItems(data);
    }

    @FXML
    private void handleAjouter() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un type");
        dialog.setHeaderText("Saisissez le nom du nouveau type d'article");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField nomField = new TextField();
        nomField.setPromptText("Nom du type");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Nom :"), nomField);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return nomField.getText().trim();
            }
            return null;
        });

        dialog.showAndWait()
                .filter(nom -> !nom.isBlank())
                .ifPresent(nom -> {
                    boolean success = typeDAO.create(new Type(0, nom));
                    if (success) {
                        loadData(); // recharge la liste des types
                    } else {
                        showAlert("Erreur", "L'ajout du type a échoué.");
                    }
                });
    }

    @FXML
    private void handleModifier() {
        Type sel = tableTypes.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert("Aucun type sélectionné", "Veuillez sélectionner un type.");
            return;
        }
        TextInputDialog dlg = new TextInputDialog(sel.getNomType());
        dlg.setTitle("Modifier le type");
        dlg.setHeaderText("Nouveau nom pour le type \"" + sel.getNomType() + "\"");
        dlg.setContentText("Type :");
        Optional<String> res = dlg.showAndWait();
        res.filter(s -> !s.isBlank()).ifPresent(name -> {
            sel.setNomType(name);
            if (typeDAO.update(sel)) {
                loadData();
            } else {
                showAlert("Erreur", "La modification a échoué.");            }
        });
    }



    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
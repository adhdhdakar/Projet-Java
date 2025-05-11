package controller;

import dao.TypeDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Type;

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
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Ajouter un type");
        dlg.setHeaderText("Nom du nouveau type");
        dlg.setContentText("Type :");
        Optional<String> res = dlg.showAndWait();
        res.filter(s -> !s.isBlank()).ifPresent(name -> {
            if (typeDAO.create(new Type(0, name))) {
                loadData();
            } else {
                showAlert("Erreur", "L'ajout a échoué.");
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
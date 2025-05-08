package controller;

import dao.AchatDAO;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Achat;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StatsAdminController {

    @FXML private TableView<Achat> tableStats;
    @FXML private TableColumn<Achat, String> colArticle;
    @FXML private TableColumn<Achat, Number> colTotalQuantite;
    @FXML private TextField searchField;

    private FilteredList<Achat> filteredData;

    @FXML
    public void initialize() {
        colArticle.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNomArticle()));
        colTotalQuantite.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getQuantite()));

        loadStats();

        searchField.textProperty().addListener((obs, oldVal, newValue) -> {
            filteredData.setPredicate(achat -> {
                if (newValue == null || newValue.isEmpty()) return true;
                return achat.getNomArticle().toLowerCase().contains(newValue.toLowerCase());
            });
        });
    }

    private void loadStats() {
        AchatDAO dao = new AchatDAO();
        List<Achat> raw = dao.findAll();

        // Agréger les quantités par article
        List<Achat> agreges = raw.stream()
                .collect(Collectors.groupingBy(Achat::getNomArticle, Collectors.summingInt(Achat::getQuantite)))
                .entrySet().stream()
                .map(e -> new Achat(e.getKey(), "", "", e.getValue(), "", 0, 0, 0))
                .sorted(Comparator.comparingInt(Achat::getQuantite).reversed())
                .collect(Collectors.toList());

        filteredData = new FilteredList<>(FXCollections.observableArrayList(agreges), p -> true);
        tableStats.setItems(filteredData);
    }

}

package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainPageController {

    @FXML private ImageView tshirt1;
    @FXML private ImageView tshirt2;
    @FXML private ImageView chaussuresImage;

    @FXML
    private void initialize() {
        // Attention : on charge depuis /images/ puisque c'est le dossier OUT
        // qui s'appelle images et qui contient tshirt1.png, tshirt2.png, chaussure.png
        System.out.println("→ img1: " + getClass().getResource("/images/tshirt1.png"));
        System.out.println("→ img2: " + getClass().getResource("/images/tshirt2.png"));
        System.out.println("→ img3: " + getClass().getResource("/images/chaussure.png"));

        tshirt1.setImage(new Image(
                getClass().getResource("/images/tshirt1.png").toExternalForm()
        ));
        tshirt2.setImage(new Image(
                getClass().getResource("/images/tshirt2.png").toExternalForm()
        ));
        chaussuresImage.setImage(new Image(
                getClass().getResource("/images/chaussure.png").toExternalForm()
        ));
    }

    @FXML private void ajouterTshirt()   { showAlert("T-shirt ajouté !"); }
    @FXML private void ajouterPantalon() { showAlert("Pantalon ajouté !"); }
    @FXML private void ajouterChaussures() { showAlert("Chaussures ajoutées !"); }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Panier");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
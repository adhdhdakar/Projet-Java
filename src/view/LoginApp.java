package view;

import dao.ClientDAO;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Client;

public class LoginApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Connexion Client");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        // Champs
        Label emailLabel = new Label("Email :");
        TextField emailField = new TextField();

        Label mdpLabel = new Label("Mot de passe :");
        PasswordField mdpField = new PasswordField();

        Button loginButton = new Button("Se connecter");
        Label resultLabel = new Label();

        // Placement
        grid.add(emailLabel, 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(mdpLabel, 0, 1);
        grid.add(mdpField, 1, 1);
        grid.add(loginButton, 1, 2);
        grid.add(resultLabel, 1, 3);

        // Action du bouton
        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String mdp = mdpField.getText();

            ClientDAO dao = new ClientDAO();
            Client client = dao.findByEmailAndPassword(email, mdp);

            if (client != null) {
                primaryStage.close(); // ferme la fenêtre Login
                try {
                    new CatalogueApp().start(new Stage()); // ouvre la fenêtre Catalogue
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                resultLabel.setText("Identifiants incorrects.");
            }

        });

        // Affichage
        Scene scene = new Scene(grid, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

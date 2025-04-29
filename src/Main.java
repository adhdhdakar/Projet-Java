import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger le fichier FXML principal (AdminController.fxml)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin.fxml"));
        AnchorPane root = loader.load();

        // Initialiser la scène
        Scene scene = new Scene(root);

        // Définir la scène et le titre
        primaryStage.setTitle("Panneau d'Administration");
        primaryStage.setScene(scene);

        // Afficher la fenêtre
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // Lance l'application
    }
}
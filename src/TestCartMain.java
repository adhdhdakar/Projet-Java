import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Client;
import model.Session;

public class TestCartMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. Simuler un client connecté (à adapter selon votre base)
        //    ici, on prend l'idClient = 1
        Client fakeClient = new Client(
                2,                // idClient existant en base
                "Dupont",         // nom
                "Marie",          // prénom
                "marie@exemple.fr", // email
                "azerty",         // motDePasse
                "standard"        // typeClient
        );
        Session.getInstance().setClient(fakeClient);

        // 2. Charger la vue du panier
        Parent root = FXMLLoader.load(
                getClass().getResource("/view/Cart.fxml")
        );

        // 3. Configurer et afficher la fenêtre
        primaryStage.setTitle("Test : Mon Panier");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
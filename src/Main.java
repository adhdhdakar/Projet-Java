import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println(">> FXML lookup: " + getClass().getResource("/MainPage.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("/MainPage.fxml"));
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setTitle("Boutique");
        primaryStage.show();
    }
    public static void main(String[] args) { launch(args); }
}
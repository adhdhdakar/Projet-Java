package controller;
import javafx.event.ActionEvent; // À ne pas oublier !
import javafx.fxml.FXML;
import javafx.scene.control.*;
import dao.ClientDAO;
import model.Client;


public class SignUpController {

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField mdpField;

    @FXML
    private Button signUpButton;

    @FXML
    private void handleSignUp(ActionEvent event) {
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String mdp = mdpField.getText();

        if (prenom.isEmpty() || email.isEmpty() || mdp.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        Client client = new Client(0, prenom, "", email, mdp, "nouveau");
        ClientDAO dao = new ClientDAO();
        boolean success = dao.create(client);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Inscription réussie !");
            clearFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Email déjà utilisé ?");
        }
    }

    private void clearFields() {
        prenomField.clear();
        emailField.clear();
        mdpField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
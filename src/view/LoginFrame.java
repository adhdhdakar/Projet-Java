package view;

import dao.ClientDAO;
import model.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel resultLabel;

    public LoginFrame() {
        setTitle("Connexion Client");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centre la fenêtre

        // Création des composants
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Se connecter");
        resultLabel = new JLabel("");

        // Layout
        setLayout(new GridLayout(4, 2));

        add(new JLabel("Email :"));
        add(emailField);
        add(new JLabel("Mot de passe :"));
        add(passwordField);
        add(new JLabel(""));
        add(loginButton);
        add(resultLabel);

        // Action bouton
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String mdp = new String(passwordField.getPassword());

                ClientDAO dao = new ClientDAO();
                Client client = dao.findByEmailAndPassword(email, mdp);

                if (client != null) {
                    resultLabel.setText("Bienvenue " + client.getPrenom() + " !");
                } else {
                    resultLabel.setText("Identifiants incorrects.");
                }
            }
        });

        setVisible(true);
    }
}

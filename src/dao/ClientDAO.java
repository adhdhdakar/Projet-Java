package dao;

import model.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    // Récupère tous les clients
    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM Client";

        try (Connection conn = Connexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Client c = new Client(
                        rs.getInt("idClient"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("motDePasse"),
                        rs.getString("typeClient"),
                        rs.getString("code_cb")
                );
                clients.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clients;
    }

    // Récupère un client par email + mot de passe
    public Client findByEmailAndPassword(String email, String motDePasse) {
        String sql = "SELECT * FROM Client WHERE email = ? AND motDePasse = ?";

        try (Connection conn = Connexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, motDePasse);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Client(
                        rs.getInt("idClient"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("motDePasse"),
                        rs.getString("typeClient"),
                        rs.getString("code_cb")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    public boolean create(Client client) {
        String sql = "INSERT INTO Client (nom, prenom, email, motDePasse, typeClient, code_cb)"
                + " VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Connexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setString(3, client.getEmail());
            stmt.setString(4, client.getMotDePasse());
            stmt.setString(5, client.getTypeClient());
            stmt.setString(6, client.getCodeCb());;

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void update(Client client) throws SQLException {
        String sql = "UPDATE Client SET nom = ?, prenom = ?, email = ?, motDePasse = ?, code_cb = ?"
                + " WHERE idClient = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setString(3, client.getEmail());
            stmt.setString(4, client.getMotDePasse());
            stmt.setString(5, client.getCodeCb());
            stmt.setInt(6, client.getIdClient());

            stmt.executeUpdate();
        }
    }

    public boolean updateClientAdmin(Client client) {
        String sql = "UPDATE Client SET nom = ?, prenom = ?, email = ?, typeClient = ? WHERE idClient = ?";

        try (Connection conn = Connexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setString(3, client.getEmail());
            stmt.setString(4, client.getTypeClient());
            stmt.setInt(5, client.getIdClient());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int idClient) {
        String sql = "DELETE FROM Client WHERE idClient = ?";

        try (Connection conn = Connexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idClient);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

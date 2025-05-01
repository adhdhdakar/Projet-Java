package dao;

import java.sql.*;
import java.time.LocalDate;

public class CommandeDAO {

    /**
     * Crée une nouvelle commande avec l'état "validee"
     */
    public int create(LocalDate date, int idClient) throws SQLException {
        String sql = "INSERT INTO Commande (dateCommande, idClient, etat) VALUES (?, ?, 'validee')";

        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(date));
            ps.setInt(2, idClient);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Erreur lors de la récupération de l'ID de la commande.");
            }
        }
    }

    /**
     * Crée une commande panier si aucune n'existe déjà
     */
    public int getOrCreatePanierCommande(int idClient) throws SQLException {
        String select = "SELECT idCommande FROM Commande WHERE idClient = ? AND etat = 'panier'";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(select)) {

            ps.setInt(1, idClient);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("idCommande");
            }
        }

        // Si aucune commande "panier" n'existe, on la crée
        String insert = "INSERT INTO Commande (dateCommande, idClient, etat) VALUES (?, ?, 'panier')";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setInt(2, idClient);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Impossible de créer une commande panier.");
            }
        }
    }

    //Valide une commande panier (change son état)

    public void validerCommande(int idCommande) throws SQLException {
        String sql = "UPDATE Commande SET etat = 'validee' WHERE idCommande = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCommande);
            ps.executeUpdate();
        }
    }
}

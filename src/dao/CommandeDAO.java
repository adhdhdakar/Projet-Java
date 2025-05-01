package dao;

import java.sql.*;
import java.time.LocalDate;
import model.Commande;

public class CommandeDAO {

    /**
     * Insère une nouvelle commande et renvoie l'ID généré.
     * @param dateCommande la date de la commande
     * @param idClient     l'ID du client
     * @return l'ID de la commande créée
     * @throws SQLException
     */
    public int create(LocalDate dateCommande, int idClient) throws SQLException {
        String sql = "INSERT INTO Commande (dateCommande, idClient) VALUES (?, ?)";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(dateCommande));
            ps.setInt(2, idClient);
            ps.executeUpdate();

            // Récupère la clé générée (idCommande)
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Échec de la récupération de l'ID de la commande.");
                }
            }
        }
    }

    // import model.Commande; // ta classe métier Commande doit inclure un champ String statut

    public Commande findByClientAndStatus(int idClient, String statut) throws SQLException {
        String sql = "SELECT idCommande, dateCommande, idClient, statut "
                + "FROM Commande WHERE idClient = ? AND statut = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idClient);
            ps.setString(2, statut);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Commande(
                            rs.getInt("idCommande"),
                            rs.getDate("dateCommande").toLocalDate(),
                            rs.getInt("idClient"),
                            rs.getString("statut")
                    );
                }
            }
        }
        return null;
    }

    public void updateStatut(int idCommande, String nouveauStatut) throws SQLException {
        String sql = "UPDATE Commande SET statut = ? WHERE idCommande = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nouveauStatut);
            ps.setInt(2, idCommande);
            ps.executeUpdate();
        }
    }
}
package dao;

import java.sql.*;
import java.time.LocalDate;

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
}
package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.Commande;

public class CommandeDAO {

    /**
     * Crée une nouvelle commande avec l'état "VA"
     */
    public int create(LocalDate date, int idClient) throws SQLException {
        String sql = "INSERT INTO Commande (dateCommande, idClient, statut) VALUES (?, ?, 'VA')";

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
        String select = "SELECT idCommande FROM Commande WHERE idClient = ? AND statut = 'EC'";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(select)) {

            ps.setInt(1, idClient);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("idCommande");
            }
        }

        // Si aucune commande "panier" n'existe, on la crée
        String insert = "INSERT INTO Commande (dateCommande, idClient, statut) VALUES (?, ?, 'EC')";
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
        String sql = "UPDATE Commande SET statut = 'VA' WHERE idCommande = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCommande);
            ps.executeUpdate();
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

    public List<Commande> findValideByClient(int idClient) throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT c.idCommande, c.dateCommande, c.idClient, c.statut, " +
                "IFNULL(SUM(a.prixUnitaire * lc.quantite), 0) AS total " +
                "FROM Commande c " +
                "LEFT JOIN LigneCommande lc ON c.idCommande = lc.idCommande " +
                "LEFT JOIN Article a ON lc.idArticle = a.idArticle " +
                "WHERE c.idClient = ? AND c.statut = 'VA' " +
                "GROUP BY c.idCommande";

        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idClient);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Commande c = new Commande(rs.getInt("idCommande"),
                        rs.getDate("dateCommande").toLocalDate(),
                        rs.getInt("idClient"),
                        rs.getString("statut"),
                        rs.getDouble("total")
                );
                commandes.add(c);
            }
        }
        return commandes;
    }


}
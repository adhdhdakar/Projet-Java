package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.Commande;

public class CommandeDAO {

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

    // Crée une commande panier si aucune n'existe déjà
    public int getOrCreatePanierCommande(int idClient) throws SQLException {
        String select = "SELECT idCommande FROM Commande WHERE idClient = ? AND statut = 'PANIER'";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setInt(1, idClient);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int idCommande = rs.getInt("idCommande");
                System.out.println("Panier EXISTANT : idCommande = " + idCommande);
                return idCommande;
            }
        }

        // Sinon, créer une nouvelle commande
        String insert = "INSERT INTO Commande (dateCommande, idClient, statut, etat) VALUES (?, ?, 'PANIER', 'PANIER')";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setInt(2, idClient);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int idCommande = rs.getInt(1);
                System.out.println("NOUVEAU panier créé : idCommande = " + idCommande);
                return idCommande;
            } else {
                throw new SQLException("Échec de la création du panier.");
            }
        }
    }


    private final LigneCommandeDAO ligneDao = new LigneCommandeDAO();

    public void validerCommande(int idCommande) throws SQLException {
        String sql = "UPDATE Commande SET statut = 'VALIDEE' WHERE idCommande = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCommande);
            ps.executeUpdate();
            System.out.println("Commande validée : idCommande = " + idCommande);
        }
    }

    public Commande findByClientAndStatus(int idClient, String statut) throws SQLException {
        String sql = "SELECT idCommande, dateCommande, idClient, statut FROM Commande WHERE idClient = ? AND statut = ?";
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
                            rs.getString("statut"),
                            0.0  // valeur temporaire car cette méthode ne calcule pas le total
                    );
                }
            }
        }
        return null;
    }

    public void updateStatut(int idCommande, String newStatut) throws SQLException {
        String sql = "UPDATE Commande SET statut = ? WHERE idCommande = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatut);
            ps.setInt(2, idCommande);
            ps.executeUpdate();
        }
    }


    public List<Commande> findValideByClient(int idClient) throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT c.idCommande, c.dateCommande, c.idClient, c.statut, " +
                "SUM(a.prixUnitaire * lc.quantite) AS total " +
                "FROM Commande c " +
                "JOIN LigneCommande lc ON c.idCommande = lc.idCommande " +
                "JOIN Article a ON lc.idArticle = a.idArticle " +
                "WHERE c.idClient = ? AND c.statut = 'VALIDEE' " +
                "GROUP BY c.idCommande";

        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idClient);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                commandes.add(new Commande(
                        rs.getInt("idCommande"),
                        rs.getDate("dateCommande").toLocalDate(),
                        rs.getInt("idClient"),
                        rs.getString("statut"),
                        rs.getDouble("total")
                ));
            }
        }

        return commandes;
    }




}
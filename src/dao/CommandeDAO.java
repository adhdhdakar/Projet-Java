package dao;

import java.sql.*;
import java.time.LocalDate;
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

    public void updateStatut(int idCommande, String newStatut) throws SQLException {
        String sql = "UPDATE Commande SET statut = ? WHERE idCommande = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatut);
            ps.setInt(2, idCommande);
            ps.executeUpdate();
        }
    }




}
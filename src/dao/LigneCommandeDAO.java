package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.LigneCommande;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LigneCommandeDAO {

    /**
     * Récupère toutes les lignes de la commande donnée.
     */
    public List<LigneCommande> findByCommande(int idCommande) throws SQLException {
        String sql = "SELECT idLigne, idCommande, idArticle, quantite "
                + "FROM LigneCommande WHERE idCommande = ?";
        List<LigneCommande> list = new ArrayList<>();
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCommande);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new LigneCommande(
                            rs.getInt("idLigne"),
                            rs.getInt("idCommande"),
                            rs.getInt("idArticle"),
                            rs.getInt("quantite")
                    ));
                }
            }
        }
        return list;
    }

    /**
     * Insère une ligne de commande.
     */
    public void create(int idCommande, int idArticle, int quantite) throws SQLException {
        String sql = "INSERT INTO LigneCommande (idCommande, idArticle, quantite) VALUES (?, ?, ?)";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCommande);
            ps.setInt(2, idArticle);
            ps.setInt(3, quantite);
            ps.executeUpdate();
        }
    }

    public void createOrIncrement(int idCommande, int idArticle, int quantite) throws SQLException {
        String selectSql = "SELECT quantite FROM LigneCommande WHERE idCommande = ? AND idArticle = ?";
        String updateSql = "UPDATE LigneCommande SET quantite = quantite + ? WHERE idCommande = ? AND idArticle = ?";
        String insertSql = "INSERT INTO LigneCommande (idCommande, idArticle, quantite) VALUES (?, ?, ?)";

        try (Connection conn = Connexion.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {

            selectStmt.setInt(1, idCommande);
            selectStmt.setInt(2, idArticle);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, quantite);
                    updateStmt.setInt(2, idCommande);
                    updateStmt.setInt(3, idArticle);
                    updateStmt.executeUpdate();
                }
            } else {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, idCommande);
                    insertStmt.setInt(2, idArticle);
                    insertStmt.setInt(3, quantite);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    // Met à jour la quantité exacte (remplace l’ancienne)
    public void updateQuantite(int idCommande, int idArticle, int quantite) throws SQLException {
        String sql = "UPDATE LigneCommande SET quantite = ? WHERE idCommande = ? AND idArticle = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantite);
            ps.setInt(2, idCommande);
            ps.setInt(3, idArticle);
            ps.executeUpdate();
        }
    }

    // Supprime la ligne de commande
    public void delete(int idCommande, int idArticle) throws SQLException {
        String sql = "DELETE FROM LigneCommande WHERE idCommande = ? AND idArticle = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCommande);
            ps.setInt(2, idArticle);
            ps.executeUpdate();
        }
    }


}
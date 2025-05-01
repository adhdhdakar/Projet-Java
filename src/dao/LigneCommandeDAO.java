package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LigneCommandeDAO {

    /**
     * Insère une ligne de commande pour un article donné.
     * @param idCommande l'ID de la commande parente
     * @param idArticle  l'ID de l'article
     * @param quantite   la quantité commandée
     * @throws SQLException
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


}
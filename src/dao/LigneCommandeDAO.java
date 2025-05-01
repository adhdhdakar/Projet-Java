package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
}
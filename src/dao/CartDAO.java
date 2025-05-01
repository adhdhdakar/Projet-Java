package dao;

import model.Article;
import model.ArticleInCart;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    // Récupère tous les ArticleInCart pour un client donné en joignant Commande ⇢ LigneCommande ⇢ Article.

    public List<ArticleInCart> findByClient(int idClient) {
        List<ArticleInCart> items = new ArrayList<>();
        String sql =
                "SELECT a.idArticle, a.nom, a.description, a.prixUnitaire, " +
                        "       a.prixVrac, a.quantiteVrac, a.stock, " +
                        "       lc.quantite " +
                        "FROM Commande c " +
                        "  JOIN LigneCommande lc ON c.idCommande = lc.idCommande " +
                        "  JOIN Article a      ON a.idArticle  = lc.idArticle " +
                        "WHERE c.idClient = ? AND c.statut = 'PANIER'\n";

        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idClient);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Article a = new Article(
                        rs.getInt("idArticle"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prixUnitaire"),
                        rs.getDouble("prixVrac"),
                        rs.getInt("quantiteVrac"),
                        rs.getInt("stock")
                );
                int quantite = rs.getInt("quantite");
                items.add(new ArticleInCart(a, quantite));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }


    public void clearCart(int idClient) throws SQLException {
        String sql =
                "DELETE lc " +
                        "FROM LigneCommande lc " +
                        "  JOIN Commande c ON lc.idCommande = c.idCommande " +
                        "WHERE c.idClient = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idClient);
            ps.executeUpdate();
        }
    }
}
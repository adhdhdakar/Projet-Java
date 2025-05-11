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
                "SELECT a.idArticle,\n" +
                        "       a.nom,\n" +
                        "       a.description,\n" +
                        "       a.prixUnitaire,\n" +
                        "       a.prixVrac,\n" +
                        "       a.quantiteVrac,\n" +
                        "       a.stock,\n" +
                        "       t.nomType   AS typeName,\n" +
                        "       lc.quantite AS quantite\n" +
                        "  FROM Commande c\n" +
                        "  JOIN LigneCommande lc ON c.idCommande = lc.idCommande\n" +
                        "  JOIN Article       a  ON a.idArticle  = lc.idArticle\n" +
                        "  LEFT JOIN ArticleType at ON a.idArticle = at.idArticle\n" +
                        "  LEFT JOIN Type        t  ON at.idType    = t.idType\n" +
                        " WHERE c.idClient = ?\n" +
                        "   AND c.statut   = 'EC'";

        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idClient);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Article a = new Article(
                            rs.getInt("idArticle"),
                            rs.getString("nom"),
                            rs.getString("description"),
                            rs.getDouble("prixUnitaire"),
                            rs.getDouble("prixVrac"),
                            rs.getInt("quantiteVrac"),
                            rs.getInt("stock"),
                            rs.getString("typeName")    // on passe le type ici
                    );
                    int quantite = rs.getInt("quantite");
                    items.add(new ArticleInCart(a, quantite));
                }
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
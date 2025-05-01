package dao;

import model.Article;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleDAO {

    /**
     * Récupère tous les articles.
     */
    public List<Article> findAll() {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT idArticle, nom, description, prixUnitaire, prixVrac, quantiteVrac, stock "
                + "FROM Article";

        try (Connection conn = Connexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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
                articles.add(a);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return articles;
    }

    /**
     * Met à jour le stock d’un article.
     */
    public void updateStock(int idArticle, int nouveauStock) throws SQLException {
        String sql = "UPDATE Article SET stock = ? WHERE idArticle = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nouveauStock);
            ps.setInt(2, idArticle);
            ps.executeUpdate();
        }
    }

    /**
     * Récupère un article par son ID (avec tous les champs nécessaires).
     */
    public Article findById(int idArticle) throws SQLException {
        String sql = "SELECT idArticle, nom, description, prixUnitaire, prixVrac, quantiteVrac, stock "
                + "FROM Article WHERE idArticle = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idArticle);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Article(
                            rs.getInt("idArticle"),
                            rs.getString("nom"),
                            rs.getString("description"),
                            rs.getDouble("prixUnitaire"),
                            rs.getDouble("prixVrac"),
                            rs.getInt("quantiteVrac"),
                            rs.getInt("stock")
                    );
                }
            }
        }
        return null;
    }
}
package dao;

import model.Article;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleDAO {

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



    public int getOrCreatePanierCommande(int idClient) {
        try (Connection conn = Connexion.getConnection()) {
            String select = "SELECT idCommande FROM Commande WHERE idClient = ? AND statut = 'EC' LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(select);
            ps.setInt(1, idClient);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

            String insert = "INSERT INTO Commande (dateCommande, idClient, statut) VALUES (NOW(), ?, 'EC')";
            PreparedStatement psInsert = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            psInsert.setInt(1, idClient);
            psInsert.executeUpdate();
            ResultSet keys = psInsert.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
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

    public void decrementStock(int idArticle, int quantite) throws SQLException {
        String sql = "UPDATE Article "
                + "SET stock = stock - ? "
                + "WHERE idArticle = ? AND stock >= ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantite);
            ps.setInt(2, idArticle);
            ps.setInt(3, quantite);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("Stock insuffisant pour l'article " + idArticle);
            }
        }
    }

    public int getStock(int idArticle) throws SQLException {
        String sql = "SELECT stock FROM Article WHERE idArticle = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idArticle);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("stock");
                else throw new SQLException("Article introuvable");
            }
        }
    }

    public void incrementStock(int idArticle, int quantite) throws SQLException {
        String sql = "UPDATE Article SET stock = stock + ? WHERE idArticle = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantite);
            ps.setInt(2, idArticle);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("Article introuvable ou pas de mise à jour pour idArticle=" + idArticle);
            }
        }
    }
}

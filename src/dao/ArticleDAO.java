package dao;

import model.Article;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleDAO {

    public List<Article> findAll() {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT * FROM Article";

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

    public void updateStock(int idArticle, int nouveauStock) throws SQLException {
        String sql = "UPDATE Article SET stock = ? WHERE idArticle = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nouveauStock);
            ps.setInt(2, idArticle);
            ps.executeUpdate();
        }
    }

    public int getOrCreatePanierCommande(int idClient) {
        try (Connection conn = Connexion.getConnection()) {
            String select = "SELECT idCommande FROM Commande WHERE idClient = ? AND statut = 'PANIER' LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(select);
            ps.setInt(1, idClient);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

            String insert = "INSERT INTO Commande (dateCommande, idClient, statut) VALUES (NOW(), ?, 'PANIER')";
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

}

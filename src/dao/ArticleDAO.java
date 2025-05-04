package dao;

import model.Article;

import java.sql.*;
import java.time.LocalDate;
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

    public void updateStock(int idArticle, int nouveauStock) throws SQLException {
        String sql = "UPDATE Article SET stock = ? WHERE idArticle = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nouveauStock);
            ps.setInt(2, idArticle);
            ps.executeUpdate();
        }
    }

    public int getOrCreatePanierCommande(int idClient) throws SQLException {
        String select = "SELECT idCommande FROM Commande WHERE idClient = ? AND statut = 'PANIER'";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setInt(1, idClient);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int idCommande = rs.getInt("idCommande");
                System.out.println("Panier existant : idCommande = " + idCommande);
                return idCommande;
            }
        }

        String insert = "INSERT INTO Commande (dateCommande, idClient, statut) VALUES (?, ?, 'PANIER')";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setInt(2, idClient);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int idCommande = rs.getInt(1);
                System.out.println("Nouveau panier créé : idCommande = " + idCommande);
                return idCommande;
            } else {
                throw new SQLException("Erreur création panier.");
            }
        }
    }




    //Récupère un article par son ID

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

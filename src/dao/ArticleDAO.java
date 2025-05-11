package dao;

import model.Article;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ArticleDAO {

    public List<Article> findAll() {
        List<Article> articles = new ArrayList<>();
        String sql =
                "SELECT a.idArticle,\n" +
                        "       a.nom,\n" +
                        "       a.description,\n" +
                        "       a.prixUnitaire,\n" +
                        "       a.prixVrac,\n" +
                        "       a.quantiteVrac,\n" +
                        "       a.stock,\n" +
                        "       t.nomType AS typeName\n" +
                        "  FROM Article a\n" +
                        "  LEFT JOIN ArticleType at ON a.idArticle = at.idArticle\n" +
                        "  LEFT JOIN Type        t  ON at.idType    = t.idType\n" +
                        " ORDER BY a.nom";

        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Article a = new Article(
                        rs.getInt("idArticle"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prixUnitaire"),
                        rs.getDouble("prixVrac"),
                        rs.getInt("quantiteVrac"),
                        rs.getInt("stock"),
                        rs.getString("typeName")
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
            String select = "SELECT idCommande FROM Commande WHERE idClient = ? AND statut = 'EC' LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(select)) {
                ps.setInt(1, idClient);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) return rs.getInt(1);
            }

            // Crée la commande que si on ajt un article
            String insert = "INSERT INTO Commande (dateCommande, idClient, statut) VALUES (NOW(), ?, 'EC')";
            try (PreparedStatement psInsert = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                psInsert.setInt(1, idClient);
                psInsert.executeUpdate();
                ResultSet keys = psInsert.getGeneratedKeys();
                if (keys.next()) return keys.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * Récupère un article par son ID (avec tous les champs nécessaires).
     */
    public Article findById(int idArticle) throws SQLException {
        String sql = """
        SELECT a.idArticle,
               a.nom,
               a.description,
               a.prixUnitaire,
               a.prixVrac,
               a.quantiteVrac,
               a.stock,
               t.nomType AS typeName
          FROM Article a
          LEFT JOIN ArticleType at ON a.idArticle = at.idArticle
          LEFT JOIN Type        t  ON at.idType    = t.idType
         WHERE a.idArticle = ?
        """;
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
                            rs.getInt("stock"),
                            rs.getString("typeName")    // constructeur étendu
                    );
                }
            }
        }
        return null;
    }
    public List<String> findAllArticleNoms() {
        List<String> noms = new ArrayList<>();
        String sql = "SELECT nom FROM Article";

        try (Connection conn = Connexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                noms.add(rs.getString("nom"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return noms;
    }

    public int findIdByNom(String nomArticle) {
        String sql = "SELECT idArticle FROM Article WHERE nom = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomArticle);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idArticle");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean create(Article article) {
        String sql = "INSERT INTO Article (nom, description, prixUnitaire, prixVrac, quantiteVrac, stock) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, article.getNom());
            stmt.setString(2, article.getDescription());
            stmt.setDouble(3, article.getPrixUnitaire());
            stmt.setDouble(4, article.getPrixVrac());
            stmt.setInt(5, article.getQuantiteVrac());
            stmt.setInt(6, article.getStock());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Article article) {
        String sql = "UPDATE Article SET nom = ?, description = ?, prixUnitaire = ?, prixVrac = ?, quantiteVrac = ?, stock = ? WHERE idArticle = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, article.getNom());
            stmt.setString(2, article.getDescription());
            stmt.setDouble(3, article.getPrixUnitaire());
            stmt.setDouble(4, article.getPrixVrac());
            stmt.setInt(5, article.getQuantiteVrac());
            stmt.setInt(6, article.getStock());
            stmt.setInt(7, article.getIdArticle());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int idArticle) {
        String sql = "DELETE FROM Article WHERE idArticle = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idArticle);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteArticleType(int idArticle) {
        String sql = "DELETE FROM ArticleType WHERE idArticle = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idArticle);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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

    public List<String> findAllTypes() {
        List<String> types = new ArrayList<>();
        String sql = "SELECT nomType FROM Type ORDER BY nomType";
        try (Connection c = Connexion.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                types.add(rs.getString("nomType"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }

    public int findTypeIdByName(String nomType) {
        String sql = "SELECT idType FROM Type WHERE nomType = ?";
        try (Connection c = Connexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nomType);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("idType");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean createArticleType(int idArticle, int idType) {
        String sql = "INSERT INTO ArticleType (idArticle, idType) VALUES (?, ?)";
        try (Connection c = Connexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idArticle);
            ps.setInt(2, idType);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Article> findByType(String nomType) {
        List<Article> articles = new ArrayList<>();
        String sql = """
        SELECT a.idArticle,
               a.nom,
               a.description,
               a.prixUnitaire,
               a.prixVrac,
               a.quantiteVrac,
               a.stock,
               t.nomType AS typeName
          FROM Article a
          JOIN ArticleType at ON a.idArticle = at.idArticle
          JOIN Type        t  ON at.idType    = t.idType
         WHERE t.nomType = ?
         ORDER BY a.nom
        """;
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nomType);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    articles.add(new Article(
                            rs.getInt("idArticle"),
                            rs.getString("nom"),
                            rs.getString("description"),
                            rs.getDouble("prixUnitaire"),
                            rs.getDouble("prixVrac"),
                            rs.getInt("quantiteVrac"),
                            rs.getInt("stock"),
                            rs.getString("typeName")  // on récupère ici le nom du type
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articles;
    }

}

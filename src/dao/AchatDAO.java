package dao;

import model.Achat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AchatDAO {

    public List<Achat> findAll() {
        List<Achat> achats = new ArrayList<>();
        String sql = """
            SELECT a.nom AS nom_article, 
                   c.prenom AS prenom_client,
                   c.nom AS nom_client,
                   lc.quantite,
                   com.dateCommande,
                   lc.idCommande,
                   lc.idArticle,
                   lc.idLigne
            FROM LigneCommande lc
            JOIN Commande com ON lc.idCommande = com.idCommande
            JOIN Client c ON com.idClient = c.idClient
            JOIN Article a ON lc.idArticle = a.idArticle
            """;

        try (Connection conn = Connexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Achat achat = new Achat(
                        rs.getString("nom_article"),
                        rs.getString("prenom_client"),
                        rs.getString("nom_client"),
                        rs.getInt("quantite"),
                        rs.getDate("dateCommande").toString(),
                        rs.getInt("idCommande"),
                        rs.getInt("idArticle"),
                        rs.getInt("idLigne")
                );
                achats.add(achat);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return achats;
    }

    public boolean delete(int idCommande, int idArticle) {
        String sql = "DELETE FROM LigneCommande WHERE idCommande = ? AND idArticle = ?";

        try (Connection conn = Connexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCommande);
            stmt.setInt(2, idArticle);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Achat achat) {
        String sql = """
        UPDATE LigneCommande
        SET quantite = ?, idArticle = ?
        WHERE idLigne = ?
        """;

        try (Connection conn = Connexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, achat.getQuantite());
            stmt.setInt(2, achat.getIdArticle());
            stmt.setInt(3, achat.getIdLigne());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
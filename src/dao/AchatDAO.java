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
                   CONCAT(c.prenom, ' ', c.nom) AS nom_client,
                   lc.quantite,
                   com.dateCommande,
                   lc.idCommande
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
                        rs.getString("nom_client"),
                        rs.getInt("quantite"),
                        rs.getDate("dateCommande").toString(),
                        rs.getInt("idCommande")
                );
                achats.add(achat);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return achats;
    }
}
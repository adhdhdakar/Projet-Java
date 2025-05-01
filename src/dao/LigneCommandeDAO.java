package dao;

import model.LigneCommande;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LigneCommandeDAO {

    /**
     * Récupère toutes les lignes de la commande donnée.
     */
    public List<LigneCommande> findByCommande(int idCommande) throws SQLException {
        String sql = "SELECT idLigne, idCommande, idArticle, quantite "
                + "FROM LigneCommande WHERE idCommande = ?";
        List<LigneCommande> list = new ArrayList<>();
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCommande);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new LigneCommande(
                            rs.getInt("idLigne"),
                            rs.getInt("idCommande"),
                            rs.getInt("idArticle"),
                            rs.getInt("quantite")
                    ));
                }
            }
        }
        return list;
    }

    /**
     * Insère une ligne de commande.
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
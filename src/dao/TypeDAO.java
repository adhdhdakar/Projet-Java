package dao;

import model.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeDAO {

    /** Récupère tous les types */
    public List<Type> findAll() {
        List<Type> types = new ArrayList<>();
        String sql = "SELECT idType, nomType FROM Type ORDER BY nomType";
        try (Connection conn = Connexion.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                types.add(new Type(
                        rs.getInt("idType"),
                        rs.getString("nomType")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }

    /** Crée un nouveau type */
    public boolean create(Type t) {
        String sql = "INSERT INTO Type (nomType) VALUES (?)";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getNomType());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Met à jour un type existant */
    public boolean update(Type t) {
        String sql = "UPDATE Type SET nomType = ? WHERE idType = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getNomType());
            ps.setInt(2, t.getIdType());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Supprime un type */
    public boolean delete(int idType) {
        String sql = "DELETE FROM Type WHERE idType = ?";
        try (Connection conn = Connexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idType);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
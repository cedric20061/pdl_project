package dao;

import java.sql.*;
import java.util.ArrayList;

import model.Specialization;
import service.AppCache;

/**
 * Data Access Object pour la gestion des spécialisations (dominantes).
 * Gère les opérations CRUD (Create, Read, Update, Delete) pour la table SPECIALIZATION.
 * 
 * Responsabilités :
 * - Création de nouvelles spécialisations
 * - Modification des informations de spécialisation
 * - Suppression de spécialisations
 * - Récupération des spécialisations avec cache en mémoire
 * 
 * Le cache est automatiquement invalidé après chaque mutation (add, update, delete).
 * 
 * @author PDL Team
 * @version 2.0
 * @see Specialization
 * @see AppCache
 */
public class SpecializationDAO extends ConnectionDAO {

    /**
     * Constructeur par défaut.
     * Initialise la connexion à la base de données via le parent ConnectionDAO.
     */
    public SpecializationDAO() {
        super();
    }

    // ==========================
    // CREATE
    // ==========================
    /**
     * Crée une nouvelle spécialisation en base de données.
     * Récupère l'ID généré et le stocke dans l'objet spécialisation.
     * Invalide le cache après insertion.
     * 
     * @param s L'objet Specialization à insérer
     * @return 1 si l'insertion a réussi, 0 sinon
     */
    public int add(Specialization s) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "INSERT INTO SPECIALIZATION (name, description, acronym, handleBy, department_id) "
                       + "VALUES (?, ?, ?, ?, ?)";

            ps = con.prepareStatement(sql, new String[] { "specialization_id" });

            ps.setString(1, s.getName());
            ps.setString(2, s.getDescription());
            ps.setString(3, s.getAcronym());
            ps.setString(4, s.getHandleBy());
            ps.setInt(5, s.getDepartmentId());

            int returnValue = ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                s.setId(rs.getInt(1));
            }

            // Invalidate cache
            AppCache.getInstance().setSpecializations(null);

            return returnValue;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }
        return 0;
    }

    // ==========================
    // UPDATE
    // ==========================
    /**
     * Met à jour une spécialisation existante en base de données.
     * Invalide le cache après mise à jour.
     * 
     * @param s L'objet Specialization avec les nouvelles données
     * @return 1 si la mise à jour a réussi, 0 sinon
     */
    public int update(Specialization s) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "UPDATE SPECIALIZATION SET name = ?, description = ?, acronym = ?, "
                       + "handleBy = ?, department_id = ? WHERE specialization_id = ?";

            ps = con.prepareStatement(sql);

            ps.setString(1, s.getName());
            ps.setString(2, s.getDescription());
            ps.setString(3, s.getAcronym());
            ps.setString(4, s.getHandleBy());
            ps.setInt(5, s.getDepartmentId());
            ps.setInt(6, s.getId());

            int result = ps.executeUpdate();
            // Invalidate cache
            AppCache.getInstance().setSpecializations(null);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }
        return 0;
    }

    // ==========================
    // DELETE
    // ==========================
    /**
     * Supprime une spécialisation de la base de données par son ID.
     * Invalide le cache après suppression.
     * 
     * @param id L'identifiant unique de la spécialisation à supprimer
     * @return 1 si la suppression a réussi, 0 sinon
     */
    public int delete(int id) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "DELETE FROM SPECIALIZATION WHERE specialization_id = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            // Invalidate cache
            AppCache.getInstance().setSpecializations(null);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }
        return 0;
    }

    // ==========================
    // GET BY ID (avec JOIN)
    // ==========================
    /**
     * Récupère une spécialisation par son identifiant.
     * Utilise d'abord le cache en mémoire pour optimiser les performances.
     * 
     * @param id L'identifiant unique de la spécialisation
     * @return L'objet Specialization trouvé, ou null si aucune spécialisation ne correspond
     */
    public Specialization get(int id) {
        // Check cache first
        ArrayList<Specialization> cached = AppCache.getInstance().getSpecializations();
        if (cached != null) {
            return cached.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "SELECT s.*, d.name AS department_name " +
                         "FROM SPECIALIZATION s " +
                         "JOIN DEPARTMENT d ON s.department_id = d.department_id " +
                         "WHERE s.specialization_id = ?";

            ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            rs = ps.executeQuery();

            if (rs.next()) {
                return map(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }
        return null;
    }

    // ==========================
    // GET ALL (avec JOIN)
    // ==========================
    /**
     * Récupère toutes les spécialisations de la base de données.
     * Utilise le cache en mémoire pour éviter les requêtes répétées.
     * Les résultats sont ordonnés par specialization_id.
     * 
     * @return Liste de toutes les spécialisations, vide si aucune spécialisation n'existe
     */
    public ArrayList<Specialization> getList() {
        // Check cache first
        if (AppCache.getInstance().getSpecializations() != null) {
            return AppCache.getInstance().getSpecializations();
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        ArrayList<Specialization> list = new ArrayList<>();

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "SELECT s.*, d.name AS department_name " +
                         "FROM SPECIALIZATION s " +
                         "JOIN DEPARTMENT d ON s.department_id = d.department_id " +
                         "ORDER BY s.specialization_id";

            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(map(rs));
            }

            // Cache the result
            AppCache.getInstance().setSpecializations(list);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }

        return list;
    }

    // ==========================
    // MAPPING
    // ==========================
    private Specialization map(ResultSet rs) throws SQLException {
        return new Specialization(
            rs.getInt("specialization_id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getString("acronym"),
            rs.getString("handleBy"),
            rs.getString("department_name"),
            rs.getInt("department_id")
        );
    }
}
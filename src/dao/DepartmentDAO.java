package dao;

import java.sql.*;
import java.util.ArrayList;

import model.Department;
import service.AppCache;

/**
 * Data Access Object pour la gestion des départements.
 * Gère les opérations CRUD (Create, Read, Update, Delete) pour la table DEPARTMENT.
 * 
 * Responsabilités :
 * - Création de nouveaux départements
 * - Modification des informations de département
 * - Suppression de départements
 * - Récupération des départements avec cache en mémoire
 * 
 * Le cache est automatiquement invalidé après chaque mutation (add, update, delete).
 * 
 * @author PDL Team
 * @version 2.0
 * @see Department
 * @see AppCache
 */
public class DepartmentDAO extends ConnectionDAO {

    /**
     * Constructeur par défaut.
     * Initialise la connexion à la base de données via le parent ConnectionDAO.
     */
    public DepartmentDAO() {
        super();
    }

    // ==========================
    // CREATE
    // ==========================
    /**
     * Crée un nouveau département en base de données.
     * Récupère l'ID généré et le stocke dans l'objet département.
     * Invalide le cache après insertion.
     * 
     * @param department L'objet Department à insérer
     * @return 1 si l'insertion a réussi, 0 sinon
     */
    public int add(Department department) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "INSERT INTO DEPARTMENT (name, description, handleBy) VALUES (?, ?, ?)";
            ps = con.prepareStatement(sql, new String[] { "department_id" });

            ps.setString(1, department.getName());
            ps.setString(2, department.getDescription());
            ps.setString(3, department.getHandleBy());

            int returnValue = ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                department.setId(rs.getInt(1));
            }

            // Invalidate cache
            AppCache.getInstance().setDepartments(null);

            return returnValue;

        } catch (Exception e) {
            if (e.getMessage().contains("ORA-00001")) {
                System.out.println("Un département avec ce identifiant existe déjà !");
            } else {
                e.printStackTrace();
            }
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
     * Met à jour un département existant en base de données.
     * Invalide le cache après mise à jour.
     * 
     * @param department L'objet Department avec les nouvelles données
     * @return 1 si la mise à jour a réussi, 0 sinon
     */
    public int update(Department department) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "UPDATE DEPARTMENT SET name = ?, description = ?, handleBy = ? WHERE department_id = ?";
            ps = con.prepareStatement(sql);

            ps.setString(1, department.getName());
            ps.setString(2, department.getDescription());
            ps.setString(3, department.getHandleBy());
            ps.setInt(4, department.getId());

            int result = ps.executeUpdate();
            // Invalidate cache
            AppCache.getInstance().setDepartments(null);
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
     * Supprime un département de la base de données par son ID.
     * Invalide le cache après suppression.
     * 
     * @param id L'identifiant unique du département à supprimer
     * @return 1 si la suppression a réussi, 0 sinon
     */
    public int delete(int id) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "DELETE FROM DEPARTMENT WHERE department_id = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            // Invalidate cache
            AppCache.getInstance().setDepartments(null);
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
    // GET BY ID
    // ==========================
    /**
     * Récupère un département par son identifiant.
     * Utilise d'abord le cache en mémoire pour optimiser les performances.
     * 
     * @param id L'identifiant unique du département
     * @return L'objet Department trouvé, ou null si aucun département ne correspond
     */
    public Department get(int id) {
        // Check cache first
        ArrayList<Department> cached = AppCache.getInstance().getDepartments();
        if (cached != null) {
            return cached.stream().filter(d -> d.getId() == id).findFirst().orElse(null);
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "SELECT * FROM DEPARTMENT WHERE department_id = ?";
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
    // GET ALL
    // ==========================
    /**
     * Récupère tous les départements de la base de données.
     * Utilise le cache en mémoire pour éviter les requêtes répétées.
     * Les résultats sont ordonnés par department_id.
     * 
     * @return Liste de tous les départements, vide si aucun département n'existe
     */
    public ArrayList<Department> getList() {
        // Check cache first
        if (AppCache.getInstance().getDepartments() != null) {
            return AppCache.getInstance().getDepartments();
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        ArrayList<Department> list = new ArrayList<>();

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "SELECT * FROM DEPARTMENT ORDER BY department_id";
            ps = con.prepareStatement(sql);

            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(map(rs));
            }

            // Cache the result
            AppCache.getInstance().setDepartments(list);

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
    private Department map(ResultSet rs) throws SQLException {
        return new Department(
            rs.getInt("department_id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getString("handleBy")
        );
    }
}
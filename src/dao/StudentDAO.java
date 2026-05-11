package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Student;
import service.AppCache;

/**
 * Data Access Object pour la gestion des étudiants.
 * Gère les opérations CRUD (Create, Read, Update, Delete) pour la table STUDENT.
 * 
 * Responsabilités :
 * - Récupération des étudiants par ID ou liste complète
 * - Gestion du cache en mémoire des étudiants
 * - Mapping des données de base de données vers les objets Student
 * 
 * @author PDL Team
 * @version 2.0
 * @see Student
 * @see AppCache
 */
public class StudentDAO extends ConnectionDAO {
    
    /**
     * Constructeur par défaut.
     * Initialise la connexion à la base de données via le parent ConnectionDAO.
     */
    public StudentDAO(){
        super();
    }
    // ==========================
    // GET BY ID (avec JOIN)
    // ==========================
    /**
     * Récupère un étudiant par son identifiant.
     * Utilise d'abord le cache en mémoire pour optimiser les performances.
     * Récupère les informations du département associé via JOIN.
     * 
     * @param id L'identifiant unique de l'étudiant
     * @return L'objet Student trouvé, ou null si aucun étudiant ne correspond
     */
    public Student get(int id) {
        // Check cache first
        Student cached = AppCache.getInstance().getStudentById(id);
        if (cached != null) {
            return cached;
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "SELECT * FROM student WHERE student_id = ?";

            ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            rs = ps.executeQuery();

            if (rs.next()) {
                Student student = map(rs);
                AppCache.getInstance().addStudentToCache(student);
                return student;
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
     * Récupère tous les étudiants de la base de données.
     * Utilise le cache en mémoire pour éviter les requêtes répétées.
     * Les résultats sont ordonnés par student_id.
     * 
     * @return Liste de tous les étudiants, vide si aucun étudiant n'existe
     */
    public ArrayList<Student> getList() {
        // Check cache first
        if (AppCache.getInstance().getStudents() != null) {
            return AppCache.getInstance().getStudents();
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        ArrayList<Student> list = new ArrayList<>();

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "SELECT * FROM student ORDER BY student_id";

            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(map(rs));
            }

            // Cache the result
            AppCache.getInstance().setStudents(list);

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
    /**
     * Convertit une ligne ResultSet en objet Student.
     * Mappe tous les champs de la table STUDENT aux propriétés de l'objet.
     * 
     * @param rs Le ResultSet contenant les données de l'étudiant
     * @return Un objet Student complètement initialisé
     * @throws SQLException si une erreur d'accès aux données se produit
     */
    private Student map(ResultSet rs) throws SQLException {
        return new Student(
            rs.getInt("student_id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("email"),
            rs.getString("student_level"),
            rs.getInt("promotion")
        );
    }
}

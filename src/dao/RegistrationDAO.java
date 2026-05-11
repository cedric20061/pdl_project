package dao;

import java.sql.*;
import java.util.ArrayList;

import model.Registration;
import service.AppCache;

/**
 * Data Access Object pour la gestion des inscriptions aux sessions.
 * Gère les opérations CRUD (Create, Read, Update, Delete) pour la table REGISTRATION.
 * 
 * Responsabilités :
 * - Création d'inscriptions (ajout d'un étudiant à une session)
 * - Modification du statut ou du rang de préférence d'une inscription
 * - Suppression d'inscriptions
 * - Récupération des inscriptions avec filtrage par étudiant, session ou campagne
 * - Gestion du cache multi-niveaux (global, par étudiant, par session, par campagne)
 * - Gestion de la capacité restante des sessions (remaining_capacity)
 * 
 * Gestion de la capacité :
 * - Avant l'ajout : VÉRIFICATION que la session a des places disponibles (remaining_capacity > 0)
 * - L'ajout d'une inscription DÉCRÉMENTE la capacité restante de la session
 * - La suppression d'une inscription INCRÉMENTE la capacité restante de la session
 * - La capacité est mise à jour dans la table SESSIONS automatiquement
 * 
 * Codes de retour pour add() :
 * - 1 : Inscription créée avec succès
 * - -1 : Doublon d'inscription existante
 * - -2 : Aucune place disponible dans la session
 * - 0 : Erreur lors de la création
 * 
 * Le cache est automatiquement invalidé après chaque mutation (add, update, delete).
 * Les caches invalidés : registrations, registrationsByStudent, sessions
 * Les statuts valides : PENDING, CONFIRMED, VALIDATED, REJECTED
 * 
 * @author PDL Team
 * @version 2.2
 * @see Registration
 * @see AppCache
 */
public class RegistrationDAO extends ConnectionDAO {

    /**
     * Constructeur par défaut.
     * Initialise la connexion à la base de données via le parent ConnectionDAO.
     */
    public RegistrationDAO() {
        super();
    }

    // ==========================
    // CREATE
    // ==========================
    /**
     * Crée une nouvelle inscription en base de données.
     * Vérifie d'abord que la session a des places disponibles (remaining_capacity > 0).
     * Décrémente la capacité restante (remaining_capacity) de la session.
     * Invalide tous les caches d'inscription et sessions après insertion.
     * 
     * @param studentId L'ID de l'étudiant
     * @param sessionId L'ID de la session
     * @param preferenceRank Le rang de préférence de l'étudiant (1, 2, 3...)
     * @param status Le statut initial de l'inscription (PENDING, CONFIRMED, VALIDATED, REJECTED)
     * @return 1 si l'insertion a réussi, -1 si doublon existant, -2 si pas de place disponible, 0 sinon
     */
    public int add(int studentId, int sessionId, int preferenceRank, String status) {
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement capacityCheckPs = null;
        ResultSet capacityRs = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            // Vérifier la capacité restante
            String capacitySql = "SELECT remaining_capacity FROM SESSIONS WHERE session_id = ?";
            capacityCheckPs = con.prepareStatement(capacitySql);
            capacityCheckPs.setInt(1, sessionId);
            capacityRs = capacityCheckPs.executeQuery();

            if (capacityRs.next()) {
                int remainingCapacity = capacityRs.getInt("remaining_capacity");
                if (remainingCapacity <= 0) {
                    System.out.println("La session n'a plus de places disponibles!");
                    return -2; // No capacity available
                }
            }

            String sql = "INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) "
                       + "VALUES (?, ?, ?, ?)";

            ps = con.prepareStatement(sql);

            ps.setInt(1, studentId);
            ps.setInt(2, sessionId);
            ps.setInt(3, preferenceRank);
            ps.setString(4, status);

            int result = ps.executeUpdate();

            // Décrémenter session remaining capacity
            if (result > 0) {
                String updateSql = "UPDATE SESSIONS SET remaining_capacity = remaining_capacity - 1 WHERE session_id = ?";
                PreparedStatement updatePs = con.prepareStatement(updateSql);
                updatePs.setInt(1, sessionId);
                updatePs.executeUpdate();
                updatePs.close();
            }

            // Invalider le cache pour cet étudiant et pour les sessions
            AppCache.getInstance().setRegistrationsByStudent(studentId, null);
            AppCache.getInstance().setSessions(null);

            return result;

        } catch (Exception e) {
            if (e.getMessage().contains("ORA-00001")) {
                System.out.println("Inscription déjà existante !");
                return -1; // Indicate duplicate registration
            } else {
                e.printStackTrace();
            }
        } finally {
            try { if (capacityRs != null) capacityRs.close(); } catch (Exception ignored) {}
            try { if (capacityCheckPs != null) capacityCheckPs.close(); } catch (Exception ignored) {}
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }
        return 0;
    }

    // ==========================
    // UPDATE (status / rank)
    // ==========================
    /**
     * Met à jour une inscription existante (rang et statut).
     * Invalide tous les caches d'inscription après mise à jour.
     * 
     * @param r L'objet Registration avec les nouvelles données
     * @return 1 si la mise à jour a réussi, 0 sinon
     */
    public int update(Registration r) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "UPDATE REGISTRATION SET preference_rank = ?, status = ? "
                       + "WHERE student_id = ? AND session_id = ?";

            ps = con.prepareStatement(sql);

            ps.setInt(1, r.getRank());
            ps.setString(2, r.getStatus());
            ps.setInt(3, r.getStudentId());
            ps.setInt(4, r.getSessionId());

            int result = ps.executeUpdate();

            // Invalidate cache for this student
            AppCache.getInstance().setRegistrationsByStudent(r.getStudentId(), null);

            return result;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }
        return 0;
    }

    /**
     * Update only the status of a registration.
     * Invalide tous les caches d'inscription après mise à jour.
     * 
     * @param studentId L'ID de l'étudiant
     * @param sessionId L'ID de la session
     * @param newStatus Le nouveau statut de l'inscription
     * @return Le nombre de lignes affectées (1 ou 0)
     */
    public int updateStatus(int studentId, int sessionId, String newStatus) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "UPDATE REGISTRATION SET status = ? "
                       + "WHERE student_id = ? AND session_id = ?";

            ps = con.prepareStatement(sql);

            ps.setString(1, newStatus);
            ps.setInt(2, studentId);
            ps.setInt(3, sessionId);

            int result = ps.executeUpdate();

            // Invalidate cache for this student
            AppCache.getInstance().setRegistrationsByStudent(studentId, null);

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
     * Supprime une inscription de la base de données.
     * Incrémente la capacité restante (remaining_capacity) de la session.
     * Invalide tous les caches d'inscription et sessions après suppression.
     * 
     * @param studentId L'ID de l'étudiant
     * @param sessionId L'ID de la session
     * @return Le nombre de lignes supprimées (1 ou 0)
     */
    public int delete(int studentId, int sessionId) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "DELETE FROM REGISTRATION WHERE student_id = ? AND session_id = ?";
            ps = con.prepareStatement(sql);

            ps.setInt(1, studentId);
            ps.setInt(2, sessionId);

            int result = ps.executeUpdate();

            // Increment session remaining capacity
            if (result > 0) {
                String updateSql = "UPDATE SESSIONS SET remaining_capacity = remaining_capacity + 1 WHERE session_id = ?";
                PreparedStatement updatePs = con.prepareStatement(updateSql);
                updatePs.setInt(1, sessionId);
                updatePs.executeUpdate();
                updatePs.close();
            }

            // Invalidate cache for this student and sessions
            AppCache.getInstance().setRegistrationsByStudent(studentId, null);
            AppCache.getInstance().setSessions(null);

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
    // GET ALL (avec JOIN STUDENT)
    // ==========================
    public ArrayList<Registration> getList() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        ArrayList<Registration> list = new ArrayList<>();

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "SELECT r.*, s.last_name AS student_name, s.email AS student_email " +
                         "FROM REGISTRATION r " +
                         "JOIN STUDENT s ON r.student_id = s.student_id";

            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(map(rs));
            }

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
    // GET BY SESSION
    // ==========================
    public ArrayList<Registration> getBySession(int sessionId) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        ArrayList<Registration> list = new ArrayList<>();

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "SELECT r.*, s.last_name AS student_name, s.email AS student_email " +
                         "FROM REGISTRATION r " +
                         "JOIN STUDENT s ON r.student_id = s.student_id " +
                         "WHERE r.session_id = ?";

            ps = con.prepareStatement(sql);
            ps.setInt(1, sessionId);

            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(map(rs));
            }

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
    // GET BY STUDENT
    // ==========================
    public ArrayList<Registration> getByStudent(int studentId) {
        // Check cache first
        ArrayList<Registration> cached = AppCache.getInstance().getRegistrationsByStudent(studentId);
        if (cached != null) {
            return cached;
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        ArrayList<Registration> list = new ArrayList<>();

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "SELECT r.*, s.last_name AS student_name, s.email AS student_email " +
                         "FROM REGISTRATION r " +
                         "JOIN STUDENT s ON r.student_id = s.student_id " +
                         "WHERE r.student_id = ?";

            ps = con.prepareStatement(sql);
            ps.setInt(1, studentId);

            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(map(rs));
            }

            // Cache the result
            AppCache.getInstance().setRegistrationsByStudent(studentId, list);

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
    // GET BY SESSION AND STUDENT
    // ==========================
    /**
     * Find a specific registration by session and student.
     * @param sessionId The session ID
     * @param studentId The student ID
     * @return The registration if exists, null otherwise
     */
    public Registration findBySessionAndStudent(int sessionId, int studentId) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "SELECT r.*, s.last_name AS student_name, s.email AS student_email " +
                         "FROM REGISTRATION r " +
                         "JOIN STUDENT s ON r.student_id = s.student_id " +
                         "WHERE r.session_id = ? AND r.student_id = ?";

            ps = con.prepareStatement(sql);
            ps.setInt(1, sessionId);
            ps.setInt(2, studentId);

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

    /**
     * Add a new registration using a Registration object.
     * @param registration The registration to add
     * @return The number of rows affected
     */
    public int add(Registration registration) {
        return add(registration.getStudentId(), registration.getSessionId(), 
                   registration.getRank(), registration.getStatus());
    }

    /**
     * Delete a registration by session and student.
     * Incrémente la capacité restante (remaining_capacity) de la session.
     * Invalide le cache d'inscription et sessions après suppression.
     * 
     * @param sessionId The session ID
     * @param studentId The student ID
     * @return The number of rows affected
     */
    public int deleteRegistration(int sessionId, int studentId) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "DELETE FROM REGISTRATION WHERE student_id = ? AND session_id = ?";
            ps = con.prepareStatement(sql);

            ps.setInt(1, studentId);
            ps.setInt(2, sessionId);

            int result = ps.executeUpdate();

            // Increment session remaining capacity
            if (result > 0) {
                String updateSql = "UPDATE SESSIONS SET remaining_capacity = remaining_capacity + 1 WHERE session_id = ?";
                PreparedStatement updatePs = con.prepareStatement(updateSql);
                updatePs.setInt(1, sessionId);
                updatePs.executeUpdate();
                updatePs.close();
            }

            // Invalidate cache for this student and sessions
            AppCache.getInstance().setRegistrationsByStudent(studentId, null);
            AppCache.getInstance().setSessions(null);

            return result;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }
        return 0;
    }

    /**
     * Find registrations for a student in a specific campaign.
     * @param studentId The student ID
     * @param campaignId The campaign ID
     * @return List of registrations for the student in the campaign
     */
    public ArrayList<Registration> findByStudentAndCampaign(int studentId, int campaignId) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        ArrayList<Registration> list = new ArrayList<>();

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "SELECT r.*, s.last_name AS student_name, s.email AS student_email " +
                         "FROM REGISTRATION r " +
                         "JOIN STUDENT s ON r.student_id = s.student_id " +
                         "JOIN SESSIONS se ON r.session_id = se.session_id " +
                         "WHERE r.student_id = ? AND se.campaign_id = ?";

            ps = con.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setInt(2, campaignId);

            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(map(rs));
            }

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
    private Registration map(ResultSet rs) throws SQLException {
        return new Registration(
            rs.getInt("session_id"),
            rs.getInt("student_id"),
            rs.getString("student_name"),
            rs.getString("student_email"),
            rs.getInt("preference_rank"),
            rs.getString("status")
        );
    }
}
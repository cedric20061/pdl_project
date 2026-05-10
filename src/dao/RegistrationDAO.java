package dao;

import java.sql.*;
import java.util.ArrayList;

import model.Registration;
import service.AppCache;

public class RegistrationDAO extends ConnectionDAO {

    public RegistrationDAO() {
        super();
    }

    // ==========================
    // CREATE
    // ==========================
    public int add(int studentId, int sessionId, int preferenceRank, String status) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "INSERT INTO REGISTRATION (student_id, session_id, preference_rank, status) "
                       + "VALUES (?, ?, ?, ?)";

            ps = con.prepareStatement(sql);

            ps.setInt(1, studentId);
            ps.setInt(2, sessionId);
            ps.setInt(3, preferenceRank);
            ps.setString(4, status);

            int result = ps.executeUpdate();

            // Invalidate cache for this student
            AppCache.getInstance().setRegistrationsByStudent(studentId, null);

            return result;

        } catch (Exception e) {
            if (e.getMessage().contains("ORA-00001")) {
                System.out.println("Inscription déjà existante !");
            } else {
                e.printStackTrace();
            }
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }
        return 0;
    }

    // ==========================
    // UPDATE (status / rank)
    // ==========================
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
     * @param studentId The student ID
     * @param sessionId The session ID
     * @param newStatus The new status value
     * @return The number of rows affected
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
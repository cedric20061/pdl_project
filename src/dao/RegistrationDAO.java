package dao;

import java.sql.*;
import java.util.ArrayList;

import model.Registration;

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

            return ps.executeUpdate();

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

            return ps.executeUpdate();

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

            return ps.executeUpdate();

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
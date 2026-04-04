package dao;

import java.sql.*;
import java.util.ArrayList;

import model.Specialization;
import service.AppCache;

public class SpecializationDAO extends ConnectionDAO {

    public SpecializationDAO() {
        super();
    }

    // ==========================
    // CREATE
    // ==========================
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
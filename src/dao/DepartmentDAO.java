package dao;

import java.sql.*;
import java.util.ArrayList;

import model.Department;
import service.AppCache;

public class DepartmentDAO extends ConnectionDAO {

    public DepartmentDAO() {
        super();
    }

    // ==========================
    // CREATE
    // ==========================
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
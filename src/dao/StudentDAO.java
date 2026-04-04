package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Student;
import service.AppCache;

public class StudentDAO extends ConnectionDAO {
    
    public StudentDAO(){
        super();
    }
    // ==========================
    // GET BY ID (avec JOIN)
    // ==========================
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

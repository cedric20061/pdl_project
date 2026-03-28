package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Student;

public class StudentDAO extends ConnectionDAO {
    
    public StudentDAO(){
        super();
    }
    // ==========================
    // GET BY ID (avec JOIN)
    // ==========================
    public Student get(int id) {
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
    public ArrayList<Student> getList() {
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

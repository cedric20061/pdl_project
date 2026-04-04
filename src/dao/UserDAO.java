package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.Admin;
import model.Student;
import model.User;
import service.AppSession;

public class UserDAO extends ConnectionDAO {
    /**
	 * Constructor
	 * 
	 */
	public UserDAO() {
		super();
	}

	/**
	 * Permet de connecter un utilisateur (admin ou étudiant) à partir de son identifiant (id ou email) et de son mot de passe.
	 * 
	 * @param identifier l'identifiant de l'utilisateur (id ou email)
	 * @param password le mot de passe de l'utilisateur
	 * @return l'utilisateur connecté, ou null si les identifiants sont incorrects
	 */
	public User login(String identifier, String password) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            boolean isNumeric = identifier.matches("\\d+");

            // ==========================
            // 1. ADMIN
            // ==========================
            String sqlAdmin;

            if (isNumeric) {
                sqlAdmin = "SELECT * FROM administrator WHERE admin_id = ? AND password_hash = ?";
                ps = con.prepareStatement(sqlAdmin);
                ps.setInt(1, Integer.parseInt(identifier));
                ps.setString(2, password);
            } else {
                sqlAdmin = "SELECT * FROM administrator WHERE email = ? AND password_hash = ?";
                ps = con.prepareStatement(sqlAdmin);
                ps.setString(1, identifier);
                ps.setString(2, password);
            }

            rs = ps.executeQuery();

            if (rs.next()) {
                Admin admin = new Admin(
                    rs.getInt("admin_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email")
                );

                AppSession.getInstance().setUser(admin);
                AppSession.getInstance().setIsAdmin(true);

                return admin;
            }

            rs.close();
            ps.close();

            // ==========================
            // 2. STUDENT
            // ==========================
            String sqlStudent;

            if (isNumeric) {
                sqlStudent = "SELECT * FROM student WHERE student_id = ? AND password_hash = ?";
                ps = con.prepareStatement(sqlStudent);
                ps.setInt(1, Integer.parseInt(identifier));
                ps.setString(2, password);
            } else {
                sqlStudent = "SELECT * FROM student WHERE email = ? AND password_hash = ?";
                ps = con.prepareStatement(sqlStudent);
                ps.setString(1, identifier);
                ps.setString(2, password);
            }

            rs = ps.executeQuery();

            if (rs.next()) {
                Student student = new Student(
                    rs.getInt("student_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("student_level"),
                    rs.getInt("promotion")
                );

                AppSession.getInstance().setUser(student);
                AppSession.getInstance().setIsAdmin(false);

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
	
}

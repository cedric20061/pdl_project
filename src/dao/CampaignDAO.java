package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import model.Admin;
import model.Campaign;
import service.AppSession;

public class CampaignDAO extends ConnectionDAO {

    public CampaignDAO() {
        super();
    }

    // ==========================
    // CREATE
    // ==========================
    public int add(Campaign campaign) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Admin admin = (Admin) AppSession.getInstance().getUser();

        if (admin == null) {
            throw new IllegalStateException("Aucun utilisateur connecté");
        }
        if (!AppSession.getInstance().getIsAdmin()) {
            throw new IllegalStateException("L'utilisateur connecté n'est pas un administrateur");
        }
        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);
 
            String sql = "INSERT INTO CAMPAIGN (start_date, end_date, status, max_choices, promotion, created_by) "
                       + "VALUES (TO_DATE(?, 'YYYY-MM-DD'), TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?, ?)";

            ps = con.prepareStatement(sql, new String[] { "campaign_id" });
            
            ps.setString(1, campaign.getStartDate().toString()); // java.sql.Date
            ps.setString(2, campaign.getEndDate().toString());
            ps.setString(3, campaign.getStatus());
            ps.setInt(4, campaign.getMaxChoices());
            ps.setInt(5, campaign.getPromotion());
            ps.setInt(6, admin.getId());


            int returnValue = ps.executeUpdate();

            // 🔥 récupérer l'ID généré
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                campaign.setId(rs.getInt(1));
                campaign.setCreatedBy(admin.getLastName());
            }

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
    public int update(Campaign campaign) {
        Connection con = null;
        PreparedStatement ps = null;
        Admin admin = (Admin) AppSession.getInstance().getUser();

        if (admin == null) {
            throw new IllegalStateException("Aucun utilisateur connecté");
        }
        if (!AppSession.getInstance().getIsAdmin()) {
            throw new IllegalStateException("L'utilisateur connecté n'est pas un administrateur");
        }
        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "UPDATE CAMPAIGN SET start_date = TO_DATE(?, 'YYYY-MM-DD'), end_date = TO_DATE(?, 'YYYY-MM-DD'), status = ?, "
                       + "max_choices = ?, promotion = ?, modified_by = ? "
                       + "WHERE campaign_id = ?";

            ps = con.prepareStatement(sql);

            ps.setString(1, campaign.getStartDate().toString());
            ps.setString(2, campaign.getEndDate().toString());
            ps.setString(3, campaign.getStatus());
            ps.setInt(4, campaign.getMaxChoices());
            ps.setInt(5, campaign.getPromotion());
            ps.setInt(6, admin.getId());
            ps.setInt(7, campaign.getId());

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
    public int delete(int id) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "DELETE FROM CAMPAIGN WHERE campaign_id = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);

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
    // GET BY ID
    // ==========================
    public Campaign get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "SELECT c.*, " +
             "a1.last_name AS created_by_name, " +
             "a2.last_name AS modified_by_name " +
             "FROM CAMPAIGN c " +
             "LEFT JOIN ADMINISTRATOR a1 ON c.created_by = a1.admin_id " +
             "LEFT JOIN ADMINISTRATOR a2 ON c.modified_by = a2.admin_id " +
             "WHERE c.campaign_id = ?";
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
    public ArrayList<Campaign> getList() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        ArrayList<Campaign> list = new ArrayList<>();

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "SELECT c.*, " +
             "a1.last_name AS created_by_name, " +
             "a2.last_name AS modified_by_name " +
             "FROM CAMPAIGN c " +
             "LEFT JOIN ADMINISTRATOR a1 ON c.created_by = a1.admin_id " +
             "LEFT JOIN ADMINISTRATOR a2 ON c.modified_by = a2.admin_id " +
             "ORDER BY c.campaign_id";
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
    // MAPPING (très important)
    // ==========================
    private Campaign map(ResultSet rs) throws SQLException {
        Campaign c = new Campaign();

        c.setId(rs.getInt("campaign_id"));
        c.setStartDate(LocalDate.parse(rs.getDate("start_date").toString()));
        c.setEndDate(LocalDate.parse(rs.getDate("end_date").toString()));
        c.setStatus(rs.getString("status"));
        c.setMaxChoices(rs.getInt("max_choices"));
        c.setPromotion(rs.getInt("promotion"));

        // 🔥 NOUVEAU
        c.setCreatedBy(rs.getString("created_by_name"));
        c.setModifiedBy(rs.getString("modified_by_name"));

        return c;
    }
}
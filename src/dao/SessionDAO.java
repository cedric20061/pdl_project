package dao;

import java.sql.*;
import java.util.ArrayList;

import model.Session;
import service.AppCache;
import service.AppSession;

public class SessionDAO extends ConnectionDAO {

    public SessionDAO() {
        super();
    }

    // ==========================
    // CREATE
    // ==========================
    public int add(Session s) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "INSERT INTO SESSIONS (session_date, start_time, end_time, max_capacity, remaining_capacity, room, specialization_id, campaign_id, created_by) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            ps = con.prepareStatement(sql, new String[]{"session_id"});

            ps.setDate(1, Date.valueOf(s.getDate()));
            ps.setTimestamp(2, Timestamp.valueOf(s.getDate().atTime(s.getStartTime())));
            ps.setTimestamp(3, Timestamp.valueOf(s.getDate().atTime(s.getEndTime())));
            ps.setInt(4, s.getMaxCapacity());
            ps.setInt(5, s.getMaxCapacity());
            ps.setString(6, s.getRoom());
            ps.setInt(7, s.getSpecializationId());
            ps.setInt(8, s.getCampaignId());
            ps.setInt(9, AppSession.getInstance().getUser().getId());

            int returnValue = ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                s.setId(rs.getInt(1));
                s.setCreatedBy(AppSession.getInstance().getUser().getLastName());
            }

            if(AppCache.getInstance().getSessions() != null) {
                AppCache.getInstance().getSessions().add(s);
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
    public int update(Session s) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "UPDATE SESSIONS SET session_date = ?, start_time = ?, end_time = ?, "
                       + "max_capacity = ?, remaining_capacity = ?, room = ?, specialization_id = ?, campaign_id = ?, modified_by = ? "
                       + "WHERE session_id = ?";

            ps = con.prepareStatement(sql);

            ps.setDate(1, Date.valueOf(s.getDate()));
            ps.setTimestamp(2, Timestamp.valueOf(s.getDate().atTime(s.getStartTime())));
            ps.setTimestamp(3, Timestamp.valueOf(s.getDate().atTime(s.getEndTime())));
            ps.setInt(4, s.getMaxCapacity());
            ps.setInt(5, s.getRemainingCapacity());
            ps.setString(6, s.getRoom());
            ps.setInt(7, s.getSpecializationId());
            ps.setInt(8, s.getCampaignId());
            ps.setInt(9, AppSession.getInstance().getUser().getId());
            ps.setInt(10, s.getId());

            if(AppCache.getInstance().getSessions() != null) {
                AppCache.getInstance().getSessions().removeIf(sess -> sess.getId() == s.getId());
                AppCache.getInstance().getSessions().add(s);
            }
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

            String sql = "DELETE FROM SESSIONS WHERE session_id = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            if(AppCache.getInstance().getSessions() != null) {
                AppCache.getInstance().getSessions().removeIf(s -> s.getId() == id);
            }
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
    // GET BY ID (JOIN complet)
    // ==========================
    public Session get(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        if(AppCache.getInstance().getSessions() != null) {
            return AppCache.getInstance().getSessions().stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElse(null);
        }
        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "SELECT s.*, " +
                         "a1.last_name AS created_by_name, " +
                         "a2.last_name AS modified_by_name, " +
                         "sp.name AS specialization_name, " +
                         "c.start_date AS campaign_start_date, " +
                         "c.end_date AS campaign_end_date " +
                         "FROM SESSIONS s " +
                         "LEFT JOIN ADMINISTRATOR a1 ON s.created_by = a1.admin_id " +
                         "LEFT JOIN ADMINISTRATOR a2 ON s.modified_by = a2.admin_id " +
                        "LEFT JOIN SPECIALIZATION sp ON s.specialization_id = sp.specialization_id " +
                        "LEFT JOIN CAMPAIGN c ON s.campaign_id = c.campaign_id " +
                         "WHERE s.session_id = ?";

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
    public ArrayList<Session> getList() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        ArrayList<Session> list = new ArrayList<>();

        if(AppCache.getInstance().getSessions() != null) {
            return AppCache.getInstance().getSessions();
        }
        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "SELECT s.*, " +
                         "a1.last_name AS created_by_name, " +
                         "a2.last_name AS modified_by_name, " +
                         "sp.name AS specialization_name, " +
                         "c.start_date AS campaign_start_date, " +
                         "c.end_date AS campaign_end_date " +
                         "FROM SESSIONS s " +
                         "LEFT JOIN ADMINISTRATOR a1 ON s.created_by = a1.admin_id " +
                         "LEFT JOIN ADMINISTRATOR a2 ON s.modified_by = a2.admin_id " +
                         "LEFT JOIN SPECIALIZATION sp ON s.specialization_id = sp.specialization_id " +
                         "LEFT JOIN CAMPAIGN c ON s.campaign_id = c.campaign_id " +
                         "ORDER BY s.session_id";

            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(map(rs));
            }

            AppCache.getInstance().setSessions(list);

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
    private Session map(ResultSet rs) throws SQLException {

        Date date = rs.getDate("session_date");
        Timestamp start = rs.getTimestamp("start_time");
        Timestamp end = rs.getTimestamp("end_time");

        return new Session(
            rs.getInt("session_id"),
            date.toLocalDate().toString(),
            start.toLocalDateTime().toLocalTime().toString(),
            end.toLocalDateTime().toLocalTime().toString(),
            rs.getInt("max_capacity"),
            rs.getInt("remaining_capacity"),
            rs.getString("room"),
            rs.getInt("specialization_id"),
            rs.getString("specialization_name"), // from JOIN
            rs.getInt("campaign_id"),
            "Campagne - " + rs.getInt("campaign_id") + " - " + (rs.getDate("campaign_start_date").toString()) + " à " + (rs.getDate("campaign_end_date").toString()), // from JOIN
            rs.getString("created_by_name"),
            rs.getString("modified_by_name")
        );
    }
}
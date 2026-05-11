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
import service.AppCache;

/**
 * Data Access Object pour la gestion des campagnes d'inscriptions.
 * Gère les opérations CRUD (Create, Read, Update, Delete) pour la table CAMPAIGN.
 * 
 * Responsabilités :
 * - Création de campagnes avec validation de l'utilisateur administrateur
 * - Modification des détails de campagne (dates, statut, max_choices, promotion)
 * - Suppression de campagnes existantes
 * - Récupération des campagnes avec cache en mémoire
 * - Filtrage des campagnes actives (statut OPEN)
 * 
 * Le cache est automatiquement invalidé après chaque mutation (add, update, delete).
 * 
 * @author PDL Team
 * @version 2.0
 * @see Campaign
 * @see AppCache
 * @see Admin
 */
public class CampaignDAO extends ConnectionDAO {

    /**
     * Constructeur par défaut.
     * Initialise la connexion à la base de données via le parent ConnectionDAO.
     */
    public CampaignDAO() {
        super();
    }

    // ==========================
    // CREATE
    // ==========================
    /**
     * Crée une nouvelle campagne d'inscriptions en base de données.
     * Valide que l'utilisateur connecté est administrateur.
     * Récupère l'ID généré et le stocke dans l'objet campagne.
     * Invalide le cache après insertion.
     * 
     * @param campaign L'objet Campaign à insérer
     * @return 1 si l'insertion a réussi, 0 sinon
     * @throws IllegalStateException si aucun utilisateur n'est connecté ou si l'utilisateur n'est pas admin
     */
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

            // Invalidate cache
            AppCache.getInstance().setCampaigns(null);

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
    /**
     * Met à jour une campagne existante en base de données.
     * Valide que l'utilisateur connecté est administrateur.
     * Modifie les dates, statut, nombre de choix maximum et promotion.
     * Invalide le cache après mise à jour.
     * 
     * @param campaign L'objet Campaign avec les nouvelles données
     * @return 1 si la mise à jour a réussi, 0 sinon
     * @throws IllegalStateException si aucun utilisateur n'est connecté ou si l'utilisateur n'est pas admin
     */
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

            int result = ps.executeUpdate();
            // Invalidate cache
            AppCache.getInstance().setCampaigns(null);
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
     * Supprime une campagne de la base de données par son ID.
     * Invalide le cache après suppression.
     * 
     * @param id L'identifiant unique de la campagne à supprimer
     * @return 1 si la suppression a réussi, 0 sinon
     */
    public int delete(int id) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DriverManager.getConnection(URL, LOGIN, PASS);

            String sql = "DELETE FROM CAMPAIGN WHERE campaign_id = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            // Invalidate cache
            AppCache.getInstance().setCampaigns(null);
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
    /**
     * Récupère une campagne par son identifiant.
     * Utilise d'abord le cache en mémoire pour optimiser les performances.
     * Récupère les noms des administrateurs créateur et modifieur via JOIN.
     * 
     * @param id L'identifiant unique de la campagne
     * @return L'objet Campaign trouvé, ou null si aucune campagne ne correspond
     */
    public Campaign get(int id) {
        // Check cache first
        ArrayList<Campaign> cached = AppCache.getInstance().getCampaigns();
        if (cached != null) {
            return cached.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
        }

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
    /**
     * Récupère toutes les campagnes de la base de données.
     * Utilise le cache en mémoire pour éviter les requêtes répétées.
     * Les résultats sont ordonnés par campaign_id.
     * Inclut les noms des administrateurs (créateur et modifieur).
     * 
     * @return Liste de toutes les campagnes, vide si aucune campagne n'existe
     */
    public ArrayList<Campaign> getList() {
        // Check cache first
        if (AppCache.getInstance().getCampaigns() != null) {
            return AppCache.getInstance().getCampaigns();
        }

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

            // Cache the result
            AppCache.getInstance().setCampaigns(list);

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
    // GET ACTIVE CAMPAIGNS
    // ==========================
    /**
     * Récupère toutes les campagnes actuellement actives (statut = "OPEN").
     * Filtre le cache complet en mémoire pour optimiser les performances.
     * Aucune requête supplémentaire à la base de données n'est effectuée.
     * 
     * @return Liste des campagnes avec le statut OPEN
     */
    public ArrayList<Campaign> getActiveCampaigns() {
        // Use full cache and filter in-memory
        ArrayList<Campaign> allCampaigns = getList();
        ArrayList<Campaign> active = new ArrayList<>();

        for (Campaign c : allCampaigns) {
            if ("OPEN".equals(c.getStatus())) {
                active.add(c);
            }
        }

        return active;
    }

    // ==========================
    // MAPPING (très important)
    // ==========================
    /**
     * Convertit une ligne ResultSet en objet Campaign.
     * Mappe tous les champs de la table CAMPAIGN aux propriétés de l'objet.
     * Gère la conversion des dates (java.sql.Date -> LocalDate).
     * 
     * @param rs Le ResultSet contenant les données de la campagne
     * @return Un objet Campaign complètement initialisé
     * @throws SQLException si une erreur d'accès aux données se produit
     */
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
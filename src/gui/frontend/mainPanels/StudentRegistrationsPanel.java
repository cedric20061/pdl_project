package gui.frontend.mainPanels;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

import common.components.app.UIStyle;
import gui.frontend.components.StudentRegistrationCard;
import model.Campaign;
import model.Registration;
import model.Session;
import model.Student;
import dao.CampaignDAO;
import dao.RegistrationDAO;
import dao.SessionDAO;

/**
 * Panel that displays the student's registrations for sessions.
 * Features:
 * - View registrations grouped by campaign
 * - Hide registrations from expired/closed campaigns
 * - Respect max_choices per campaign
 * - Modify preference order between registrations
 * - Cancel registrations
 * 
 * @author Cédric GUIDI && Baptiste DUCROCQ
 * @version 1.0
 */
public class StudentRegistrationsPanel extends JPanel {

    private Student student;
    private RegistrationDAO registrationDAO;
    private SessionDAO sessionDAO;
    private CampaignDAO campaignDAO;
    private ArrayList<Registration> allRegistrations;
    private ArrayList<Session> sessions;
    private ArrayList<Campaign> campaigns;
    
    private JPanel registrationCardsPanel;
    private JScrollPane scrollPane;
    private JComboBox<String> campaignFilterCombo;
    private JLabel countLabel;
    private Map<Integer, ArrayList<Registration>> registrationsByCampaign;
    private ArrayList<Integer> campaignFilterIds;  // Store campaign IDs for combo selection

    public StudentRegistrationsPanel(Student student) {
        this.student = student;
        this.registrationDAO = new RegistrationDAO();
        this.sessionDAO = new SessionDAO();
        this.campaignDAO = new CampaignDAO();
        this.allRegistrations = new ArrayList<>();
        this.sessions = new ArrayList<>();
        this.campaigns = new ArrayList<>();
        this.registrationsByCampaign = new HashMap<>();
        this.campaignFilterIds = new ArrayList<>();

        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        createHeader();
        add(createFilterBar(), BorderLayout.BEFORE_FIRST_LINE);
        createRegistrationsArea();
        loadRegistrations();
    }

    // ===========================
    // UI CREATION
    // ===========================
    private void createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        header.setBackground(Color.WHITE);

        // LEFT (titre)
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel title = new JLabel("Mes inscriptions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel subtitle = new JLabel("Gérez vos sessions par campagne");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setForeground(new Color(120,120,120));

        left.add(title);
        left.add(subtitle);

        // RIGHT (actions)
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        countLabel = new JLabel("0 inscription(s)");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(new Color(100,100,100));

        JButton refresh = new JButton("Actualiser");
        UIStyle.stylePrimaryButton(refresh);
        refresh.addActionListener(e -> loadRegistrations());

        right.add(countLabel);
        right.add(refresh);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private JPanel createFilterBar() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        wrapper.setOpaque(false);

        JPanel card = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel label = new JLabel("Campagne");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(new Color(90,90,90));

        JButton act = new JButton("Actualiser");
        UIStyle.stylePrimaryButton(act);
        act.addActionListener(e -> loadRegistrations());

        campaignFilterCombo = new JComboBox<>();
        UIStyle.styleComboBox(campaignFilterCombo);
        campaignFilterCombo.addActionListener(e -> displayCurrentCampaignRegistrations());

        card.add(label);
        card.add(campaignFilterCombo);
        card.add(act);

        wrapper.add(card, BorderLayout.WEST);

        return wrapper;
    }

    private void createRegistrationsArea() {
        registrationCardsPanel = new JPanel();
        registrationCardsPanel.setLayout(new BoxLayout(registrationCardsPanel, BoxLayout.Y_AXIS));
        registrationCardsPanel.setBackground(new Color(240, 242, 245));
        registrationCardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        scrollPane = new JScrollPane(registrationCardsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(240, 242, 245));
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);

        add(scrollPane, BorderLayout.CENTER);
    }

    // ===========================
    // LOAD AND DISPLAY
    // ===========================
    private void loadRegistrations() {
        allRegistrations = registrationDAO.getByStudent(student.getId());
        sessions = sessionDAO.getList();
        campaigns = campaignDAO.getList();
        
        // Group registrations by campaign (no filtering by status or expiry date)
        registrationsByCampaign.clear();
        for (Registration reg : allRegistrations) {
            Session session = sessions.stream()
                .filter(s -> s.getId() == reg.getSessionId())
                .findFirst()
                .orElse(null);
            
            if (session != null) {
                Campaign camp = campaigns.stream()
                    .filter(c -> c.getId() == session.getCampaignId())
                    .findFirst()
                    .orElse(null);
                
                if (camp != null) {
                    registrationsByCampaign.computeIfAbsent(camp.getId(), k -> new ArrayList<>()).add(reg);
                }
            }
        }

        
        // Sort registrations within each campaign by rank
        for (ArrayList<Registration> regs : registrationsByCampaign.values()) {
            regs.sort(Comparator.comparingInt(Registration::getRank));
        }
        
        // Update campaign combo
        updateCampaignCombo();
        displayCurrentCampaignRegistrations();
    }
    
    private void updateCampaignCombo() {
        campaignFilterCombo.removeAllItems();
        campaignFilterIds.clear();
        campaignFilterCombo.addItem("Toutes les campagnes");
        
        for (Campaign camp : campaigns) {
            if (registrationsByCampaign.containsKey(camp.getId())) {
                campaignFilterIds.add(camp.getId());
                campaignFilterCombo.addItem("Campagne " + camp.getId() + " (Promo " + camp.getPromotion() + ")");
            }
        }
    }
    
    private void displayCurrentCampaignRegistrations() {
        registrationCardsPanel.removeAll();

        int selectedIndex = campaignFilterCombo.getSelectedIndex();
        ArrayList<Registration> toDisplay = new ArrayList<>();

        if (selectedIndex == 0) {
            for (ArrayList<Registration> regs : registrationsByCampaign.values()) {
                toDisplay.addAll(regs);
            }
        } else if (selectedIndex > 0 && selectedIndex - 1 < campaignFilterIds.size()) {
            int selectedCampaignId = campaignFilterIds.get(selectedIndex - 1);
            toDisplay = registrationsByCampaign.getOrDefault(selectedCampaignId, new ArrayList<>());
        }

        countLabel.setText(toDisplay.size() + " inscription(s)");

        if (toDisplay.isEmpty()) {
            JLabel empty = new JLabel("Aucune inscription pour cette campagne", SwingConstants.CENTER);
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            empty.setForeground(new Color(140,140,140));
            empty.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));

            registrationCardsPanel.setLayout(new BorderLayout());
            registrationCardsPanel.add(empty, BorderLayout.CENTER);
        } else {
            registrationCardsPanel.setLayout(new BoxLayout(registrationCardsPanel, BoxLayout.Y_AXIS));

            for (Registration reg : toDisplay) {
                Session sessionInfo = sessions.stream()
                        .filter(s -> s.getId() == reg.getSessionId())
                        .findFirst()
                        .orElse(null);

                if (sessionInfo != null) {
                    StudentRegistrationCard card = new StudentRegistrationCard(
                            student, reg, sessionInfo,
                            () -> onRegistrationModified()
                    );

                    // 🔥 FIX ALIGNEMENT
                    card.setAlignmentX(Component.LEFT_ALIGNMENT);

                    registrationCardsPanel.add(card);
                    registrationCardsPanel.add(Box.createVerticalStrut(12));
                }
            }
        }

        registrationCardsPanel.revalidate();
        registrationCardsPanel.repaint();
    }

    private void onRegistrationModified() {
        loadRegistrations();
    }

    public void refreshData() {
        loadRegistrations();
    }
    
    public int getMaxChoicesForCampaign(int campaignId) {
        Campaign camp = campaigns.stream()
            .filter(c -> c.getId() == campaignId)
            .findFirst()
            .orElse(null);
        return camp != null ? camp.getMaxChoices() : 0;
    }
    
    public ArrayList<Integer> getUsedRanksForCampaign(int campaignId) {
        ArrayList<Integer> usedRanks = new ArrayList<>();
        ArrayList<Registration> regs = registrationsByCampaign.getOrDefault(campaignId, new ArrayList<>());
        for (Registration reg : regs) {
            usedRanks.add(reg.getRank());
        }
        return usedRanks;
    }
}

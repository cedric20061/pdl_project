package gui.frontend.mainPanels;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

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
        createRegistrationsArea();
        loadRegistrations();
    }

    // ===========================
    // UI CREATION
    // ===========================
    private void createHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(240, 242, 245));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JLabel titleLabel = new JLabel("Mes Inscriptions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(40, 40, 40));
        headerPanel.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel("Gérez vos inscriptions aux sessions par campagne");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(10));

        // Filter bar
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        filterPanel.setBackground(new Color(240, 242, 245));

        countLabel = new JLabel("0 inscription(s)");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(new Color(100, 100, 100));
        filterPanel.add(countLabel);

        // Campaign filter combo
        filterPanel.add(new JLabel("Campagne:"));
        campaignFilterCombo = new JComboBox<>();
        campaignFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        campaignFilterCombo.addActionListener(e -> displayCurrentCampaignRegistrations());
        filterPanel.add(campaignFilterCombo);

        JButton refreshButton = new JButton("Actualiser");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        refreshButton.setBackground(new Color(34, 139, 230));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadRegistrations());
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(refreshButton);

        headerPanel.add(filterPanel);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void createRegistrationsArea() {
        registrationCardsPanel = new JPanel();
        registrationCardsPanel.setLayout(new BoxLayout(registrationCardsPanel, BoxLayout.Y_AXIS));
        registrationCardsPanel.setBackground(new Color(240, 242, 245));
        registrationCardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        scrollPane = new JScrollPane(registrationCardsPanel);
        scrollPane.setBackground(new Color(240, 242, 245));
        scrollPane.getViewport().setBackground(new Color(240, 242, 245));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);

        add(scrollPane, BorderLayout.CENTER);
    }

    // ===========================
    // LOAD AND DISPLAY
    // ===========================
    private void loadRegistrations() {
        allRegistrations = registrationDAO.findByStudent(student.getId());
        sessions = sessionDAO.getList();
        campaigns = campaignDAO.getList();  // Get ALL campaigns, not just OPEN ones
        
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
            // Show all campaigns
            for (ArrayList<Registration> regs : registrationsByCampaign.values()) {
                toDisplay.addAll(regs);
            }
        } else if (selectedIndex > 0 && selectedIndex - 1 < campaignFilterIds.size()) {
            // Show specific campaign using the stored ID
            int selectedCampaignId = campaignFilterIds.get(selectedIndex - 1);
            toDisplay = registrationsByCampaign.getOrDefault(selectedCampaignId, new ArrayList<>());
        }
        
        countLabel.setText(toDisplay.size() + " inscription(s)");
        
        if (toDisplay.isEmpty()) {
            JLabel emptyLabel = new JLabel("Aucune inscription pour cette campagne");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            emptyLabel.setForeground(new Color(120, 120, 120));
            registrationCardsPanel.add(emptyLabel);
        } else {
            for (int i = 0; i < toDisplay.size(); i++) {
                Registration reg = toDisplay.get(i);
                Session sessionInfo = sessions.stream()
                    .filter(s -> s.getId() == reg.getSessionId())
                    .findFirst()
                    .orElse(null);

                if (sessionInfo != null) {
                    StudentRegistrationCard card = new StudentRegistrationCard(
                        student, reg, sessionInfo, 
                        () -> onRegistrationModified()
                    );
                    registrationCardsPanel.add(card);
                    registrationCardsPanel.add(Box.createVerticalStrut(10));
                }
            }
        }

        registrationCardsPanel.add(Box.createVerticalGlue());
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

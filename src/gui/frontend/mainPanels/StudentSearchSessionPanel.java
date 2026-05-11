package gui.frontend.mainPanels;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

import javax.swing.*;

import common.components.app.UIStyle;
import dao.SpecializationDAO;
import gui.frontend.components.SessionCard;
import gui.frontend.components.SessionDetailsDialog;
import gui.frontend.services.SessionFilterService;
import gui.frontend.utils.Tools;
import model.Session;
import model.Specialization;
import model.Student;

/**
 * Panel for students to search, browse, and register for sessions.
 * Features:
 * - Real-time search by specialization, campaign, date
 * - Filter by capacity (available/full)
 * - View session details and available capacity
 * - Register for sessions with preference ranking
 * - Get suggestions for full sessions
 * 
 * @author Cédric GUIDI && Baptiste DUCROCQ
 * @version 1.0
 */
public class StudentSearchSessionPanel extends JPanel {

    private Student student;
    private SessionFilterService filterService;
    //private SessionDAO sessionDAO;
    private SpecializationDAO specializationDAO;

    private ArrayList<Session> availableSessions;
    private ArrayList<Session> filteredSessions;

    // UI Components
    private JTextField searchField;
    private JComboBox<String> specializationFilter;
    private JComboBox<String> sortComboBox;
    private JLabel resultCountLabel;
    private JPanel sessionsContainer;
    private JScrollPane scrollPane;

    public StudentSearchSessionPanel(Student student) {
        this.student = student;
        this.filterService = new SessionFilterService();
        //this.sessionDAO = new SessionDAO();
        this.specializationDAO = new SpecializationDAO();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(new Color(240, 242, 245));

        // Load available sessions
        this.availableSessions = filterService.getAvailableSessionsForStudent(student);
        this.filteredSessions = new ArrayList<>(availableSessions);

        createHeader();
        this.add(Box.createVerticalStrut(15));
        createSearchBar();
        this.add(Box.createVerticalStrut(15));
        createSessionsDisplay();
    }

    // ===========================
    // HEADER
    // ===========================
    private void createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        header.setBackground(Color.WHITE);

        JLabel title = new JLabel("Rechercher des sessions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel subtitle = new JLabel("Explorez les dominantes disponibles");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setForeground(new Color(120,120,120));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        text.add(title);
        text.add(subtitle);

        // Add refresh button on the right
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        
        JButton refreshButton = new JButton("Actualiser");
        UIStyle.stylePrimaryButton(refreshButton);
        refreshButton.addActionListener(e -> refreshAvailableSessions());
        
        right.add(refreshButton);

        header.add(text, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        this.add(header, BorderLayout.NORTH);
    }

    // ===========================
    // SEARCH BAR
    // ===========================
    private void createSearchBar() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
        wrapper.setOpaque(false);

        // 🔹 Card container (effet bloc moderne)
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // ===========================
        // 🔎 SEARCH BAR (plus moderne)
        // ===========================
        searchField = new JTextField();
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        searchField.setToolTipText("Rechercher une session...");

        card.add(searchField);
        card.add(Box.createVerticalStrut(15));

        // ===========================
        // 🎛️ FILTERS ROW (alignés proprement)
        // ===========================
        JPanel filtersRow = new JPanel(new GridLayout(1, 2, 15, 0));
        filtersRow.setOpaque(false);

        // 🔹 Dominantes
        JPanel specPanel = new JPanel();
        specPanel.setLayout(new BoxLayout(specPanel, BoxLayout.Y_AXIS));
        specPanel.setOpaque(false);

        JLabel specLabel = Tools.createLabel("Dominantes");

        ArrayList<Specialization> specializations = specializationDAO.getList(); 
        ArrayList<String> specNames = new ArrayList<>(); 
        specNames.add("Toutes"); 
        for (Specialization spec : specializations) { 
            specNames.add(spec.getName()); 
        } 
        specializationFilter = new JComboBox<>(specNames.toArray(new String[0]));
        UIStyle.styleComboBox(specializationFilter);

        specPanel.add(specLabel);
        specPanel.add(Box.createVerticalStrut(5));
        specPanel.add(specializationFilter);

        // 🔹 Horaires
        JPanel timePanel = new JPanel();
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
        timePanel.setOpaque(false);

        JLabel timeLabel = Tools.createLabel("Horaires");

        sortComboBox = new JComboBox<>(generateTimeSlots());
        UIStyle.styleComboBox(sortComboBox);

        timePanel.add(timeLabel);
        timePanel.add(Box.createVerticalStrut(5));
        timePanel.add(sortComboBox);

        filtersRow.add(specPanel);
        filtersRow.add(timePanel);

        card.add(filtersRow);
        card.add(Box.createVerticalStrut(15));

        // ===========================
        // 🔘 BUTTONS (plus modernes)
        // ===========================
        JPanel buttons = new JPanel(new BorderLayout());
        buttons.setOpaque(false);

        JButton applyButton = new JButton("Appliquer");
        UIStyle.stylePrimaryButton(applyButton);
        applyButton.addActionListener(e -> updateFilteredSessions());

        JButton resetButton = new JButton("Réinitialiser");
        UIStyle.styleSecondaryButton(resetButton);
        resetButton.addActionListener(e -> resetFilters());

        JPanel leftButtons = new JPanel();
        leftButtons.setLayout(new BoxLayout(leftButtons, BoxLayout.X_AXIS));
        leftButtons.setOpaque(false);

        leftButtons.add(applyButton);
        leftButtons.add(Box.createHorizontalStrut(10));
        leftButtons.add(resetButton);

        buttons.add(leftButtons, BorderLayout.WEST);

        card.add(buttons);

        wrapper.add(card, BorderLayout.CENTER);
        this.add(wrapper);
    }

    private String[] generateTimeSlots() {
        ArrayList<String> slots = new ArrayList<>();
        String[] periods = {"Toutes", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30",
                            "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"};
        slots.addAll(java.util.Arrays.asList(periods));
        return slots.toArray(new String[0]);
    }

    // ===========================
    // SESSIONS DISPLAY
    // ===========================
    private void createSessionsDisplay() {
        JPanel displayWrapper = new JPanel(new BorderLayout());
        displayWrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        displayWrapper.setOpaque(false);

        // 🔹 Top bar (résultats + éventuellement tri plus tard)
        resultCountLabel = new JLabel();
        resultCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultCountLabel.setForeground(new Color(100, 100, 100));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(resultCountLabel, BorderLayout.WEST);

        displayWrapper.add(topPanel, BorderLayout.NORTH);

        // 🔹 Container GRID
        sessionsContainer = new JPanel();
        sessionsContainer.setOpaque(false);

        scrollPane = new JScrollPane(sessionsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);

        displayWrapper.add(scrollPane, BorderLayout.CENTER);

        this.add(displayWrapper);

        // 🔥 IMPORTANT → recalcul dynamique
        scrollPane.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                refreshSessionsDisplay();
            }
        });

        refreshSessionsDisplay();
    }
    private int calculateColumns() {
        int width = scrollPane.getViewport().getWidth();

        if (width > 1400) return 3;
        if (width > 900) return 2;
        return 1;
    }
    // ===========================
    // UPDATE & REFRESH
    // ===========================
    private void updateFilteredSessions() {
        filteredSessions = new ArrayList<>(availableSessions);
        // Apply search
        String searchText = searchField.getText();
        filteredSessions = filterService.searchBySpecialization(filteredSessions, searchText);
        // Apply specialization filter
        String selectedSpec = (String) specializationFilter.getSelectedItem();
        if (selectedSpec != null && !selectedSpec.equals("Toutes")) {
            filteredSessions = filterService.searchBySpecialization(filteredSessions, selectedSpec);
        }

        // Apply time filter or sorting
        String timeOrSort = (String) sortComboBox.getSelectedItem();
        if (timeOrSort != null && !timeOrSort.equals("Toutes")) {
            // Check if it's a time filter (e.g., "08:30") or a sort option
            if (timeOrSort.matches("\\d{2}:\\d{2}")) {
                // It's a time - filter sessions that start at this time
                filteredSessions = filterService.filterByStartTime(filteredSessions, timeOrSort);
            } else if (timeOrSort.contains("Capacité")) {
                // Sort by capacity
                filteredSessions = filterService.sortByCapacity(filteredSessions);
            } else if (timeOrSort.contains("Date et Capacité")) {
                // Sort by date and capacity
                filteredSessions = filterService.sortByDateAndCapacity(filteredSessions);
            } else {
                // Default: sort by date
                filteredSessions = filterService.sortByDate(filteredSessions);
            }
        }

        refreshSessionsDisplay();
    }

    private void refreshSessionsDisplay() {
        sessionsContainer.removeAll();

        int columns = calculateColumns();

        sessionsContainer.setLayout(new GridLayout(0, columns, 15, 15));

        if (filteredSessions.isEmpty()) {
            sessionsContainer.setLayout(new BorderLayout());

            JLabel emptyLabel = new JLabel("Aucune session trouvée", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(150, 150, 150));

            sessionsContainer.add(emptyLabel, BorderLayout.CENTER);
        } else {
            for (Session session : filteredSessions) {
                SessionCard card = new SessionCard(session, student, this::onSessionAction);

                // 🔥 IMPORTANT → force la card à remplir la cellule
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

                sessionsContainer.add(card);
            }
        }

        resultCountLabel.setText(filteredSessions.size() + " sessions trouvées");

        sessionsContainer.revalidate();
        sessionsContainer.repaint();
    }

    private void resetFilters() {
        searchField.setText("");
        specializationFilter.setSelectedIndex(0);
        sortComboBox.setSelectedIndex(0);
        filteredSessions = new ArrayList<>(availableSessions);
        refreshSessionsDisplay();
    }

    // ===========================
    // CALLBACKS
    // ===========================
    private void onSessionAction(String action, Session session) {
        switch (action) {
            case "REGISTER":
            case "DETAILS":
                showSessionDetailsDialog(session);
                break;
            case "SUGGESTIONS":
                showAlternativeSuggestions(session);
                break;
        }
    }

    private void showSessionDetailsDialog(Session session) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        SessionDetailsDialog dialog = new SessionDetailsDialog(
            parentFrame,
            student,
            session,
            () -> {
                availableSessions = filterService.getAvailableSessionsForStudent(student);
                updateFilteredSessions();
            }
        );
        dialog.setVisible(true);
    }

    private void showAlternativeSuggestions(Session session) {
        ArrayList<Session> suggestions = filterService.getSuggestionsForFullSession(student, session);

        if (suggestions.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Aucune session alternative disponible.",
                "Pas d'alternatives",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            StringBuilder msg = new StringBuilder("Sessions alternatives (même spécialité, 7 jours):\n\n");
            for (int i = 0; i < suggestions.size() && i < 3; i++) {
                Session s = suggestions.get(i);
                msg.append(i + 1).append(". ").append(s.getDate()).append(" à ").append(s.getStartTime())
                   .append(" - ").append(s.getRemainingCapacity()).append(" places\n");
            }
            JOptionPane.showMessageDialog(this, msg.toString(), "Suggestions de Sessions", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ===========================
    // REFRESH AVAILABLE SESSIONS
    // ===========================
    private void refreshAvailableSessions() {
        availableSessions = filterService.getAvailableSessionsForStudent(student);
        filteredSessions = new ArrayList<>(availableSessions);
        
        // Reset filters
        searchField.setText("");
        specializationFilter.setSelectedIndex(0);
        sortComboBox.setSelectedIndex(0);
        
        refreshSessionsDisplay();
    }
}

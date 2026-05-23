package gui.frontend.components;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import gui.frontend.services.SessionFilterService;
import model.Campaign;
import model.Registration;
import model.Session;
import model.Student;
import dao.CampaignDAO;
import dao.RegistrationDAO;

/**
 * Dialog that shows detailed information about a session.
 * Features:
 * - Complete session information
 * - Capacity status and visualization
 * - Register for the session with preference ranking
 * - View alternative session suggestions if full
 * - See if already registered for this session
 * 
 * @author Cédric GUIDI
 * @author Baptiste DUCROCQ
 * @version 1.0
 */
public class SessionDetailsDialog extends JDialog {

    private Session session;
    private Student student;
    private Runnable onRegistrationComplete;
    private RegistrationDAO registrationDAO;
    private SessionFilterService filterService;

    public SessionDetailsDialog(JFrame parent, Student student, Session session, Runnable onRegistrationComplete) {
        super(parent, "Détails de la Session", true);
        this.student = student;
        this.session = session;
        this.onRegistrationComplete = onRegistrationComplete;
        this.registrationDAO = new RegistrationDAO();
        this.filterService = new SessionFilterService();

        setLayout(new BorderLayout());
        setSize(700, 700);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        createContent();
    }

    private void createContent() {
        // Create inner panel with vertical BoxLayout
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        innerPanel.setBackground(new Color(240, 242, 245));
        innerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        // IMPORTANT: Set preferred width to avoid horizontal scrolling
        innerPanel.setPreferredSize(new Dimension(670, 1000));

        // Session Info Section
        innerPanel.add(createSessionInfoSection());
        innerPanel.add(Box.createVerticalStrut(15));

        // Capacity Section
        innerPanel.add(createCapacitySection());
        innerPanel.add(Box.createVerticalStrut(15));

        // Registration Section
        innerPanel.add(createRegistrationSection());
        innerPanel.add(Box.createVerticalStrut(15));

        // Alternative Sessions Section (if full)
        if (session.getRemainingCapacity() <= 0) {
            innerPanel.add(createAlternativesSection());
            innerPanel.add(Box.createVerticalStrut(15));
        }

        // Close button
        JButton closeButton = new JButton("Fermer");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        closeButton.setBackground(new Color(34, 139, 230));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.setMaximumSize(new Dimension(150, 40));
        closeButton.addActionListener(e -> dispose());
        innerPanel.add(closeButton);

        innerPanel.add(Box.createVerticalGlue());

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(innerPanel);
        scrollPane.setBackground(new Color(240, 242, 245));
        scrollPane.getViewport().setBackground(new Color(240, 242, 245));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Add to dialog using BorderLayout
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSessionInfoSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder("Informations de la Session"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        addInfoRow(panel, "Dominante:", session.getSpecializationName());
        addInfoRow(panel, "Campagne:", session.getCampaignName());
        addInfoRow(panel, "Date:", session.getDate().toString());
        addInfoRow(panel, "Horaire:", session.getStartTime() + " - " + session.getEndTime());
        addInfoRow(panel, "Salle:", session.getRoom());
        addInfoRow(panel, "Places totales:", String.valueOf(session.getMaxCapacity()));
        

        return panel;
    }

    private JPanel createCapacitySection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder("État de la Capacité"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        int remaining = session.getRemainingCapacity();
        int max = session.getMaxCapacity();

        JLabel capacityLabel = new JLabel(remaining + " / " + max + " places disponibles");
        capacityLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        capacityLabel.setForeground(remaining > 0 ? new Color(76, 175, 80) : new Color(220, 53, 69));
        panel.add(capacityLabel);

        JProgressBar progressBar = new JProgressBar(0, max);
        progressBar.setValue(max - remaining);
        progressBar.setForeground(remaining > 0 ? new Color(76, 175, 80) : new Color(220, 53, 69));
        progressBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 25));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        progressBar.setString(Math.round(((double)(max - remaining) / max) * 100) + "% remplie");
        progressBar.setStringPainted(true);
        panel.add(Box.createVerticalStrut(10));
        panel.add(progressBar);

        return panel;
    }

    private JPanel createRegistrationSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder("Inscription"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        // Check if already registered
        Registration existingReg = registrationDAO.findBySessionAndStudent(session.getId(), student.getId());

        if (existingReg != null) {
            JLabel alreadyRegisteredLabel = new JLabel("Vous êtes déjà inscrit à cette session (Préférence #" + existingReg.getRank() + ")");
            alreadyRegisteredLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            alreadyRegisteredLabel.setForeground(new Color(76, 175, 80));
            panel.add(alreadyRegisteredLabel);
        } else {
            // ==========================
            // CHECK: Campaign status (ARCHIVED)
            // ==========================
            CampaignDAO campaignDAO = new CampaignDAO();
            Campaign campaign = campaignDAO.get(session.getCampaignId());
            
            if (campaign != null && "ARCHIVED".equals(campaign.getStatus())) {
                JTextArea archivedMsgArea = new JTextArea("Cette campagne est archivée.\nLes inscriptions pour cette campagne ne sont plus possibles.");
                archivedMsgArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                archivedMsgArea.setForeground(new Color(220, 53, 69));
                archivedMsgArea.setBackground(new Color(255, 240, 245));
                archivedMsgArea.setBorder(BorderFactory.createLineBorder(new Color(220, 53, 69)));
                archivedMsgArea.setLineWrap(true);
                archivedMsgArea.setWrapStyleWord(true);
                archivedMsgArea.setEditable(false);
                archivedMsgArea.setOpaque(true);
                archivedMsgArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                panel.add(archivedMsgArea);
            } else {
                // Check for schedule conflicts
                boolean hasConflict = filterService.checkScheduleConflict(student.getId(), session);
            
            if (hasConflict) {
                // Display conflict message
                String conflictMsg = filterService.getConflictMessage(student.getId(), session);
                JTextArea conflictTextArea = new JTextArea(conflictMsg);
                conflictTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                conflictTextArea.setForeground(new Color(220, 53, 69));
                conflictTextArea.setBackground(new Color(255, 240, 245));
                conflictTextArea.setBorder(BorderFactory.createLineBorder(new Color(220, 53, 69)));
                conflictTextArea.setLineWrap(true);
                conflictTextArea.setWrapStyleWord(true);
                conflictTextArea.setEditable(false);
                conflictTextArea.setOpaque(true);
                conflictTextArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
                conflictTextArea.setPreferredSize(new Dimension(600, 80));
                
                panel.add(conflictTextArea);
            } else {
                // Get campaign info for max_choices validation
                ArrayList<Registration> sameCampaignRegs = registrationDAO.findByStudentAndCampaign(student.getId(), session.getCampaignId());
                
                int maxChoices = campaign != null ? campaign.getMaxChoices() : 10;
                int currentChoices = sameCampaignRegs.size();
                
                if (currentChoices >= maxChoices) {
                    // Max choices reached
                    JLabel maxReachedLabel = new JLabel("Vous avez atteint le nombre maximal d'inscriptions pour cette campagne (" + maxChoices + ")");
                    maxReachedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    maxReachedLabel.setForeground(new Color(220, 53, 69));
                    panel.add(maxReachedLabel);
                } else {
                    // Show available ranks
                    ArrayList<Integer> usedRanks = new ArrayList<>();
                    for (Registration reg : sameCampaignRegs) {
                        usedRanks.add(reg.getRank());
                    }
                    
                    // Preference rank input with available options
                    JPanel rankPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    rankPanel.setBackground(Color.WHITE);
                    rankPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                    JLabel rankLabel = new JLabel("Rang de préférence:");
                    rankLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    
                    // Create list of available ranks
                    ArrayList<Integer> availableRanks = new ArrayList<>();
                    for (int i = 1; i <= maxChoices; i++) {
                        if (!usedRanks.contains(i)) {
                            availableRanks.add(i);
                        }
                    }
                    
                    JComboBox<Integer> rankCombo = new JComboBox<>();
                    for (Integer rank : availableRanks) {
                        rankCombo.addItem(rank);
                    }
                    rankCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    
                    rankPanel.add(rankLabel);
                    rankPanel.add(rankCombo);
                    if (!availableRanks.isEmpty()) {
                        rankPanel.add(new JLabel("(" + availableRanks.size() + " rang(s) disponible(s))"));
                    }
                    panel.add(rankPanel);

                    // Register button
                    JButton registerButton = new JButton("S'inscrire");
                    registerButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    registerButton.setBackground(new Color(76, 175, 80));
                    registerButton.setForeground(Color.WHITE);
                    registerButton.setFocusPainted(false);
                    registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    registerButton.addActionListener(e -> {
                        int rank = (Integer) rankCombo.getSelectedItem();
                        registerForSession(rank);
                    });
                    panel.add(Box.createVerticalStrut(10));
                    panel.add(registerButton);
                }
            }
            }
        }

        return panel;
    }

    private JPanel createAlternativesSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder("Sessions Alternatives"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel infoLabel = new JLabel("Cette session est pleine. Voici des suggestions:");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(40, 40, 40));
        panel.add(infoLabel);
        panel.add(Box.createVerticalStrut(10));

        // Get alternative suggestions
        ArrayList<Session> alternatives = filterService.getSuggestionsForFullSession(student, session);

        if (alternatives.isEmpty()) {
            JLabel noAlternativesLabel = new JLabel("Aucune session alternative disponible");
            noAlternativesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            noAlternativesLabel.setForeground(new Color(120, 120, 120));
            panel.add(noAlternativesLabel);
        } else {
            for (Session alt : alternatives) {
                JLabel altLabel = new JLabel(
                    "• " + alt.getSpecializationName() + " - " +
                    alt.getDate() + " (" + alt.getRemainingCapacity() + " places)"
                );
                altLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                altLabel.setForeground(new Color(40, 40, 40));
                panel.add(altLabel);
            }
        }

        return panel;
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setBackground(Color.WHITE);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 11));
        labelComp.setForeground(new Color(40, 40, 40));
        labelComp.setPreferredSize(new Dimension(150, 20));

        JLabel valueComp = new JLabel(value != null ? value : "(null)");
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        valueComp.setForeground(new Color(120, 120, 120));

        row.add(labelComp);
        row.add(valueComp);
        panel.add(row);
    }

    private void registerForSession(int rank) {
        // Check capacity
        if (session.getRemainingCapacity() <= 0) {
            JOptionPane.showMessageDialog(
                this,
                "Cette session est pleine. Veuillez choisir une alternative.",
                "Session Pleine",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Create registration
        Registration registration = new Registration(
            session.getId(),
            student.getId(),
            student.getFirstName() + " " + student.getLastName(),
            student.getEmail(),
            rank,
            "PENDING"
        );

        int result = registrationDAO.add(registration);
        if (result > 0) {
            JOptionPane.showMessageDialog(
                this,
                "Inscription réussie !",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE
            );
            if (onRegistrationComplete != null) {
                onRegistrationComplete.run();
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Erreur lors de l'inscription. Veuillez réessayer.",
                "Erreur",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

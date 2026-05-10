package gui.backoffice.editorFrames;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import model.Campaign;
import model.Registration;
import model.Session;
import model.Student;
import dao.CampaignDAO;
import dao.RegistrationDAO;
import dao.SessionDAO;
import dao.StudentDAO;
import common.components.app.UIStyle;

/**
 * Dialog for validating all registrations of a campaign.
 * - Shows only active (OPEN) and planned (PLANNED) campaigns
 * - Validates registrations while checking for schedule conflicts
 * - Updates registration status to "VALIDATED"
 * 
 * @author Cédric GUIDI && Baptiste DUCROCQ
 * @version 1.0
 */
public class ValidateRegistrationsDialog extends JDialog {

    private JComboBox<String> campaignCombo;
    private JTextArea resultArea;
    private JButton validateButton;
    private JButton closeButton;
    private CampaignDAO campaignDAO;
    private RegistrationDAO registrationDAO;
    private SessionDAO sessionDAO;
    private StudentDAO studentDAO;
    private ArrayList<Campaign> activeCampaigns;
    private Map<String, Integer> campaignMap; // Display name -> ID mapping

    public ValidateRegistrationsDialog(JFrame parent) {
        super(parent, "Valider les inscriptions d'une campagne", true);
        
        this.campaignDAO = new CampaignDAO();
        this.registrationDAO = new RegistrationDAO();
        this.sessionDAO = new SessionDAO();
        this.studentDAO = new StudentDAO();
        this.activeCampaigns = new ArrayList<>();
        this.campaignMap = new HashMap<>();

        setSize(600, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        initComponents();
        loadActiveCampaigns();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 242, 245));

        // ===== TITLE =====
        JLabel title = new JLabel("Validation des inscriptions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(10));

        // ===== CAMPAIGN SELECTION =====
        JPanel campaignPanel = new JPanel();
        campaignPanel.setLayout(new BoxLayout(campaignPanel, BoxLayout.X_AXIS));
        campaignPanel.setOpaque(false);

        JLabel campaignLabel = new JLabel("Sélectionnez une campagne:");
        campaignLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        campaignLabel.setPreferredSize(new Dimension(160, 30));

        campaignCombo = new JComboBox<>();
        UIStyle.styleComboBox(campaignCombo);
        campaignCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        campaignPanel.add(campaignLabel);
        campaignPanel.add(Box.createHorizontalStrut(10));
        campaignPanel.add(campaignCombo);
        mainPanel.add(campaignPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // ===== RESULT AREA =====
        JLabel resultLabel = new JLabel("Résultats de validation:");
        resultLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        resultLabel.setForeground(new Color(100, 100, 100));
        mainPanel.add(resultLabel);
        mainPanel.add(Box.createVerticalStrut(5));

        resultArea = new JTextArea();
        resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBackground(Color.WHITE);
        resultArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(15));

        // ===== BUTTONS =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);

        validateButton = new JButton("Valider les inscriptions");
        UIStyle.stylePrimaryButton(validateButton);
        validateButton.addActionListener(e -> validateCampaignRegistrations());

        closeButton = new JButton("Fermer");
        UIStyle.styleSecondaryButton(closeButton);
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(validateButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(closeButton);
        buttonPanel.add(Box.createHorizontalGlue());

        mainPanel.add(buttonPanel);

        add(mainPanel);
    }

    private void loadActiveCampaigns() {
        campaignCombo.removeAllItems();
        activeCampaigns.clear();
        campaignMap.clear();

        // Get all campaigns and filter active ones
        ArrayList<Campaign> allCampaigns = campaignDAO.getList();
        
        for (Campaign camp : allCampaigns) {
            // Keep only OPEN and PLANNED campaigns
            if ("OPEN".equals(camp.getStatus()) || "PLANNED".equals(camp.getStatus())) {
                activeCampaigns.add(camp);
                String displayName = "Campagne #" + camp.getId() + " (" + camp.getStatus() + ")";
                campaignCombo.addItem(displayName);
                campaignMap.put(displayName, camp.getId());
            }
        }

        if (campaignCombo.getItemCount() == 0) {
            campaignCombo.addItem("Aucune campagne active");
            validateButton.setEnabled(false);
        } else {
            validateButton.setEnabled(true);
        }
    }

    private void validateCampaignRegistrations() {
        String selectedCampaign = (String) campaignCombo.getSelectedItem();
        
        if (selectedCampaign == null || selectedCampaign.equals("Aucune campagne active")) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner une campagne", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer campaignId = campaignMap.get(selectedCampaign);
        if (campaignId == null) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la sélection de la campagne", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get all registrations for this campaign
        ArrayList<Registration> campaignRegistrations = registrationDAO.getList();
        ArrayList<Registration> filteredRegs = new ArrayList<>();

        for (Registration reg : campaignRegistrations) {
            Session session = sessionDAO.get(reg.getSessionId());
            if (session != null && session.getCampaignId() == campaignId) {
                filteredRegs.add(reg);
            }
        }

        if (filteredRegs.isEmpty()) {
            resultArea.setText("Aucune inscription trouvée pour cette campagne.");
            return;
        }

        // Group registrations by student
        Map<Integer, ArrayList<Registration>> regsByStudent = new HashMap<>();
        for (Registration reg : filteredRegs) {
            regsByStudent.computeIfAbsent(reg.getStudentId(), k -> new ArrayList<>())
                         .add(reg);
        }

        // Validate each student's registrations
        StringBuilder report = new StringBuilder();
        int validatedCount = 0;
        int conflictCount = 0;

        for (Map.Entry<Integer, ArrayList<Registration>> entry : regsByStudent.entrySet()) {
            int studentId = entry.getKey();
            ArrayList<Registration> studentRegs = entry.getValue();

            Student student = studentDAO.get(studentId);
            String studentName = (student != null) ? student.getFirstName() + " " + student.getLastName() : "Étudiant #" + studentId;

            // Check for schedule conflicts
            boolean hasConflict = false;
            StringBuilder conflictDetails = new StringBuilder();

            for (int i = 0; i < studentRegs.size() && !hasConflict; i++) {
                Session session1 = sessionDAO.get(studentRegs.get(i).getSessionId());
                if (session1 == null) continue;

                for (int j = i + 1; j < studentRegs.size(); j++) {
                    Session session2 = sessionDAO.get(studentRegs.get(j).getSessionId());
                    if (session2 == null) continue;

                    // Check same day and time overlap
                    if (session1.getDate().equals(session2.getDate())) {
                        if (session1.getStartTime().isBefore(session2.getEndTime()) &&
                            session2.getStartTime().isBefore(session1.getEndTime())) {
                            hasConflict = true;
                            conflictDetails.append(session1.getSpecializationName())
                                          .append(" (").append(session1.getStartTime()).append("-")
                                          .append(session1.getEndTime()).append(") chevauche ")
                                          .append(session2.getSpecializationName());
                            break;
                        }
                    }
                }
            }

            if (hasConflict) {
                report.append(studentName).append(": CONFLIT D'HORAIRE\n");
                report.append("   ").append(conflictDetails).append("\n");
                conflictCount++;
            } else {
                // No conflicts - validate all registrations
                for (Registration reg : studentRegs) {
                    registrationDAO.updateStatus(reg.getStudentId(), reg.getSessionId(), "ACCEPTED");
                    validatedCount++;
                }
                report.append(studentName).append(": ")
                      .append(studentRegs.size()).append(" inscription(s) validée(s)\n");
            }
        }

        report.append("\n");
        report.append("RÉSUMÉ:\n");
        report.append("Étudiants validés: ").append(regsByStudent.size() - conflictCount).append("\n");
        report.append("Étudiants en conflit: ").append(conflictCount).append("\n");
        report.append("Total inscriptions validées: ").append(validatedCount).append("\n");

        resultArea.setText(report.toString());
        resultArea.setCaretPosition(0);

        JOptionPane.showMessageDialog(this,
            "Validation terminée!\n" + validatedCount + " inscription(s) validée(s)",
            "Succès",
            JOptionPane.INFORMATION_MESSAGE);
    }
}

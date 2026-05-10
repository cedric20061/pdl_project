package gui.backoffice.editorFrames;

import java.awt.*;

import javax.swing.*;

import common.components.app.UIStyle;
import dao.CampaignDAO;
import dao.RegistrationDAO;
import dao.SessionDAO;
import dao.SpecializationDAO;
import dao.StudentDAO;
import model.Campaign;
import model.Registration;
import model.Session;
import model.Specialization;
import model.Student;

public class EditRegistrationDialog extends JDialog {

    private RegistrationDAO registrationDao;
    private SessionDAO sessionDao;
    private StudentDAO studentDao;
    private CampaignDAO campaignDao;
    private SpecializationDAO specializationDao;

    private int studentId;
    private int sessionId;
    private Registration registration;

    public EditRegistrationDialog(JFrame parent, int studentId, int sessionId) {
        super(parent, "Éditer l'inscription", true);
        this.studentId = studentId;
        this.sessionId = sessionId;

        registrationDao = new RegistrationDAO();
        sessionDao = new SessionDAO();
        studentDao = new StudentDAO();
        campaignDao = new CampaignDAO();
        specializationDao = new SpecializationDAO();

        registration = registrationDao.findBySessionAndStudent(sessionId, studentId);

        if (registration == null) {
            JOptionPane.showMessageDialog(this, "Inscription non trouvée.", "Erreur", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        initUI();
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        getContentPane().setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Détails de l'inscription");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIStyle.PRIMARY_COLOR);
        add(titleLabel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        Session session = sessionDao.get(sessionId);
        Student student = studentDao.get(studentId);
        Campaign campaign = campaignDao.get(session.getCampaignId());
        Specialization specialization = specializationDao.get(session.getSpecializationId());

        // Student info
        JPanel studentPanel = createInfoPanel("Étudiant", student.getLastName() + " " + student.getFirstName());
        panel.add(studentPanel);
        panel.add(Box.createVerticalStrut(15));

        // Session info
        JPanel sessionPanel = createInfoPanel("Session", session.getDate() + " " + session.getStartTime() + " - " + session.getEndTime() + " (" + session.getRoom() + ")");
        panel.add(sessionPanel);
        panel.add(Box.createVerticalStrut(15));

        // Specialization info
        JPanel specPanel = createInfoPanel("Dominante", specialization.getName());
        panel.add(specPanel);
        panel.add(Box.createVerticalStrut(15));

        // Campaign info
        JPanel campPanel = createInfoPanel("Campagne", "Campagne #" + campaign.getId() + " (" + campaign.getStatus() + ")");
        panel.add(campPanel);
        panel.add(Box.createVerticalStrut(15));

        // Rank info
        JPanel rankPanel = createInfoPanel("Rang", String.valueOf(registration.getRank()));
        panel.add(rankPanel);
        panel.add(Box.createVerticalStrut(15));

        // Status selector
        JPanel statusPanel = createStatusPanel();
        panel.add(statusPanel);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createInfoPanel(String label, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelComp.setForeground(new Color(80, 80, 80));

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        valueComp.setForeground(new Color(100, 100, 100));

        panel.add(labelComp);
        panel.add(valueComp);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel label = new JLabel("Statut de l'inscription");
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(80, 80, 80));
        panel.add(label);

        JComboBox<String> statusCombo = new JComboBox<>(new String[]{
            "PENDING", "ACCEPTED", "REJECTED"
        });
        statusCombo.setSelectedItem(registration.getStatus());
        UIStyle.styleComboBox(statusCombo, 300);
        statusCombo.setMaximumSize(new Dimension(300, 30));
        statusCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(Box.createVerticalStrut(8));
        panel.add(statusCombo);

        // Store reference for later use
        panel.putClientProperty("statusCombo", statusCombo);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);

        JButton cancelButton = new JButton("Annuler");
        cancelButton.setBackground(new Color(200, 200, 200));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setPreferredSize(new Dimension(100, 36));
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = new JButton("Enregistrer");
        UIStyle.stylePrimaryButton(saveButton);
        saveButton.setPreferredSize(new Dimension(120, 36));
        saveButton.addActionListener(e -> saveChanges());

        panel.add(cancelButton);
        panel.add(saveButton);

        return panel;
    }

    private void saveChanges() {
        // Find the status combo
        JPanel contentPanel = null;
        for (Component comp : getContentPane().getComponents()) {
            if (comp instanceof JPanel && comp != getRootPane()) {
                JPanel p = (JPanel) comp;
                for (Component c : p.getComponents()) {
                    if (c instanceof JPanel) {
                        JPanel subPanel = (JPanel) c;
                        if (subPanel.getClientProperty("statusCombo") != null) {
                            contentPanel = subPanel;
                            break;
                        }
                    }
                }
            }
        }

        if (contentPanel != null) {
            JComboBox<?> statusCombo = (JComboBox<?>) contentPanel.getClientProperty("statusCombo");
            String newStatus = (String) statusCombo.getSelectedItem();

            int result = registrationDao.updateStatus(studentId, sessionId, newStatus);
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Inscription mise à jour avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

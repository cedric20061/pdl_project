package gui.frontend.components;

import java.awt.*;
import javax.swing.*;

import common.components.app.UIStyle;
import model.Registration;
import model.Session;
import model.Student;
import dao.RegistrationDAO;

/**
 * Card component that displays a registration summary.
 * Shows:
 * - Preference rank
 * - Session details (specialization, date, time)
 * - Registration status
 * - Action button to view details and modify rank
 * 
 * @author Cédric GUIDI && Baptiste DUCROCQ
 * @version 1.0
 */
public class StudentRegistrationCard extends JPanel {

    private Registration registration;
    private Session session;
    //private Student student;
    private Runnable onModified;

    public StudentRegistrationCard(Student student, Registration registration, Session session, Runnable onModified) {
        //this.student = student;
        this.registration = registration;
        this.session = session;
        this.onModified = onModified;

        setLayout(new BorderLayout(10, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        setPreferredSize(new Dimension(Integer.MAX_VALUE, 110));

        createContent();
    }

    private void createContent() {
        // Left section: Rank badge
        JPanel rankPanel = new JPanel();
        rankPanel.setLayout(new BoxLayout(rankPanel, BoxLayout.Y_AXIS));
        rankPanel.setBackground(Color.WHITE);
        rankPanel.setPreferredSize(new Dimension(60, 100));

        JLabel rankLabel = new JLabel("Préférence #" + registration.getRank());
        rankLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rankLabel.setForeground(UIStyle.PRIMARY_COLOR);
        rankPanel.add(Box.createVerticalGlue());
        rankPanel.add(rankLabel);
        rankPanel.add(Box.createVerticalGlue());

        add(rankPanel, BorderLayout.WEST);

        // Center section: Session info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        if (session != null) {
            // Specialization and Campaign
            JLabel specLabel = new JLabel(session.getSpecializationName() + " - " + session.getCampaignName());
            specLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            specLabel.setForeground(UIStyle.TEXT_COLOR);
            infoPanel.add(specLabel);

            // Date and Time
            JLabel dateTimeLabel = new JLabel(
                session.getDate().toString() + " | " +
                session.getStartTime() + " - " + session.getEndTime()
            );
            dateTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            dateTimeLabel.setForeground(new Color(120, 120, 120));
            infoPanel.add(dateTimeLabel);

            // Room
            JLabel roomLabel = new JLabel("Salle: " + session.getRoom());
            roomLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            roomLabel.setForeground(new Color(120, 120, 120));
            infoPanel.add(roomLabel);
        } else {
            JLabel noSessionLabel = new JLabel("Session non trouvée (ID: " + registration.getSessionId() + ")");
            noSessionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            noSessionLabel.setForeground(UIStyle.DANGER_COLOR);
            infoPanel.add(noSessionLabel);
        }

        add(infoPanel, BorderLayout.CENTER);

        // Right section: Status and actions
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);

        // Status badge
        JLabel statusLabel = new JLabel(getStatusText());
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(getStatusColor());
        rightPanel.add(statusLabel);

        // Action button
        JButton cancelButton = new JButton("Annuler cette inscription");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cancelButton.setBackground(UIStyle.DANGER_COLOR);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        cancelButton.addActionListener(e -> cancelRegistration());
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(cancelButton);

        add(rightPanel, BorderLayout.EAST);
    }

    private String getStatusText() {
        switch (registration.getStatus()) {
            case "CONFIRMED":
                return "✓ Confirmée";
            case "PENDING":
                return "⏳ En attente";
            case "REJECTED":
                return "✗ Rejetée";
            default:
                return registration.getStatus();
        }
    }

    private Color getStatusColor() {
        switch (registration.getStatus()) {
            case "CONFIRMED":
                return UIStyle.SUCCESS_COLOR;
            case "PENDING":
                return new Color(255, 165, 0); // Orange
            case "REJECTED":
                return UIStyle.DANGER_COLOR;
            default:
                return UIStyle.SECONDARY_COLOR;
        }
    }

    private void cancelRegistration() {
        int result = JOptionPane.showConfirmDialog(
            SwingUtilities.getWindowAncestor(this),
            "Êtes-vous sûr de vouloir annuler cette inscription ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            RegistrationDAO dao = new RegistrationDAO();
            dao.deleteRegistration(registration.getSessionId(), registration.getStudentId());
            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(this),
                "Inscription annulée avec succès",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE
            );
            if (onModified != null) {
                onModified.run();
            }
        }
    }
}

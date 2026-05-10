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
    private Runnable onModified;

    public StudentRegistrationCard(Student student, Registration registration, Session session, Runnable onModified) {
        this.registration = registration;
        this.session = session;
        this.onModified = onModified;

        setLayout(new BorderLayout(15, 0));
        setBackground(Color.WHITE);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        createContent();
    }

    private void createContent() {
        add(createRankBadge(), BorderLayout.WEST);
        add(createInfoPanel(), BorderLayout.CENTER);
        add(createRightPanel(), BorderLayout.EAST);
    }

    // ===========================
    // 🎯 RANK BADGE (beaucoup plus visuel)
    // ===========================
    private JPanel createRankBadge() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(60, 80));

        JLabel badge = new JLabel("#" + registration.getRank(), SwingConstants.CENTER);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 14));
        badge.setForeground(Color.WHITE);
        badge.setOpaque(true);
        badge.setBackground(UIStyle.PRIMARY_COLOR);
        badge.setPreferredSize(new Dimension(40, 40));

        panel.add(badge);
        return panel;
    }

    // ===========================
    // 📄 INFOS
    // ===========================
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        if (session != null) {
            JLabel title = new JLabel(session.getSpecializationName());
            title.setFont(new Font("Segoe UI", Font.BOLD, 14));
            title.setForeground(UIStyle.TEXT_COLOR);

            JLabel campaign = new JLabel(session.getCampaignName());
            campaign.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            campaign.setForeground(new Color(120, 120, 120));

            JLabel datetime = new JLabel(
                    session.getDate() + " • " +
                    session.getStartTime() + " - " + session.getEndTime()
            );
            datetime.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            datetime.setForeground(new Color(100, 100, 100));

            JLabel room = new JLabel("Salle " + session.getRoom());
            room.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            room.setForeground(new Color(100, 100, 100));

            panel.add(title);
            panel.add(Box.createVerticalStrut(2));
            panel.add(campaign);
            panel.add(Box.createVerticalStrut(6));
            panel.add(datetime);
            panel.add(room);

        } else {
            JLabel error = new JLabel("Session introuvable");
            error.setForeground(UIStyle.DANGER_COLOR);
            panel.add(error);
        }

        return panel;
    }

    // ===========================
    // 👉 RIGHT PANEL
    // ===========================
    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(createStatusBadge());
        panel.add(Box.createVerticalGlue());
        panel.add(createCancelButton());

        return panel;
    }

    // ===========================
    // 🟢 STATUS BADGE
    // ===========================
    private JLabel createStatusBadge() {
        JLabel status = new JLabel(getStatusText(), SwingConstants.CENTER);
        status.setFont(new Font("Segoe UI", Font.BOLD, 11));
        status.setOpaque(true);
        status.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        Color bg = getStatusColor();
        status.setBackground(bg);
        status.setForeground(Color.WHITE);

        return status;
    }

    private JButton createCancelButton() {
        JButton btn = new JButton("Annuler");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setForeground(UIStyle.DANGER_COLOR);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(UIStyle.DANGER_COLOR));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Disable cancel button if status is ACCEPTED
        if ("ACCEPTED".equals(registration.getStatus())) {
            btn.setEnabled(false);
            btn.setForeground(new Color(150, 150, 150));
            btn.setText("Validée");
            btn.setToolTipText("Cette inscription est validée et ne peut pas être annulée");
        } else {
            btn.addActionListener(e -> cancelRegistration());
        }

        return btn;
    }

    // ===========================
    // 🎨 STATUS LOGIC
    // ===========================
    private String getStatusText() {
        switch (registration.getStatus()) {
            case "CONFIRMED": return "Confirmée";
            case "PENDING": return "En attente";
            case "REJECTED": return "Rejetée";
            case "ACCEPTED": return "Acceptée";
            
            default: return registration.getStatus();
        }
    }

    private Color getStatusColor() {
        switch (registration.getStatus()) {
            case "CONFIRMED": return UIStyle.SUCCESS_COLOR;
            case "PENDING": return new Color(255, 165, 0);
            case "REJECTED": return UIStyle.DANGER_COLOR;
            case "ACCEPTED": return UIStyle.SUCCESS_COLOR;  // Darker green for accepted
            default: return UIStyle.SECONDARY_COLOR;
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
package gui.frontend.components;

import java.awt.*;
import javax.swing.*;

import dao.SpecializationDAO;
import model.Session;
import model.Specialization;
import model.Student;

public class SessionCard extends JPanel {

    private final Session session;
    private final SessionCardListener listener;

    // 🎨 Palette centralisée
    private static final Color BG_DEFAULT = Color.WHITE;
    private static final Color BG_HOVER = new Color(248, 249, 250);
    private static final Color BORDER_COLOR = new Color(220, 220, 220);
    private static final Color TEXT_PRIMARY = new Color(40, 40, 40);
    private static final Color TEXT_SECONDARY = new Color(100, 100, 100);
    private static final Color BLUE = new Color(34, 139, 230);
    private static final Color ORANGE = new Color(235, 125, 52);
    private static final Color RED = new Color(220, 53, 69);

    public interface SessionCardListener {
        void onSessionAction(String action, Session session);
    }

    public SessionCard(Session session, Student student, SessionCardListener listener) {
        this.session = session;
        this.listener = listener;
        initUI();
        add(createInfoPanel(), BorderLayout.CENTER);
        add(createRightPanel(), BorderLayout.EAST);
    }

    private void initUI() {
        setLayout(new BorderLayout(12, 0));
        setBackground(BG_DEFAULT);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        //setPreferredSize(new Dimension(0, 140));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        // Hover effect
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(BG_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(BG_DEFAULT);
            }
        });
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(createTitle());
        panel.add(Box.createVerticalStrut(4));
        panel.add(createDescription());
        panel.add(Box.createVerticalStrut(8));
        panel.add(createDetailsRow());
        panel.add(Box.createVerticalStrut(10));
        panel.add(createCapacityBar());
        panel.add(Box.createVerticalStrut(5));
        panel.add(createProfessor());

        return panel;
    }

    private JLabel createTitle() {
        JLabel label = new JLabel(session.getSpecializationName());
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private JLabel createDescription() {
        // ⚠️ à remplacer par session.getDescription()
        JLabel label = new JLabel("Introduction au Machine Learning et aux réseaux de neurones");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    private JPanel createDetailsRow() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);

        panel.add(createDetailLabel(session.getDate().toString()));
        panel.add(spacer());
        panel.add(createDetailLabel(session.getStartTime() + "-" + session.getEndTime()));
        panel.add(spacer());
        panel.add(createDetailLabel(session.getRoom()));
        panel.add(spacer());

        String capacityText = (session.getMaxCapacity() - session.getRemainingCapacity())
                + "/" + session.getMaxCapacity() + " inscrits";

        panel.add(createDetailLabel(capacityText));

        return panel;
    }

    private JLabel createDetailLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(new Color(80, 80, 80));
        return label;
    }

    private Component spacer() {
        return Box.createHorizontalStrut(20);
    }

    private JPanel createCapacityBar() {
        int remaining = session.getRemainingCapacity();
        int max = session.getMaxCapacity();
        int occupied = max - remaining;

        double percentage = max == 0 ? 0 : (double) occupied / max * 100;

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);

        JProgressBar bar = new JProgressBar(0, max);
        bar.setValue(occupied);
        bar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 6));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
        bar.setBorder(BorderFactory.createEmptyBorder());

        // 🎯 couleur intelligente
        if (remaining == 0) {
            bar.setForeground(RED);
        } else if (percentage > 70) {
            bar.setForeground(ORANGE);
        } else {
            bar.setForeground(BLUE);
        }

        bar.setBackground(new Color(235, 235, 235));

        JLabel percent = new JLabel(String.format("%.0f%%", percentage));
        percent.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        percent.setForeground(TEXT_SECONDARY);
        percent.setPreferredSize(new Dimension(35, 16));

        panel.add(bar);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(percent);

        return panel;
    }

    private JLabel createProfessor() {
        SpecializationDAO specDAO = new SpecializationDAO();
        Specialization spec = specDAO.get(session.getSpecializationId());
        JLabel label = new JLabel("Par "+ spec.getHandleBy());
        label.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        label.setForeground(new Color(120, 120, 120));
        return label;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JButton button = new JButton("+ Ajouter");
        styleButton(button);

        button.addActionListener(e ->
                listener.onSessionAction("DETAILS", session)
        );

        panel.add(button);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBackground(BLUE);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
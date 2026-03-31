package gui.backoffice.mainPanels;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import common.components.app.UIStyle;
import service.AppSession;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(new EmptyBorder(20, 20, 20, 20));
        this.setBackground(Color.WHITE);

        createHeader();
        this.add(Box.createVerticalStrut(20));
        createStatsCards();
        this.add(Box.createVerticalStrut(20));
        createQuickActions();
        this.add(Box.createVerticalStrut(20));
        createRecentActivity();
    }

    // ==========================
    // HEADER
    // ==========================
    private void createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);

        JLabel title = new JLabel("Tableau de bord");
        UIStyle.styleHeaderLabel(title);

        JLabel welcome = new JLabel("Bienvenue, "+ AppSession.getInstance().getUser().getFirstName() + " !");
        UIStyle.styleSubHeaderLabel(welcome);

        header.add(title, BorderLayout.WEST);
        header.add(welcome, BorderLayout.SOUTH);

        this.add(header);
    }

    // ==========================
    // STATISTIQUES
    // ==========================
    private void createStatsCards() {
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsPanel.setBackground(Color.WHITE);

        cardsPanel.add(createStatCard("Étudiants", 120, new Color(66, 133, 244)));
        cardsPanel.add(createStatCard("Sessions", 15, new Color(52, 168, 83)));
        cardsPanel.add(createStatCard("Départements", 5, new Color(251, 188, 5)));
        cardsPanel.add(createStatCard("Dominantes", 10, new Color(234, 67, 53)));

        this.add(cardsPanel);
    }

    private JPanel createStatCard(String title, int value, Color color) {
        JPanel card = new JPanel();
        card.setBackground(color);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lblValue = new JLabel(String.valueOf(value));
        lblValue.setForeground(Color.WHITE);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(lblValue);

        return card;
    }

    // ==========================
    // ACTIONS RAPIDES
    // ==========================
    private void createQuickActions() {
        JPanel actionPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        actionPanel.setBackground(Color.WHITE);

        actionPanel.add(createActionButton("Départements"));
        actionPanel.add(createActionButton("Dominantes"));
        actionPanel.add(createActionButton("Sessions"));
        actionPanel.add(createActionButton("Inscriptions"));

        this.add(actionPanel);
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        UIStyle.styleSecondaryButton(button);
        return button;
    }

    // ==========================
    // ACTIVITÉS RÉCENTES
    // ==========================
    private void createRecentActivity() {
        JPanel recentPanel = new JPanel();
        recentPanel.setLayout(new BorderLayout());
        recentPanel.setBackground(Color.WHITE);
        recentPanel.setBorder(BorderFactory.createTitledBorder("Dernières inscriptions"));

        String[] columns = {"ID", "Étudiant", "Session", "Statut"};
        Object[][] data = {
            {1, "Alice Dupont", "Session 1", "Confirmée"},
            {2, "Bob Martin", "Session 2", "En attente"},
            {3, "Claire Moreau", "Session 3", "Confirmée"},
            {4, "David Petit", "Session 1", "Annulée"}
        };

        JTable table = new JTable(data, columns);
        UIStyle.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);

        recentPanel.add(scroll, BorderLayout.CENTER);
        this.add(recentPanel);
    }

    // ==========================
    // MAIN POUR TEST
    // ==========================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setContentPane(new DashboardPanel());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
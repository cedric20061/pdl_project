package gui.backoffice.mainPanels;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import common.components.app.UIStyle;
import service.AppSession;
import dao.StudentDAO;
import dao.SessionDAO;
import dao.DepartmentDAO;
import dao.SpecializationDAO;
import dao.RegistrationDAO;
import model.Student;
import model.Session;
import model.Registration;

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

        // Get real data from database
        StudentDAO studentDAO = new StudentDAO();
        SessionDAO sessionDAO = new SessionDAO();
        DepartmentDAO departmentDAO = new DepartmentDAO();
        SpecializationDAO specializationDAO = new SpecializationDAO();

        int studentCount = studentDAO.getList().size();
        int sessionCount = sessionDAO.getList().size();
        int departmentCount = departmentDAO.getList().size();
        int specializationCount = specializationDAO.getList().size();

        cardsPanel.add(createStatCard("Étudiants", studentCount, new Color(66, 133, 244)));
        cardsPanel.add(createStatCard("Sessions", sessionCount, new Color(52, 168, 83)));
        cardsPanel.add(createStatCard("Départements", departmentCount, new Color(251, 188, 5)));
        cardsPanel.add(createStatCard("Dominantes", specializationCount, new Color(234, 67, 53)));

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

        String[] columns = {"ID", "Étudiant", "Session", "Rang", "Statut"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        // Get real data from database
        RegistrationDAO registrationDAO = new RegistrationDAO();
        StudentDAO studentDAO = new StudentDAO();
        SessionDAO sessionDAO = new SessionDAO();

        ArrayList<Registration> registrations = registrationDAO.getList();
        
        // Show only the 10 most recent registrations (sorted by ID descending)
        int limit = Math.min(10, registrations.size());
        for (int i = Math.max(0, registrations.size() - limit); i < registrations.size(); i++) {
            Registration reg = registrations.get(i);
            Session session = sessionDAO.get(reg.getSessionId());
            Student student = studentDAO.get(reg.getStudentId());

            String sessionName = (session != null) ? session.getSpecializationName() : "Session #" + reg.getSessionId();
            String studentName = (student != null) ? student.getFirstName() + " " + student.getLastName() : "Étudiant #" + reg.getStudentId();

            String statusDisplay = getStatusDisplay(reg.getStatus());

            model.addRow(new Object[]{
                reg.getSessionId(),
                studentName,
                sessionName,
                reg.getRank(),
                statusDisplay
            });
        }

        JTable table = new JTable(model);
        UIStyle.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);

        recentPanel.add(scroll, BorderLayout.CENTER);
        this.add(recentPanel);
    }

    private String getStatusDisplay(String status) {
        switch (status) {
            case "PENDING": return "En attente";
            case "REJECTED": return "Rejetée";
            case "ACCEPTED": return "Validée";
            default: return status;
        }
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
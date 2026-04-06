package gui.frontend;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gui.frontend.mainPanels.StudentRegistrationsPanel;
import gui.frontend.mainPanels.StudentSearchSessionPanel;
import gui.frontend.components.StudentSidebarButton;
import model.Student;
import service.AppSession;

/**
 * Main window for the student frontend interface.
 * Allows students to:
 * - Search and browse available sessions from active campaigns
 * - Register for sessions with preference ranking
 * - View and manage their registrations
 * - Filter sessions by specialization, campaign, capacity
 * - Get suggestions for sessions when preferred are full
 * 
 * @author Cédric GUIDI && Baptiste DUCROCQ
 * @version 1.0
 */
public class StudentFrontendMain extends JFrame {

    private Container container;
    private JPanel pages;
    private CardLayout cardLayout;
    private Student currentStudent;

    public static void main(String[] args) {
        // Note: In actual app, this would be called after login
        new StudentFrontendMain();
    }

    public StudentFrontendMain() {
        Student current = (Student) AppSession.getInstance().getUser();
        this.currentStudent = current;
        
        setTitle("Ent-Wish - Student Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        container = this.getContentPane();
        container.setLayout(new BorderLayout());

        createHeader();
        createPages();

        this.setVisible(true);
    }

    // ===========================
    // PAGE CREATION
    // ===========================
    private void createPages() {
        cardLayout = new CardLayout();
        pages = new JPanel(cardLayout);

        StudentSearchSessionPanel searchPanel = new StudentSearchSessionPanel(currentStudent);
        StudentRegistrationsPanel registrationsPanel = new StudentRegistrationsPanel(currentStudent);

        pages.add(searchPanel, "search");
        pages.add(registrationsPanel, "registrations");
        
        container.add(pages, BorderLayout.CENTER);
        
        // Show search page by default
        cardLayout.show(pages, "search");
    }

    // ===========================
    // HEADER CREATION
    // ===========================
    private void createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());
        header.setBackground(new Color(34, 139, 230)); // Bleu primaire
        header.setPreferredSize(new Dimension(1200, 70));
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // LEFT: Logo and Title
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Ent-Wish");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        leftPanel.add(titleLabel);
        header.add(leftPanel, BorderLayout.WEST);

        // CENTER: Navigation Buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
        centerPanel.setOpaque(false);

        // Search button
        StudentSidebarButton searchBtn = new StudentSidebarButton(
            "Rechercher Sessions",
            e -> cardLayout.show(pages, "search")
        );

        // Registrations button
        StudentSidebarButton registrationsBtn = new StudentSidebarButton(
            "Mes Inscriptions",
            e -> cardLayout.show(pages, "registrations")
        );

        centerPanel.add(searchBtn);
        centerPanel.add(Box.createHorizontalStrut(15));
        centerPanel.add(registrationsBtn);

        header.add(centerPanel, BorderLayout.CENTER);

        // RIGHT: User Info
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));
        rightPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel(String.format("Bienvenue, %s", currentStudent.getFirstName()));
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        welcomeLabel.setForeground(Color.WHITE);

        JLabel infoLabel = new JLabel(String.format("| Promo: %d • Niveau: %s", currentStudent.getPromotion(), currentStudent.getLevel()));
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(200, 220, 255));

        rightPanel.add(welcomeLabel);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(infoLabel);

        header.add(rightPanel, BorderLayout.EAST);

        container.add(header, BorderLayout.NORTH);
    }
}

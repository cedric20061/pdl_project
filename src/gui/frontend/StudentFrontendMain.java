package gui.frontend;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import gui.frontend.mainPanels.StudentRegistrationsPanel;
import gui.frontend.mainPanels.StudentSearchSessionPanel;
import gui.frontend.components.HeaderNavButton;
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
 * @author Cédric GUIDI
 * @author Baptiste DUCROCQ
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
    private void createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        header.setPreferredSize(new Dimension(1200, 65));

        // ===========================
        // LEFT : LOGO / TITLE
        // ===========================
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));
        left.setOpaque(false);

        JLabel title = new JLabel("Ent-Wish");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        left.add(title);

        // petit séparateur visuel
        left.add(Box.createHorizontalStrut(20));
        left.add(createDivider());

        header.add(left, BorderLayout.WEST);

        // ===========================
        // CENTER : NAVIGATION (style onglets)
        // ===========================
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        nav.setOpaque(false);

        // Boutons avec icônes
        HeaderNavButton searchBtn = new HeaderNavButton(
            "Rechercher",
            "/icons/search.png",
            "search"
        );

        HeaderNavButton registrationsBtn = new HeaderNavButton(
            "Inscriptions",
            "/icons/registration.png",
            "registrations"
        );

        // Ajout
        HeaderNavButton[] buttons = { searchBtn, registrationsBtn };

        for (HeaderNavButton btn : buttons) {
            nav.add(btn);
        }

        header.add(nav, BorderLayout.CENTER);

        final HeaderNavButton[] activeButton = { searchBtn };
        activeButton[0].setActive(true);

        for (HeaderNavButton btn : buttons) {
            btn.addActionListener(e -> {
                cardLayout.show(pages, btn.getPageName());

                activeButton[0].setActive(false);
                btn.setActive(true);
                activeButton[0] = btn;
            });
        }
                // ===========================
        // RIGHT : USER INFO
        // ===========================
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.X_AXIS));
        right.setOpaque(false);

        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setOpaque(false);

        JLabel welcome = new JLabel("Bonjour, " + currentStudent.getFirstName());
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 12));
        welcome.setForeground(Color.WHITE);

        JLabel details = new JLabel(
            "Promo " + currentStudent.getPromotion() + " • " + currentStudent.getLevel()
        );
        details.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        details.setForeground(new Color(200, 220, 255));

        userInfo.add(welcome);
        userInfo.add(details);

        right.add(userInfo);
        right.add(Box.createHorizontalStrut(15));

        // bouton déconnexion (optionnel mais UX ++)
        // JButton logout = new JButton("⎋");
        // logout.setForeground(Color.WHITE);
        // logout.setBackground(new Color(255, 255, 255, 40));
        // logout.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));


        // right.add(logout);

        header.add(right, BorderLayout.EAST);

        container.add(header, BorderLayout.NORTH);
    }

    private JComponent createDivider() {
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setForeground(new Color(255, 255, 255, 80));
        sep.setPreferredSize(new Dimension(1, 20));
        return sep;
    }
}

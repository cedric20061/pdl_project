package gui.backoffice;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gui.backoffice.components.SidebarButton;
import gui.backoffice.mainPanels.CampaignPanel;
import gui.backoffice.mainPanels.DashboardPanel;
import gui.backoffice.mainPanels.DepartmentPanel;
import gui.backoffice.mainPanels.RegistrationPanel;
import gui.backoffice.mainPanels.SessionPanel;
import gui.backoffice.mainPanels.SpecializationPanel;
import common.components.app.IconUtils;

public class Main extends JFrame {

    private Container container;

    private JPanel pages;          // contient toutes les pages
    private CardLayout cardLayout; // gestionnaire de navigation

    public static void main() {
        new Main();
    }

    public Main() {

        setTitle("Ent-Wish");

        container = this.getContentPane();
        container.setLayout(new BorderLayout());

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1300, 600);
        this.setLocationRelativeTo(null);

        createPages();
        createSidebar();

        this.setVisible(true);
    }

    // -------------------------
    // Création des pages
    // -------------------------
    private void createPages() {

        cardLayout = new CardLayout();
        pages = new JPanel(cardLayout);

        DashboardPanel dashboardPnl = new DashboardPanel();
        DepartmentPanel departmentPnl = new DepartmentPanel();
        CampaignPanel campaignPnl = new CampaignPanel();
        SpecializationPanel specializationPnl = new SpecializationPanel();
        SessionPanel sessionPnl = new SessionPanel();
        RegistrationPanel registrationPnl = new RegistrationPanel();

        pages.add(dashboardPnl, "home");
        pages.add(departmentPnl, "departments");
        pages.add(campaignPnl, "campaigns");
        pages.add(specializationPnl, "specializations");
        pages.add(sessionPnl, "sessions");
        pages.add(registrationPnl, "registrations");
        container.add(pages, BorderLayout.CENTER);
    }

    // -------------------------
    // Sidebar
    // -------------------------
    private void createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(new Color(44, 62, 80));

        JLabel logo = new JLabel();
        logo.setIcon(IconUtils.load("/icons/logo.png", 180, 50));

        logoPanel.add(logo);

        // IMPORTANT : éviter qu'il s'étire verticalement
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        sidebar.add(logoPanel);
        sidebar.add(Box.createVerticalStrut(20));

        // Création des boutons sidebar
        SidebarButton homeBtn = new SidebarButton("Accueil", "/icons/home.png", "home");
        SidebarButton specializationBtn = new SidebarButton("Dominantes", "/icons/specialization.png", "specializations");
        SidebarButton sessionBtn = new SidebarButton("Sessions", "/icons/session.png", "sessions");
        SidebarButton campaignBtn = new SidebarButton("Campagnes", "/icons/campaign.png", "campaigns");
        SidebarButton departmentBtn = new SidebarButton("Départements", "/icons/department.png", "departments");
        SidebarButton registrationBtn = new SidebarButton("Inscriptions", "/icons/registration.png", "registrations");
        //SidebarButton statisticsBtn = new SidebarButton("Statistiques", "/icons/statistic.png", "statistics");

        // Ajout à la sidebar avec spacing
        SidebarButton[] buttons = { homeBtn, departmentBtn, specializationBtn, sessionBtn, campaignBtn, registrationBtn };
        for (SidebarButton btn : buttons) {
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(10));
        }

        // Ajout sidebar à la fenêtre principale
        container.add(sidebar, BorderLayout.WEST);

        // ===============================
        // Gestion de la page active via CardLayout
        // ===============================
        final SidebarButton[] activeButton = { homeBtn }; // tableau d’un seul élément
        activeButton[0].setActive(true);

        for (SidebarButton btn : buttons) {
            btn.addActionListener(e -> {
                // Affiche la page correspondante
                cardLayout.show(pages, btn.getPageName());

                // Mets à jour l'état actif
                activeButton[0].setActive(false);
                btn.setActive(true);
                activeButton[0] = btn; // mise à jour du bouton actif
            });
        }
    }

}
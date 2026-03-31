package gui.backoffice.mainPanels;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import gui.backoffice.editorFrames.CreateRegistration;
import gui.backoffice.utils.PanelsUtils;
import model.Registration;
import model.Session;
import common.components.app.ButtonEditor;
import common.components.app.ButtonRenderer;
import common.components.app.UIStyle;
import dao.CampaignDAO;
import dao.RegistrationDAO;
import dao.SessionDAO;

public class RegistrationPanel extends JPanel {

    private JTable table;
    private JTextField searchField;
    private JComboBox<String> sessionFilter;
    private JComboBox<String> campaignFilter;
    private JLabel countLabel;
    private RegistrationDAO registrationDao;

    public RegistrationPanel() {
        registrationDao = new RegistrationDAO(); // DAO pour gérer les inscriptions
        ArrayList<Registration> registrations = registrationDao.getList(); // Récupérer les inscriptions depuis la DB

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        PanelsUtils.createHeader(
            "Inscriptions",
            "Voir et ajouter des inscriptions aux sessions",
            this,
            e -> new CreateRegistration(table) // Formulaire d'ajout
        );

        this.add(Box.createVerticalStrut(15));
        createToolbar(registrations);
        this.add(Box.createVerticalStrut(15));
        createTable(registrations);
    }

    // ==========================
    // TOOLBAR
    // ==========================
    private void createToolbar(ArrayList<Registration> registrations) {

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        wrapper.setOpaque(false);

        JPanel card = new JPanel(new BorderLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Filtres");
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        border.setTitleColor(new Color(120,120,120));
        card.setBorder(border);
        card.setBackground(Color.WHITE);

        JPanel content = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        content.setOpaque(false);

        // 🔍 Recherche étudiant
        searchField = new JTextField();
        UIStyle.styleTextField(searchField, 200, 30);
        content.add(new JLabel("Recherche étudiant"));
        content.add(searchField);

        // 🏫 Filtre session
        sessionFilter = new JComboBox<>();
        sessionFilter.addItem("ALL");
        SessionDAO sessionDAO = new SessionDAO();
        sessionDAO.getList().forEach(session -> {
            // ajouter les sessions à la combo box
            sessionFilter.addItem(session.toString());
        });
        
        UIStyle.styleComboBox(sessionFilter, 180);
        content.add(new JLabel("Session"));
        content.add(sessionFilter);

        // 📢 Filtre campagne
        campaignFilter = new JComboBox<>();
        campaignFilter.addItem("ALL");
        CampaignDAO campaignDAO = new CampaignDAO();
        campaignDAO.getList().forEach(campaign -> {
            // ajouter les campagnes à la combo box
            campaignFilter.addItem(campaign.toString());
        });
        UIStyle.styleComboBox(campaignFilter, 180);
        content.add(new JLabel("Campagne"));
        content.add(campaignFilter);

        // 🔘 Bouton Filtrer
        JButton searchButton = new JButton("Filtrer");
        UIStyle.styleFilterButton(searchButton);
        content.add(searchButton);

        // 📊 Compteur
        countLabel = new JLabel(registrations.size() + " inscription(s)");
        UIStyle.styleSmallLabel(countLabel);
        countLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));

        card.add(content, BorderLayout.CENTER);
        card.add(countLabel, BorderLayout.SOUTH);

        wrapper.add(card);
        this.add(wrapper);

        searchButton.addActionListener(e -> searchRegistrations(registrations ));
    }

    // ==========================
    // TABLE
    // ==========================
    private void createTable(ArrayList<Registration> registrations) {

        String[] colonnes = {
            "SessionID", "StudentID", "Nom étudiant", "Email", "Rang", "Statut", "Actions"
        };

        DefaultTableModel model = new DefaultTableModel(colonnes, 0);

        for (Registration registration : registrations) {
            model.addRow(new Object[]{
                registration.getSessionId(),
                registration.getStudentId(),
                registration.getStudentName(),
                registration.getStudentEmail(),
                registration.getRank(),
                registration.getStatus(),
                "Actions" // Actions column
            });
        }

        table = new JTable(model);
        UIStyle.styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        wrapper.add(scrollPane);

        this.add(wrapper);

        // Actions
        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(
            new ButtonEditor(
                new JCheckBox(),
                null,
                row -> {
                    // seulement suppression possible
                    int session_id = (int) table.getValueAt(row, 0);
                    int student_id = (int) table.getValueAt(row, 1);
                    String name = (String) table.getValueAt(row, 2);

                    int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Supprimer l'inscription de : " + name + " ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        // supprimer de la DB
                        int isDelete = registrationDao.delete(student_id, session_id);
                        if(isDelete > 0) {
                            JOptionPane.showMessageDialog(null, "Inscription supprimée avec succès.");
                            ((DefaultTableModel) table.getModel()).removeRow(row);
                            countLabel.setText(table.getRowCount() + " inscription(s)");
                        } else {
                            JOptionPane.showMessageDialog(null, "Erreur lors de la suppression.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            )
        );

        // cacher SessionID et StudentID
        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setWidth(0);

        table.getColumnModel().getColumn(2).setMinWidth(0);
        table.getColumnModel().getColumn(2).setMaxWidth(0);
        table.getColumnModel().getColumn(2).setWidth(0);

        countLabel.setText(model.getRowCount() + " inscription(s)");
    }

    // ==========================
    // FILTRE
    // ==========================
    private void searchRegistrations(ArrayList<Registration> registrations) {

        String keyword = searchField.getText().trim().toLowerCase();
        String selectedSession = (String) sessionFilter.getSelectedItem();
        String selectedCamp = (String) campaignFilter.getSelectedItem();

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (Registration registration : registrations) {

            String studentName = registration.getStudentName().toLowerCase();
            String studentEmail = registration.getStudentEmail().toLowerCase();
            int sessionId = registration.getSessionId();

            int selectedSessionId = -1;
            if (!selectedSession.equals("ALL")) {
                String[] parts = selectedSession.split(" - ");
                if (parts.length > 1) {
                    try {
                        selectedSessionId = Integer.parseInt(parts[1].trim());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }

            SessionDAO sessionDAO = new SessionDAO();
            Session session = sessionDAO.get(sessionId);
            int campId = session.getCampaignId();
            int selectedCampId = -1;
            if (!selectedCamp.equals("ALL")) {
                String[] parts = selectedCamp.split(" - ");
                if (parts.length > 1) {
                    try {
                        selectedCampId = Integer.parseInt(parts[1].trim());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
            boolean matchKeyword = studentName.contains(keyword) || studentEmail.contains(keyword);
            boolean matchSession = selectedSession.equals("ALL") || sessionId == selectedSessionId;
            boolean matchCamp = selectedCamp.equals("ALL") || campId == selectedCampId;

            if (matchKeyword && matchSession && matchCamp) {
                model.addRow(new Object[]{
                registration.getSessionId(),
                registration.getStudentId(),
                registration.getStudentName(),
                registration.getStudentEmail(),
                registration.getRank(),
                registration.getStatus(),
                "Actions" // Actions column
            });
            }
        }

        countLabel.setText(model.getRowCount() + " inscription(s)");
    }
}
package gui.backoffice.mainPanels;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import gui.backoffice.components.StatusRenderer;
import gui.backoffice.editorFrames.CreateOrEditCampaign;
import gui.backoffice.utils.PanelsUtils;
import common.components.app.ButtonEditor;
import common.components.app.ButtonRenderer;
import common.components.app.UIStyle;
import dao.CampaignDAO;
import model.Campaign;


public class CampaignPanel extends JPanel {

    private JTable table;
    private JTextField searchField;
    private JLabel countLabel;
    private JComboBox<String> statusCombo;
    private com.toedter.calendar.JDateChooser fromDate;
    private com.toedter.calendar.JDateChooser toDate;
    private CampaignDAO campDAO;

    public CampaignPanel() {
        campDAO = new CampaignDAO();
        ArrayList<Campaign> campaigns = campDAO.getList();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        PanelsUtils.createHeader("Campagnes", 
        "Créer et gérer les campagnes disponibles", 
                    this, 
                    e->{
                        new CreateOrEditCampaign(null, table);
                    }
            );
        this.add(Box.createVerticalStrut(15));
        createToolbar(campaigns);
        this.add(Box.createVerticalStrut(15));
        createTable(campaigns);
    }

    private void createToolbar(ArrayList<Campaign> campaigns) {

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        wrapper.setOpaque(false);

        JPanel card = new JPanel(new BorderLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Filtres");
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        border.setTitleColor(new Color(120,120,120));
        card.setBorder(border);
        card.setBackground(Color.WHITE);

        // ==========================
        // CONTENU FILTRE
        // ==========================
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);

        // ==========================
        // PARTIE GAUCHE (champs)
        // ==========================
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        left.setOpaque(false);

        // 🔍 Recherche (PLUS LARGE)
        searchField = new JTextField();
        UIStyle.styleTextField(searchField, 220, 30);

        left.add(new JLabel("Recherche"));
        left.add(searchField);

        // 📌 Status
        statusCombo = new JComboBox<>(
            new String[]{"ALL", "OPEN", "CLOSED", "VALIDATED", "PLANNED"}
        );
        UIStyle.styleComboBox(statusCombo, 150);

        left.add(new JLabel("Statut"));
        left.add(statusCombo);

        // 📅 Dates (JDateChooser)
        com.toedter.calendar.JDateChooser fromDate = new com.toedter.calendar.JDateChooser();
        UIStyle.styleDateChooser(fromDate, 140);

        com.toedter.calendar.JDateChooser toDate = new com.toedter.calendar.JDateChooser();
        UIStyle.styleDateChooser(toDate, 140);

        left.add(new JLabel("Du"));
        left.add(fromDate);

        left.add(new JLabel("Au"));
        left.add(toDate);

        // ==========================
        // PARTIE DROITE (bouton)
        // ==========================
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        right.setOpaque(false);

        JButton searchButton = new JButton("Filtrer");
        UIStyle.styleFilterButton(searchButton);

        right.add(searchButton);

        // ==========================
        // COMPTEUR (bas)
        // ==========================
        countLabel = new JLabel(campaigns.size() + " élément(s)");
        UIStyle.styleSmallLabel(countLabel);
        countLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));

        // ==========================
        // ASSEMBLAGE
        // ==========================
        content.add(left, BorderLayout.CENTER);
        content.add(right, BorderLayout.EAST);

        card.add(content, BorderLayout.CENTER);
        card.add(countLabel, BorderLayout.SOUTH);

        wrapper.add(card, BorderLayout.CENTER);

        this.add(wrapper);
        this.add(Box.createVerticalStrut(15));

        // ==========================
        // LOGIQUE FILTRE
        // ==========================
        searchButton.addActionListener(e -> searchCampaign(campaigns));
    }
    // -------------------------
    // Création du tableau
    // -------------------------
    private void createTable(ArrayList<Campaign> campaigns) {

        String[] colonnes = {"ID", "Début", "Fin", "Statut", "Choix max", "Promotion", "Créer par", "Modifier par", "Actions"};

        DefaultTableModel model = new DefaultTableModel(colonnes, 0);

        for (Campaign campaign : campaigns) {

            model.addRow(new Object[]{
                    campaign.getId(),
                    campaign.getStartDate(),
                    campaign.getEndDate(),
                    campaign.getStatus(),
                    campaign.getMaxChoices(),
                    campaign.getPromotion(),
                    campaign.getCreatedBy(),
                    campaign.getModifiedBy()
            });
        }

        table = new JTable(model);
        UIStyle.styleTable(table);
        table.getColumn("Statut").setCellRenderer(new StatusRenderer());
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        tableWrapper.setOpaque(false);

        scrollPane.setBorder(null);

        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        this.add(tableWrapper);

        // Boutons "Modifier / Supprimer" avec ButtonRenderer
        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(
            new ButtonEditor(
                new JCheckBox(),

                // EDIT
                row -> {
                    int id = (int) table.getValueAt(row, 0);
                    String startDate = table.getValueAt(row, 1).toString();
                    String endDate = table.getValueAt(row, 2).toString();
                    String status = table.getValueAt(row, 3).toString();
                    int maxChoices = (int) table.getValueAt(row, 4);
                    int promotion = (int) table.getValueAt(row, 5);
                    String createdBy = table.getValueAt(row, 6).toString();
                    String modifiedBy = (table.getValueAt(row, 7) != null) ? table.getValueAt(row, 7).toString() : null;

                    Campaign camp = new Campaign(id, status, startDate, endDate, maxChoices, promotion, createdBy, modifiedBy);
                    new CreateOrEditCampaign(camp, table);
                },

                // DELETE
                row -> {
                    int id = (int) table.getValueAt(row, 0);
                    int confirm = JOptionPane.showConfirmDialog(
                            null,
                            "Voulez-vous supprimer la campagne : " + id + " ?",
                            "Confirmation de suppression",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        int isDelete = campDAO.delete((int) id);
                        if(isDelete == 1){
                            // suppression de la ligne du tableau
                            ((DefaultTableModel) table.getModel()).removeRow(row);
                            JOptionPane.showMessageDialog(new JFrame(), "Campagne supprimée avec succès !");
                        }else{
                            JOptionPane.showMessageDialog(new JFrame(), "Erreur lors de la suppression de la campagne", "Dialog",
                                JOptionPane.ERROR_MESSAGE
                            );
                        }
                        
                    }
                }
            )
        );
        // Largeurs des colonnes
        table.getColumn("Actions").setPreferredWidth(200);
        table.getColumn("ID").setPreferredWidth(50);
        table.getColumn("Début").setPreferredWidth(100);
        table.getColumn("Fin").setPreferredWidth(100);
        table.getColumn("Statut").setPreferredWidth(100);
        table.getColumn("Choix max").setPreferredWidth(100);
        table.getColumn("Promotion").setPreferredWidth(100);
        table.getColumn("Créer par").setPreferredWidth(100);
        table.getColumn("Modifier par").setPreferredWidth(100);

        countLabel.setText(model.getRowCount() + " élément(s)");
    }

    // -------------------------
    // Filtrage par recherche
    // -------------------------
    private void searchCampaign(ArrayList<Campaign> campaigns) {
        String keyword = searchField.getText() != null 
                ? searchField.getText().trim().toLowerCase() 
                : "";

        String selectedStatus = statusCombo.getSelectedItem() != null 
                ? ((String) statusCombo.getSelectedItem()).toUpperCase() 
                : "ALL";

        java.util.Date start = (fromDate != null) ? fromDate.getDate() : null;
        java.util.Date end = (toDate != null) ? toDate.getDate() : null;

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Campaign campaign : campaigns) {

            int id = campaign.getId();
            String status = campaign.getStatus().toUpperCase();

            Date campaignStart;
            Date campaignEnd;

            try {
                campaignStart = sdf.parse(campaign.getStartDate().toString());
                campaignEnd = sdf.parse(campaign.getEndDate().toString());
            } catch (Exception ex) {
                continue;
            }

            // ==========================
            // 🔎 KEYWORD (ID optionnel)
            // ==========================
            boolean matchKeyword = true;

            if (!keyword.isEmpty()) {
                try {
                    matchKeyword = id == Integer.parseInt(keyword);
                } catch (NumberFormatException e) {
                    matchKeyword = false;
                }
            }

            // ==========================
            // 📌 STATUS
            // ==========================
            boolean matchStatus = selectedStatus.equals("ALL") || status.equals(selectedStatus);

            // ==========================
            // 📅 DATE (ULTRA IMPORTANT)
            // ==========================
            boolean matchDate = true;

            if (start != null && campaignStart.before(start)) {
                matchDate = false;
            }

            if (end != null && campaignEnd.after(end)) {
                matchDate = false;
            }

            // ==========================
            // ✅ RESULTAT
            // ==========================
            if (matchKeyword && matchStatus && matchDate) {
                model.addRow(new Object[]{
                    campaign.getId(),
                    campaign.getStartDate(),
                    campaign.getEndDate(),
                    campaign.getStatus(),
                    campaign.getMaxChoices(),
                    campaign.getPromotion(),
                    campaign.getCreatedBy(),
                    campaign.getModifiedBy()
                });
            }
        }
    }

    // -------------------------
    // Méthode main pour tester le panel seul
    // -------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Gestion des Départements");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 500);
            frame.setContentPane(new DepartmentPanel());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
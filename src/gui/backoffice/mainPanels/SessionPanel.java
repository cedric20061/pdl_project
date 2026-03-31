package gui.backoffice.mainPanels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import common.components.app.ButtonEditor;
import common.components.app.ButtonRenderer;
import common.components.app.UIStyle;
import dao.CampaignDAO;
import dao.SessionDAO;
import dao.SpecializationDAO;
import gui.backoffice.editorFrames.CreateOrEditSession;
import gui.backoffice.utils.PanelsUtils;
import model.Campaign;
import model.Session;
import model.Specialization;


public class SessionPanel extends JPanel {

    private JTable table;
    private JTextField searchField;
    private JComboBox<String> specializationFilter;
    private JComboBox<String> campaignFilter;
    private JLabel countLabel;

    private SessionDAO sessionDAO;

    public SessionPanel() {

        sessionDAO = new SessionDAO();
        
        ArrayList<Session> sessions = sessionDAO.getList();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        PanelsUtils.createHeader(
            "Sessions",
            "Créer et gérer les sessions disponibles",
            this,
            e -> new CreateOrEditSession(null, table)
        );

        this.add(Box.createVerticalStrut(15));
        createToolbar(sessions);
        this.add(Box.createVerticalStrut(15));
        createTable(sessions);
    }

    private void createToolbar(ArrayList<Session> sessions) {

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

        // 🔍 Recherche
        searchField = new JTextField();
        UIStyle.styleTextField(searchField, 200, 30);

        content.add(new JLabel("Recherche"));
        content.add(searchField);

        SpecializationDAO specDAO = new SpecializationDAO();

        ArrayList<Specialization> specializations = specDAO.getList();
        ArrayList<String> specNames = new ArrayList<>();
        specNames.add("ALL");

        for (Specialization spec : specializations) {
            specNames.add(spec.getName());
        }

        // 🎯 Dominante
        specializationFilter = new JComboBox<>(specNames.toArray(new String[0]));
        UIStyle.styleComboBox(specializationFilter, 180);

        content.add(new JLabel("Dominante"));
        content.add(specializationFilter);

        CampaignDAO campDAO = new CampaignDAO();
        ArrayList<Campaign> campaigns = campDAO.getList();
        ArrayList<String> campNames = new ArrayList<>();
        campNames.add("ALL");
        for (Campaign camp : campaigns) {
            campNames.add(camp.toString());
        }

        // Campagne
        campaignFilter = new JComboBox<>(campNames.toArray(new String[0]));
        UIStyle.styleComboBox(campaignFilter, 200);

        content.add(new JLabel("Campagne"));
        content.add(campaignFilter);

        // 🔘 Bouton
        JButton filterBtn = new JButton("Filtrer");
        UIStyle.styleFilterButton(filterBtn);
        content.add(filterBtn);

        // 📊 compteur
        countLabel = new JLabel(sessions.size() + " élément(s)");
        UIStyle.styleSmallLabel(countLabel);
        countLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));

        card.add(content, BorderLayout.CENTER);
        card.add(countLabel, BorderLayout.SOUTH);

        wrapper.add(card);
        this.add(wrapper);

        filterBtn.addActionListener(e -> searchSession(sessions));
    }

    private void createTable(ArrayList<Session> sessions) {

        String[] cols = {
            "ID", "Date", "Début", "Fin", "Capacité", "Restant", "Salle",
            "Dominante", "SpecID", "Campagne", "CampID", "Créer par", "Modifier par",  "Actions"
        };

        DefaultTableModel model = new DefaultTableModel(cols, 0);

        for(Session session: sessions){
            model.addRow(new Object[]{
                session.getId(), 
                session.getDate(),
                session.getStartTime(),
                session.getEndTime(),
                session.getMaxCapacity(),
                session.getRemainingCapacity(),
                session.getRoom(),
                session.getSpecializationName(),
                session.getSpecializationId(),
                session.getCampaignName(),
                session.getCampaignId(),
                session.getCreatedBy(),
                session.getModifiedBy(),
                "Actions"
            });
        }

        table = new JTable(model);
        UIStyle.styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        wrapper.add(scroll);

        this.add(wrapper);

        // Actions
        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(
            new ButtonEditor(
                new JCheckBox(),

                // EDIT
                row -> {
                    int id = (int) table.getValueAt(row, 0);
                    String date = (String) table.getValueAt(row, 1).toString();
                    String start = (String) table.getValueAt(row, 2).toString();
                    String end = (String) table.getValueAt(row, 3).toString();
                    int capacity = (int) table.getValueAt(row, 4);
                    int remainingCapacity = (int) table.getValueAt(row, 5);
                    String room = (String) table.getValueAt(row, 6);
                    int specialization = (int) table.getValueAt(row, 8);
                    String specializationName = (String) table.getValueAt(row, 7);
                    int campaign = (int) table.getValueAt(row, 10);
                    String campaignName = (String) table.getValueAt(row, 9);
                    String createdBy = (String) table.getValueAt(row, 11);   
                    String modifiedBy = (String) table.getValueAt(row, 12);   

                    Session sess = new Session(id, date, start, end, capacity, remainingCapacity, room, specialization, specializationName, campaign, campaignName, createdBy, modifiedBy);
                    new CreateOrEditSession(sess, table);
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
                        int isDelete = sessionDAO.delete((int) id);
                        if(isDelete == 1){
                            // suppression de la ligne du tableau
                            ((DefaultTableModel) table.getModel()).removeRow(row);
                            JOptionPane.showMessageDialog(new JFrame(), "Session supprimée avec succès !");
                        }else{
                            JOptionPane.showMessageDialog(new JFrame(), "Erreur lors de la suppression de la session", "Dialog",
                                JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                }
            )
        );

        // cacher IDs
        UIStyle.hideColumn(table,8);
        UIStyle.hideColumn(table, 10);

        table.getColumn("Actions").setPreferredWidth(200);
        table.getColumn("ID").setPreferredWidth(50);
        table.getColumn("Début").setPreferredWidth(75);
        table.getColumn("Fin").setPreferredWidth(75);
        table.getColumn("Date").setPreferredWidth(100);
        table.getColumn("Capacité").setPreferredWidth(50);
        table.getColumn("Créer par").setPreferredWidth(100);
        table.getColumn("Modifier par").setPreferredWidth(100);
        countLabel.setText(model.getRowCount() + " élément(s)");
    }

    private void searchSession(ArrayList<Session> sessions) {

        String keyword = searchField.getText().trim().toLowerCase();
        String selectedSpec = (String) specializationFilter.getSelectedItem();
        String selectedCamp = (String) campaignFilter.getSelectedItem();

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (Session session : sessions) {

            String date = session.getDate().toString().toLowerCase();
            String spec = session.getSpecializationName();
            String camp = session.getCampaignName();

            boolean matchKeyword = date.contains(keyword) || String.valueOf(session.getId()).contains(keyword);
            boolean matchSpec = selectedSpec.equals("ALL") || spec.equals(selectedSpec);
            boolean matchCamp = selectedCamp.equals("ALL") || camp.equals(selectedCamp);

            if (matchKeyword && matchSpec && matchCamp) {
                model.addRow(new Object[]{
                    session.getId(), 
                session.getDate(),
                session.getStartTime(),
                session.getEndTime(),
                session.getMaxCapacity(),
                session.getRemainingCapacity(),
                session.getRoom(),
                session.getSpecializationName(),
                session.getSpecializationId(),
                session.getCampaignName(),
                session.getCampaignId(),
                session.getCreatedBy(),
                session.getModifiedBy(),
                "Actions"
                });
            }
        }

        countLabel.setText(model.getRowCount() + " élément(s)");
    }
}

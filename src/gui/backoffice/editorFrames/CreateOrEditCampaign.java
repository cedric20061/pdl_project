package gui.backoffice.editorFrames;

import com.toedter.calendar.JDateChooser;

import common.components.app.UIStyle;
import dao.CampaignDAO;
import model.Campaign;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class CreateOrEditCampaign extends JFrame {

    private JDateChooser startDateField;
    private JDateChooser endDateField;

    private JComboBox<String> statusCombo;
    private JSpinner maxChoicesSpinner;
    private JComboBox<Integer> promotionCombo;

    private JButton saveButton;
    private JButton cancelButton;

    private JTable table; // référence au tableau pour le rafraîchissement après modification
    private Campaign campaign; // campagne à modifier (null si création)

    public CreateOrEditCampaign(Campaign campaign, JTable table) {

        this.campaign = campaign;
        this.table = table;

        setTitle("Créer une campagne");
        setSize(400, 370);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ==========================
        // Dates
        // ==========================
        startDateField = new JDateChooser();
        endDateField = new JDateChooser();

        panel.add(createField("Date début", startDateField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createField("Date fin", endDateField));

        // ==========================
        // Status
        // ==========================
        String[] statuses = {"OPEN", "CLOSED", "VALIDATED", "PLANNED"};
        statusCombo = new JComboBox<>(statuses);

        panel.add(Box.createVerticalStrut(10));
        panel.add(createField("Status", statusCombo));

        // ==========================
        // Max choices
        // ==========================
        maxChoicesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

        panel.add(Box.createVerticalStrut(10));
        panel.add(createField("Choix max", maxChoicesSpinner));

        // ==========================
        // Promotion (année)
        // ==========================
        promotionCombo = new JComboBox<>();

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = currentYear; year <= 2035; year++) {
            promotionCombo.addItem(year);
        }

        panel.add(Box.createVerticalStrut(10));
        panel.add(createField("Promotion", promotionCombo));

        panel.add(Box.createVerticalStrut(20));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // Remplir si édition
        if (campaign != null) {
            try {
                startDateField.setDate(sdf.parse(campaign.getStartDate().toString()));
                endDateField.setDate(sdf.parse(campaign.getEndDate().toString()));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            statusCombo.setSelectedItem(campaign.getStatus());
            maxChoicesSpinner.setValue(campaign.getMaxChoices());
            promotionCombo.setSelectedItem(campaign.getPromotion());
        }
        // ==========================
        // Boutons
        // ==========================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        saveButton = new JButton(campaign == null ? "Créer" : "Modifier");
        cancelButton = new JButton("Annuler");

        UIStyle.stylePrimaryButton(saveButton);
        UIStyle.styleSecondaryButton(cancelButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel);

        // ==========================
        // Actions
        // ==========================
        saveButton.addActionListener(this::onSave);
        cancelButton.addActionListener(e -> dispose());

        add(panel);
    }

    // -------------------------
    // Helper UI propre
    // -------------------------
    private JPanel createField(String labelText, JComponent field) {

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout(5, 5));
        wrapper.setOpaque(false);

        JLabel label = new JLabel(labelText);

        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(field, BorderLayout.CENTER);

        return wrapper;
    }

    // -------------------------
    // Save
    // -------------------------
    private void onSave(ActionEvent e) {

        if (startDateField.getDate() == null || endDateField.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Les dates sont obligatoires !");
            return;
        }

        if (endDateField.getDate().before(startDateField.getDate())) {
            JOptionPane.showMessageDialog(this, "La date de fin doit être après la date de début !");
            return;
        }

        String status = (String) statusCombo.getSelectedItem();
        int maxChoices = (int) maxChoicesSpinner.getValue();
        int promotion = (int) promotionCombo.getSelectedItem();

        
        CampaignDAO campDAO = new CampaignDAO();
        if (status.isEmpty() || maxChoices <= 0 || promotion <= 0 || "ALL".equals(status)) {
            JOptionPane.showMessageDialog(this, "Les champs sont obligatoires !", 
                                          "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (campaign == null) {
            // Création
            Date startDate = startDateField.getDate();
            Date endDate = endDateField.getDate();

            if (startDate == null || endDate == null) {
                JOptionPane.showMessageDialog(null, "Veuillez sélectionner les dates");
                return;
            }

            LocalDate startLocalDate = startDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            LocalDate endLocalDate = endDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            campaign = new Campaign(
                1,
                status,
                startLocalDate.toString(), // ✅ format ISO yyyy-MM-dd
                endLocalDate.toString(),
                maxChoices,
                promotion,
                "admin",
                null
            );

            // Appel DAO pour enregistrer
            int isAdd = campDAO.add(campaign);

            if(isAdd == 1){
                // Ajouter la nouvelle ligne dans le tableau
                ((DefaultTableModel) table.getModel()).addRow(new Object[]{
                    campaign.getId(), campaign.getStartDate(), campaign.getEndDate(), campaign.getStatus(), campaign.getMaxChoices(), campaign.getPromotion(), campaign.getCreatedBy(), campaign.getModifiedBy()
                });
                JOptionPane.showMessageDialog(this, "Campagne créée avec succès !");
                dispose(); // fermer la fenêtre
            }else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la création de la campagne", "Dialog",
					    JOptionPane.ERROR_MESSAGE
                );
            }
            
        } else {
            // Modification
            Date startDate = startDateField.getDate();
            Date endDate = endDateField.getDate();

            if (startDate != null && endDate != null) {

                LocalDate startLocalDate = startDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                LocalDate endLocalDate = endDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                campaign.setStartDate(startLocalDate);
                campaign.setEndDate(endLocalDate);
            }
            campaign.setStatus(status);
            campaign.setMaxChoices(maxChoices);
            campaign.setPromotion(promotion);

            // Appel DAO pour mettre à jour
            int isEdit = campDAO.update(campaign);
            if(isEdit == 1){
                // Rafraîchir la ligne modifiée dans le tableau
                int rowCount = table.getRowCount();
                for (int i = 0; i < rowCount; i++) {
                    if ((int) table.getValueAt(i, 0) == campaign.getId()) {
                        table.setValueAt(campaign.getStartDate(), i, 1);
                        table.setValueAt(campaign.getEndDate(), i, 2);
                        table.setValueAt(campaign.getStatus(), i, 3);
                        table.setValueAt(campaign.getMaxChoices(), i, 4);
                        table.setValueAt(campaign.getPromotion(), i, 5);
                        table.setValueAt(campaign.getModifiedBy(), i, 6);
                        break;
                    }
                }
                JOptionPane.showMessageDialog(this, "Campagne modifiée avec succès !");
                dispose(); // fermer la fenêtre
            }else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour de la campagne", "Dialog",
					    JOptionPane.ERROR_MESSAGE
                );
            }
            
        }
    }
}
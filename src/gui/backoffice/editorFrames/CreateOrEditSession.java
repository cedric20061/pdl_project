package gui.backoffice.editorFrames;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import common.components.app.UIStyle;
import dao.CampaignDAO;
import dao.SessionDAO;
import dao.SpecializationDAO;
import model.Campaign;
import model.Session;
import model.Specialization;

public class CreateOrEditSession extends JFrame {

    private JDateChooser dateChooser;
    private JComboBox<String> startTimeBox;
    private JComboBox<String> endTimeBox;
    private JTextField capacityField;
    private JTextField roomField;

    private JComboBox<String> specializationBox;
    private JComboBox<String> campaignBox;

    private JButton saveButton;
    private JButton cancelButton;

    private JTable table;

    private Session session; // null = création

    public CreateOrEditSession(Session session, JTable table) {
        this.session = session;
        this.table = table;

        setTitle(session == null ? "Créer une session" : "Modifier la session");
        setSize(450, 500);
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
        // 📅 DATE
        // ==========================
        JLabel dateLabel = new JLabel("Date");

        dateChooser = new JDateChooser();

        panel.add(dateLabel);
        panel.add(dateChooser);
        panel.add(Box.createVerticalStrut(10));

        // ==========================
        // ⏰ HEURES
        // ==========================
        JLabel startLabel = new JLabel("Heure début");
        startTimeBox = new JComboBox<>(generateTimeSlots());
        UIStyle.styleComboBox(startTimeBox, 120);

        JLabel endLabel = new JLabel("Heure fin");
        endTimeBox = new JComboBox<>(generateTimeSlots());
        UIStyle.styleComboBox(endTimeBox, 120);

        panel.add(startLabel);
        panel.add(startTimeBox);
        panel.add(Box.createVerticalStrut(10));

        panel.add(endLabel);
        panel.add(endTimeBox);
        panel.add(Box.createVerticalStrut(10));

        // ==========================
        // CAPACITÉ
        // ==========================
        JLabel capacityLabel = new JLabel("Capacité");
        capacityField = new JTextField();

        panel.add(capacityLabel);
        panel.add(capacityField);
        panel.add(Box.createVerticalStrut(10));

        // ==========================
        // SALLE
        // ==========================
        JLabel roomLabel = new JLabel("Salle");
        roomField = new JTextField();
        panel.add(roomLabel);
        panel.add(roomField);
        panel.add(Box.createVerticalStrut(10));

        // ==========================
        // DOMINANTE
        // ==========================
        JLabel specLabel = new JLabel("Dominante");

        SpecializationDAO specDAO = new SpecializationDAO();

        ArrayList<Specialization> specializations = specDAO.getList();
        ArrayList<String> specNames = new ArrayList<>();
        specNames.add("ALL");

        for (Specialization spec : specializations) {
            specNames.add(spec.getName());
        }

        specializationBox = new JComboBox<>(specNames.toArray(new String[0]));
        UIStyle.styleComboBox(specializationBox, 250);

        panel.add(specLabel);
        panel.add(specializationBox);
        panel.add(Box.createVerticalStrut(10));

        // ==========================
        // 📢 CAMPAGNE
        // ==========================
        JLabel campLabel = new JLabel("Campagne");

        CampaignDAO campDAO = new CampaignDAO();
        ArrayList<Campaign> campaigns = campDAO.getList();
        ArrayList<String> campNames = new ArrayList<>();
        campNames.add("ALL");
        for (Campaign camp : campaigns) {
            campNames.add(camp.toString());
        }

        campaignBox = new JComboBox<>(campNames.toArray(new String[0]));
        UIStyle.styleComboBox(campaignBox, 250);

        panel.add(campLabel);
        panel.add(campaignBox);
        panel.add(Box.createVerticalStrut(20));

        // ==========================
        // BOUTONS
        // ==========================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        saveButton = new JButton(session == null ? "Créer" : "Modifier");
        cancelButton = new JButton("Annuler");

        UIStyle.stylePrimaryButton(saveButton);
        UIStyle.styleSecondaryButton(cancelButton);

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // Remplir si édition
        if (session != null) {
            try {
                dateChooser.setDate(sdf.parse(session.getDate().toString()));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            startTimeBox.setSelectedItem(session.getStartTime().toString());
            endTimeBox.setSelectedItem(session.getEndTime().toString());
            capacityField.setText(String.valueOf(session.getMaxCapacity()));
            roomField.setText(session.getRoom());
            specializationBox.setSelectedItem(session.getSpecializationName());
            campaignBox.setSelectedItem(session.getCampaignName());

        }
        // ==========================
        // ACTIONS
        // ==========================
        saveButton.addActionListener(this::onSave);
        cancelButton.addActionListener(e -> dispose());

        add(panel);
    }

    // ==========================
    // GENERATE TIME SLOTS (30 min)
    // ==========================
    private String[] generateTimeSlots() {
        ArrayList<String> slots = new ArrayList<>();
        String[] periods = {"08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30",
                            "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"};
        slots.addAll(java.util.Arrays.asList(periods));
        return slots.toArray(new String[0]);
    }

    // ==========================
    // SAVE
    // ==========================
    private void onSave(ActionEvent e) {

        Date date = dateChooser.getDate();
        String start = (String) startTimeBox.getSelectedItem();
        String end = (String) endTimeBox.getSelectedItem();
        String capacityStr = capacityField.getText().trim();
        String room = roomField.getText().trim();
        String specialization = (String) specializationBox.getSelectedItem();
        String campaign = (String) campaignBox.getSelectedItem();

        // ==========================
        // VALIDATION
        // ==========================
        if (date == null || start == null || end == null || capacityStr.isEmpty() || room.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Tous les champs sont obligatoires",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityStr);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Capacité invalide",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ==========================
        // CHECK HEURE
        // ==========================
        if (start.compareTo(end) >= 0) {
            JOptionPane.showMessageDialog(this,
                "L'heure de fin doit être après l'heure de début",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int specializationId = -1, campaignId = -1;
        for(Specialization spec: new SpecializationDAO().getList()) {
            if(spec.getName().equals(specialization)) {
                specializationId = spec.getId();
                break;
            }
        }

        for(Campaign camp: new CampaignDAO().getList()) {
            if(camp.toString().equals(campaign)) {
                campaignId = camp.getId();
                break;
            }
        }

        if(specializationId == -1 || campaignId == -1) {
            JOptionPane.showMessageDialog(this,
                "Dominante ou Campagne invalide",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        SessionDAO sessionDao = new SessionDAO();
        if (session == null) {
            // CREATE
            session = new Session(
                0, 
                dateStr, 
                start, 
                end, 
                capacity, 
                capacity,
                room,
                specializationId, 
                specialization, 
                campaignId, 
                campaign, 
                "admin", 
                null
            );
            int isAdd = sessionDao.add(session);
            if(isAdd == 0) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la création !");
                return;
            }
            ((DefaultTableModel) table.getModel()).addRow(new Object[]{
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

            JOptionPane.showMessageDialog(this, "Session créée !");
        } else {
            // UPDATE
            session.setStartTime(LocalTime.parse(start));
            session.setEndTime(LocalTime.parse(end));
            session.setMaxCapacity(capacity);
            //TODO géré la mise a jour de remaining capacity en fonction de la mise a jour de la capacité max
            session.setRoom(room);
            session.setSpecializationName(specialization);
            session.setCampaignName(campaign);
            session.setSpecializationId(specializationId);
            session.setCampaignId(campaignId);

            int isUpdate = sessionDao.update(session);
            if(isUpdate == 0) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification !");
                return;
            }
            for (int i = 0; i < model.getRowCount(); i++) {
                if ((int) model.getValueAt(i, 0) == session.getId()) {

                    model.setValueAt(dateStr, i, 1);
                    model.setValueAt(start, i, 2);
                    model.setValueAt(end, i, 3);
                    model.setValueAt(capacity, i, 4);
                    //model.setValueAt(capacity, i, 5); // TODO gérer la mise à jour de la capacité restante
                    model.setValueAt(room, i, 6);
                    model.setValueAt(specialization, i, 7);
                    model.setValueAt(specializationId, i, 8);
                    model.setValueAt(campaign, i, 9);
                    model.setValueAt(campaignId, i, 10);
                    model.setValueAt(session.getCreatedBy(), i, 11);
                    model.setValueAt(session.getModifiedBy(), i, 12);
                    break;
                }
            }

            JOptionPane.showMessageDialog(this, "Session modifiée !");
        }

        dispose();
    }
}
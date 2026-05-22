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
    private JTextField remainingCapacityField;
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
        // CAPACITÉ RESTANTE
        // ==========================
        JLabel remainingLabel = new JLabel("Capacité restante");
        remainingCapacityField = new JTextField();

        panel.add(remainingLabel);
        panel.add(remainingCapacityField);
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
        // Filtrer les campagnes archivées - on ne peut créer une session que pour une campagne non archivée
        for (Campaign camp : campaigns) {
            if (!"ARCHIVED".equals(camp.getStatus())) {
                campNames.add(camp.toString());
            }
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
            remainingCapacityField.setText(String.valueOf(session.getRemainingCapacity()));
            roomField.setText(session.getRoom());
            specializationBox.setSelectedItem(session.getSpecializationName());
            campaignBox.setSelectedItem(session.getCampaignName());

        }
        // ==========================
        // ACTIONS
        // ==========================
        saveButton.addActionListener(this::onSave);
        cancelButton.addActionListener(e -> dispose());

        // Listener pour mettre à jour la capacité restante en temps réel (uniquement en édition)
        if (session != null) {
            capacityField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { updateRemainingCapacity(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { updateRemainingCapacity(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { updateRemainingCapacity(); }
            });
        }

        add(panel);
    }

    // ==========================
    // UPDATE REMAINING CAPACITY (pour affichage temps réel)
    // ==========================
    private void updateRemainingCapacity() {
        try {
            int newCapacity = Integer.parseInt(capacityField.getText().trim());
            int oldMaxCapacity = session.getMaxCapacity();
            int oldRemaining = session.getRemainingCapacity();
            
            int newRemaining = oldRemaining + (newCapacity - oldMaxCapacity);
            
            // Si capacity est 0, remaining doit être 0
            if (newCapacity == 0) {
                newRemaining = 0;
            }
            // Éviter les valeurs négatives
            if (newRemaining < 0) {
                newRemaining = 0;
            }
            
            remainingCapacityField.setText(String.valueOf(newRemaining));
        } catch (NumberFormatException ex) {
            remainingCapacityField.setText("0");
        }
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
        
        // ==========================
        // CHECK PAUSE 12:30-13:30
        // ==========================
        // Vérifier si la session chevauche la pause de midi
        LocalTime breakStart = LocalTime.of(12, 30);
        LocalTime breakEnd = LocalTime.of(13, 30);
        LocalTime sessionStart = LocalTime.parse(start);
        LocalTime sessionEnd = LocalTime.parse(end);
        
        // La session ne doit pas chevaucher la pause
        if ((sessionStart.isBefore(breakEnd) && sessionEnd.isAfter(breakStart)) ||
            sessionStart.equals(breakStart) || sessionEnd.equals(breakEnd)) {
            JOptionPane.showMessageDialog(this,
                "La session ne peut pas chevaucher la pause de midi (12:30-13:30)\n" +
                "Choisissez une heure avant 12:30 ou après 13:30",
                "Conflit avec la pause",
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
            // Sauvegarder les anciennes valeurs
            int oldMaxCapacity = session.getMaxCapacity();
            int oldRemaining = session.getRemainingCapacity();
            int currentRegistrations = oldMaxCapacity - oldRemaining;
            
            // Récupérer la nouvelle valeur de remaining_capacity saisie par l'utilisateur
            String remainingStr = remainingCapacityField.getText().trim();
            int newRemaining;
            try {
                newRemaining = Integer.parseInt(remainingStr);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Capacité restante invalide",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // =====================================
            // VALIDATIONS
            // =====================================
            
            // 1. Validation: la nouvelle capacité doit être >= au nombre d'inscriptions actuelles
            if (capacity < currentRegistrations) {
                JOptionPane.showMessageDialog(this,
                    "Erreur: La nouvelle capacité max (" + capacity + ") ne peut pas être inférieure " +
                    "au nombre d'inscriptions actuelles (" + currentRegistrations + ")",
                    "Capacité insuffisante",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 2. Validation: remaining_capacity <= max_capacity
            if (newRemaining > capacity) {
                JOptionPane.showMessageDialog(this,
                    "Erreur: La capacité restante (" + newRemaining + ") ne peut pas être supérieure " +
                    "à la capacité max (" + capacity + ")",
                    "Capacité restante invalide",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 3. Validation: remaining_capacity >= 0
            if (newRemaining < 0) {
                JOptionPane.showMessageDialog(this,
                    "Erreur: La capacité restante ne peut pas être négative",
                    "Capacité restante invalide",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 4. Validation: remaining_capacity + inscriptions = max_capacity ou cohérent
            int calculatedRegistrations = capacity - newRemaining;
            if (calculatedRegistrations < currentRegistrations) {
                JOptionPane.showMessageDialog(this,
                    "Erreur: Avec cette capacité restante (" + newRemaining + "), " +
                    "le nombre d'inscriptions serait " + calculatedRegistrations + " " +
                    "mais vous en avez actuellement " + currentRegistrations + "",
                    "Capacité restante invalide",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            session.setStartTime(LocalTime.parse(start));
            session.setEndTime(LocalTime.parse(end));
            session.setMaxCapacity(capacity);
            session.setRemainingCapacity(newRemaining);
            
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
                    model.setValueAt(session.getRemainingCapacity(), i, 5);
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
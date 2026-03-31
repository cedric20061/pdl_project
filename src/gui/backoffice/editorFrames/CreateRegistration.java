package gui.backoffice.editorFrames;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import dao.CampaignDAO;
import dao.RegistrationDAO;
import dao.SessionDAO;
import dao.StudentDAO;
import model.Campaign;
import model.Session;
import model.Student;

public class CreateRegistration extends JFrame {

    private JTextField searchStudentField;
    private JList<String> searchResultList;
    private DefaultListModel<String> searchListModel;
    private JComboBox<String> sessionComboBox;
    private JButton addButton;
    private JButton cancelButton;

    private JTable table; // Référence au tableau pour ajouter la nouvelle inscription

    // Fake students
    private List<Student> students;

    public CreateRegistration(JTable table) {
        this.table = table;

        StudentDAO studentDAO = new StudentDAO();
        // Fake data pour étudiants
        students = studentDAO.getList(); // Récupérer les étudiants depuis la base de données

        setTitle("Ajouter une inscription");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // ========================
        // Recherche étudiant
        // ========================
        searchStudentField = new JTextField();
        searchStudentField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        searchStudentField.setBorder(BorderFactory.createTitledBorder("Rechercher un étudiant"));

        searchListModel = new DefaultListModel<>();
        searchResultList = new JList<>(searchListModel);
        searchResultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(searchResultList);
        listScrollPane.setPreferredSize(new Dimension(350, 100));

        searchStudentField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateSearchResults(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateSearchResults(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateSearchResults(); }
        });

        panel.add(searchStudentField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(listScrollPane);
        panel.add(Box.createVerticalStrut(20));

        // ========================
        // Sélection session
        // ========================
        sessionComboBox = new JComboBox<>();
        SessionDAO sessionDao = new SessionDAO();
        sessionDao.getList().forEach(session -> sessionComboBox.addItem(session.toString())); 
        sessionComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        sessionComboBox.setBorder(BorderFactory.createTitledBorder("Sélectionner une session"));

        panel.add(sessionComboBox);
        panel.add(Box.createVerticalStrut(20));

        // ========================
        // Boutons
        // ========================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        addButton = new JButton("Ajouter");
        cancelButton = new JButton("Annuler");

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel);

        addButton.addActionListener(this::onAdd);
        cancelButton.addActionListener(e -> dispose());

        add(panel);
    }

    private void updateSearchResults() {
        String keyword = searchStudentField.getText().trim().toLowerCase();
        searchListModel.clear();
        for (Student s : students) {
            if (s.getLastName().toLowerCase().contains(keyword) || s.getEmail().toLowerCase().contains(keyword)) {
                searchListModel.addElement(s.getLastName() + " | " + s.getEmail() + " | ID:" + s.getId());
            }
        }
    }

    private void onAdd(ActionEvent e) {
        String selectedValue = searchResultList.getSelectedValue();
        if (selectedValue == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un étudiant.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Récupérer l'id de l'étudiant depuis la chaîne sélectionnée
        int studentId = Integer.parseInt(selectedValue.split("ID:")[1].trim());
        String studentName = selectedValue.split("\\|")[0].trim();
        String studentEmail = selectedValue.split("\\|")[1].trim();

        int sessionId = -1; // SessionID
        if (!sessionComboBox.equals("ALL")) {
                String[] parts = ((String) sessionComboBox.getSelectedItem()).split(" - ");
                if (parts.length > 1) {
                    try {
                        sessionId = Integer.parseInt(parts[1].trim());
                    } catch (NumberFormatException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        //TODO Fake rank et status
        SessionDAO sessionDAO = new SessionDAO();
        Session selectedSession = sessionDAO.get(sessionId);
        CampaignDAO campaignDAO = new CampaignDAO();
        Campaign currentCampaign = campaignDAO.get(selectedSession.getCampaignId());
        RegistrationDAO registrationDao = new RegistrationDAO();
        int nbrStudentSession = registrationDao.getByStudent(studentId).size();
        if(nbrStudentSession >= currentCampaign.getMaxChoices()){
            JOptionPane.showMessageDialog(this, "Cet étudiant est déja inscrit à un nombre max de session", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int rank = nbrStudentSession + 1;
        String status = "PENDING";


        int isAdd = registrationDao.add(studentId, sessionId, rank, status);
        if(isAdd == 0){
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout !");
            return;
        }
        // Ajouter au tableau
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(new Object[]{
            sessionId,
            studentId,
            studentName,
            studentEmail,
            rank,
            status,
            "Actions"
        });

        JOptionPane.showMessageDialog(this, "Inscription ajoutée avec succès !");
        dispose();
    }
}
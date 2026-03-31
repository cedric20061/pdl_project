package gui.backoffice.editorFrames;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import common.components.app.LabelTextField;
import common.components.app.UIStyle;
import dao.DepartmentDAO;
import dao.SpecializationDAO;
import model.Department;
import model.Specialization;

public class CreateOrEditSpecialization extends JFrame {

    private LabelTextField nameField;
    private LabelTextField descriptionField;
    private LabelTextField acronymField;
    private LabelTextField handleByField;

    private JComboBox<String> departmentCombo;

    private JButton saveButton;
    private JButton cancelButton;

    private JTable table;
    private Specialization specialization;

    public CreateOrEditSpecialization(Specialization spec, JTable table) {
        this.specialization = spec;
        this.table = table;

        setTitle(spec == null ? "Créer une dominante" : "Modifier la dominante");
        setSize(450, 400);
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
        // Champs
        // ==========================
        nameField = new LabelTextField("Nom", 20);
        descriptionField = new LabelTextField("Description", 20);
        acronymField = new LabelTextField("Acronyme", 20);
        handleByField = new LabelTextField("Responsable", 20);

        // ==========================
        // Combo Département
        // ==========================
        JLabel deptLabel = new JLabel("Département");
        deptLabel.setAlignmentX(LEFT_ALIGNMENT);

        DepartmentDAO depDAO = new DepartmentDAO();
        ArrayList<Department> departments = depDAO.getList();
        ArrayList<String> departmentsName = new ArrayList<>();
        departmentsName.add("ALL");

        for (Department dept : departments) {
            departmentsName.add(dept.getName());
        }

        departmentCombo = new JComboBox<>(departmentsName.toArray(new String[0]));
        departmentCombo.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 30));

        // ==========================
        // Remplissage si édition
        // ==========================
        if (specialization != null) {
            nameField.setText(specialization.getName());
            descriptionField.setText(specialization.getDescription());
            acronymField.setText(specialization.getAcronym());
            handleByField.setText(specialization.getHandleBy());

            departmentCombo.setSelectedItem(specialization.getDepartmentName());
        }

        // ==========================
        // Ajout des composants
        // ==========================
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(10));

        panel.add(descriptionField);
        panel.add(Box.createVerticalStrut(10));

        panel.add(acronymField);
        panel.add(Box.createVerticalStrut(10));

        panel.add(handleByField);
        panel.add(Box.createVerticalStrut(10));

        panel.add(deptLabel);
        panel.add(departmentCombo);
        panel.add(Box.createVerticalStrut(20));

        // ==========================
        // Boutons
        // ==========================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        saveButton = new JButton(specialization == null ? "Créer" : "Modifier");
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

    private void onSave(ActionEvent e) {

        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        String acronym = acronymField.getText().trim();
        String handleBy = handleByField.getText().trim();
        String department = (String) departmentCombo.getSelectedItem();

        // Validation simple
        if (name.isEmpty() || handleBy.isEmpty() || department.equals("ALL")) {
            JOptionPane.showMessageDialog(this,
                    "Nom, Responsable et Département sont obligatoires !",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ==========================
        // CREATE
        // ==========================
        SpecializationDAO specDAO = new SpecializationDAO();
        if (specialization == null) {

            int departmentId = -1;
            for (Department dept : new DepartmentDAO().getList()) {
                if (dept.getName().equals(department)) {
                    departmentId = dept.getId();
                    break;
                }
            }
            specialization = new Specialization(
                1, // fake id
                name,
                description,
                acronym,
                handleBy,
                department, // nom du département
                departmentId
            );

            int isAdd = specDAO.add(specialization);
            if(isAdd == 0) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la création !");
                return;
            }
            ((DefaultTableModel) table.getModel()).addRow(new Object[]{
                specialization.getId(),
                specialization.getName(),
                specialization.getDescription(),
                specialization.getAcronym(),
                specialization.getHandleBy(),
                specialization.getDepartmentName(),
                specialization.getDepartmentId(),
                "Actions"
            });

            JOptionPane.showMessageDialog(this, "Dominante créée !");
            dispose();

        } 
        // ==========================
        // UPDATE
        // ==========================
        else {

            specialization.setName(name);
            specialization.setDescription(description);
            specialization.setAcronym(acronym);
            specialization.setHandleBy(handleBy);
            specialization.setDepartmentName(department);
            
            int departmentId = -1;
            for (Department dept : new DepartmentDAO().getList()) {
                if (dept.getName().equals(department)) {
                    departmentId = dept.getId();
                    break;
                }
            }
            specialization.setDepartmentId(departmentId);

            int isUpdate = specDAO.update(specialization);
            if(isUpdate == 0) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification !");
                return;
            }
            int rowCount = table.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                if ((int) table.getValueAt(i, 0) == specialization.getId()) {

                    table.setValueAt(name, i, 1);
                    table.setValueAt(description, i, 2);
                    table.setValueAt(acronym, i, 3);
                    table.setValueAt(handleBy, i, 4);
                    table.setValueAt(department, i, 5);
                    table.setValueAt(departmentId, i, 6);
                    break;
                }
            }

            JOptionPane.showMessageDialog(this, "Dominante modifiée !");
            dispose();
        }
    }
}
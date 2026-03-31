package gui.backoffice.editorFrames;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import common.components.app.LabelTextField;
import common.components.app.UIStyle;
import dao.DepartmentDAO;
import model.Department;

public class CreateOrEditDepartment extends JFrame {
    private LabelTextField nameField;
    private LabelTextField descriptionField;
    private LabelTextField handleByField;

    private JButton saveButton;
    private JButton cancelButton;
    private JTable table; // référence au tableau pour le rafraîchissement après modification

    private Department department; // département à modifier (null si création)

    public CreateOrEditDepartment(Department dep, JTable table) { 
        this.department = dep;
        this.table = table;

        setTitle(department == null ? "Créer un département" : "Modifier le département");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Champs
        nameField = new LabelTextField("Nom", 20);
        descriptionField = new LabelTextField("Description", 20);
        handleByField = new LabelTextField("Responsable", 20);

        // Remplir si édition
        if (department != null) {
            nameField.setText(department.getName());
            descriptionField.setText(department.getDescription());
            handleByField.setText(department.getHandleBy());
        }

        panel.add(nameField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(descriptionField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(handleByField);
        panel.add(Box.createVerticalStrut(20));

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        saveButton = new JButton(department == null ? "Créer" : "Modifier");
        cancelButton = new JButton("Annuler");

        UIStyle.stylePrimaryButton(saveButton);
        UIStyle.styleSecondaryButton(cancelButton);
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel);

        // Actions des boutons
        saveButton.addActionListener(this::onSave);
        cancelButton.addActionListener(e -> dispose());

        add(panel);
    }

    private void onSave(ActionEvent e) {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        String handleBy = handleByField.getText().trim();

        DepartmentDAO deptDAO = new DepartmentDAO();
        if (name.isEmpty() || handleBy.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nom, Description et Responsable sont obligatoires !", 
                                          "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (department == null) {
            // Création
            department = new Department(1, name, description, handleBy);

            // Appel DAO pour enregistrer
            int isAdd = deptDAO.add(department);

            if(isAdd == 1){
                // Ajouter la nouvelle ligne dans le tableau
                ((DefaultTableModel) table.getModel()).addRow(new Object[]{
                    department.getId(), department.getName(), department.getDescription(), department.getHandleBy(), "Action"
                });
                JOptionPane.showMessageDialog(this, "Département créé avec succès !");
                dispose(); // fermer la fenêtre
            }else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la création du département", "Dialog",
					    JOptionPane.ERROR_MESSAGE
                );
            }
            
        } else {
            // Modification
            department.setName(name);
            department.setDescription(description);
            department.setHandleBy(handleBy);

            // Appel DAO pour mettre à jour
            int isEdit = deptDAO.update(department);
            if(isEdit == 1){
                // Rafraîchir la ligne modifiée dans le tableau
                int rowCount = table.getRowCount();
                for (int i = 0; i < rowCount; i++) {
                    if ((int) table.getValueAt(i, 0) == department.getId()) {
                        table.setValueAt(department.getName(), i, 1);
                        table.setValueAt(department.getDescription(), i, 2);
                        table.setValueAt(department.getHandleBy(), i, 3);
                        break;
                    }
                }
                JOptionPane.showMessageDialog(this, "Département modifié avec succès !");
                dispose(); // fermer la fenêtre
            }else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour du département", "Dialog",
					    JOptionPane.ERROR_MESSAGE
                );
            }
            
        }
    }
}

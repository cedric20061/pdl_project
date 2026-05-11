package gui.backoffice.mainPanels;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

import gui.backoffice.editorFrames.CreateOrEditSpecialization;
import gui.backoffice.utils.PanelsUtils;
import common.components.app.ButtonEditor;
import common.components.app.ButtonRenderer;
import common.components.app.UIStyle;
import dao.SpecializationDAO;
import dao.DepartmentDAO;
import model.Specialization;
import model.Department;

public class SpecializationPanel extends JPanel {

    private JTable table;
    private JTextField searchField;
    private JComboBox<String> departmentFilter;
    private JLabel countLabel;
    private SpecializationDAO specDAO;

    public SpecializationPanel() {

        specDAO = new SpecializationDAO();
        ArrayList<Specialization> specializations = specDAO.getList();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        PanelsUtils.createHeader(
            "Dominantes",
            "Créer et gérer les spécialisations disponibles",
            this,
            e -> new CreateOrEditSpecialization(null, table)
        );

        this.add(Box.createVerticalStrut(15));
        createToolbar(specializations);
        this.add(Box.createVerticalStrut(15));
        createTable(specializations);
    }

    // ==========================
    // TOOLBAR
    // ==========================
    private void createToolbar(ArrayList<Specialization> specializations) {

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

        // 🏢 Filtre département
        DepartmentDAO depDAO = new DepartmentDAO();
        ArrayList<Department> departments = depDAO.getList();
        ArrayList<String> departmentsName = new ArrayList<>();
        departmentsName.add("ALL");

        for (Department dept : departments) {
            departmentsName.add(dept.getName());
        }

        departmentFilter = new JComboBox<>(departmentsName.toArray(new String[0]));

        UIStyle.styleComboBox(departmentFilter, 180);

        content.add(new JLabel("Département"));
        content.add(departmentFilter);

        // 🔘 Bouton
        JButton searchButton = new JButton("Filtrer");
        UIStyle.styleFilterButton(searchButton);
        
        JButton refreshButton = new JButton("Actualiser");
        UIStyle.styleFilterButton(refreshButton);
        refreshButton.addActionListener(e -> refreshTable());

        content.add(searchButton);
        content.add(refreshButton);

        // 📊 Compteur
        countLabel = new JLabel(specializations.size() + " élément(s)");
        UIStyle.styleSmallLabel(countLabel);
        countLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));

        card.add(content, BorderLayout.CENTER);
        card.add(countLabel, BorderLayout.SOUTH);

        wrapper.add(card);
        this.add(wrapper);

        searchButton.addActionListener(e -> searchSpecialization(specializations));
    }

    // ==========================
    // TABLE
    // ==========================
    private void createTable(ArrayList<Specialization> specializations) {

        String[] colonnes = {
            "ID", "Nom", "Description", "Acronyme", 
            "Responsable", "Département", "DepartmentID", "Actions"
        };

        DefaultTableModel model = new DefaultTableModel(colonnes, 0);

        for (Specialization spec : specializations) {
            model.addRow(new Object[]{
                spec.getId(),
                spec.getName(),
                spec.getDescription(),
                spec.getAcronym(),
                spec.getHandleBy(),
                spec.getDepartmentName(),
                spec.getDepartmentId(),
                "Action"
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

                // EDIT
                row -> {
                    Specialization sp = new Specialization(
                        (int) table.getValueAt(row, 0),
                        (String) table.getValueAt(row, 1),
                        (String) table.getValueAt(row, 2),
                        (String) table.getValueAt(row, 3),
                        (String) table.getValueAt(row, 4),
                        (String) table.getValueAt(row, 5), 
                        (int) table.getValueAt(row, 6)
                    );
                    new CreateOrEditSpecialization(sp, table);
                },

                // DELETE
                row -> {
                    String name = table.getValueAt(row, 1).toString();

                    int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Supprimer la dominante : " + name + " ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION
                    );

                    int isDelete = specDAO.delete((int) table.getValueAt(row, 0));
                    if(isDelete == 0) {
                        JOptionPane.showMessageDialog(null, "Erreur lors de la suppression !");
                        return;
                    }
                    if (confirm == JOptionPane.YES_OPTION) {
                        ((DefaultTableModel) table.getModel()).removeRow(row);
                    }
                }
            )
        );

        // cacher DepartmentID
        table.getColumnModel().getColumn(6).setMinWidth(0);
        table.getColumnModel().getColumn(6).setMaxWidth(0);
        table.getColumnModel().getColumn(6).setWidth(0);

        countLabel.setText(model.getRowCount() + " élément(s)");
    }

    // ==========================
    // REFRESH TABLE
    // ==========================
    private void refreshTable() {
        ArrayList<Specialization> specializations = specDAO.getList();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (Specialization spec : specializations) {
            model.addRow(new Object[]{
                spec.getId(),
                spec.getName(),
                spec.getDescription(),
                spec.getAcronym(),
                spec.getHandleBy(),
                spec.getDepartmentName(),
                spec.getDepartmentId(),
                "Action"
            });
        }

        countLabel.setText(model.getRowCount() + " élément(s)");
    }

    // ==========================
    // FILTRE
    // ==========================
    private void searchSpecialization(ArrayList<Specialization> specializations) {

        String keyword = searchField.getText().trim().toLowerCase();
        String selectedDept = ((String) departmentFilter.getSelectedItem());

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (Specialization spec: specializations) {

            String name = spec.getName().toLowerCase();
            String desc = spec.getDescription().toLowerCase();
            String dept = spec.getDepartmentName();

            boolean matchKeyword = name.contains(keyword) || desc.contains(keyword);
            boolean matchDept = selectedDept.equals("ALL") || dept.equals(selectedDept);

            if (matchKeyword && matchDept) {
                model.addRow(new Object[]{
                    spec.getId(),
                    spec.getName(),
                    spec.getDescription(),
                    spec.getAcronym(),
                    spec.getHandleBy(),
                    spec.getDepartmentName(),
                    spec.getDepartmentId(),
                    "Action"
                });
            }
        }

        countLabel.setText(model.getRowCount() + " élément(s)");
    }
}
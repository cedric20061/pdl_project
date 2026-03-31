package gui.backoffice.mainPanels;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import gui.backoffice.editorFrames.CreateOrEditDepartment;
import gui.backoffice.utils.PanelsUtils;
import common.components.app.ButtonEditor;
import common.components.app.ButtonRenderer;
import common.components.app.UIStyle;
import dao.DepartmentDAO;
import model.Department;


public class DepartmentPanel extends JPanel {

    private JTable table;
    private JTextField searchField;
    private JLabel countLabel;
    private DepartmentDAO deptDAO;

    public DepartmentPanel() {
        deptDAO = new DepartmentDAO();
        ArrayList<Department> departments = deptDAO.getList();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        PanelsUtils.createHeader("Départements", 
        "Créer et gérer les départements disponibles", 
                    this, 
                    e->{
                        new CreateOrEditDepartment(null, table);
                    }
            );
        this.add(Box.createVerticalStrut(15));
        createToolbar(departments);
        this.add(Box.createVerticalStrut(15));
        createTable(departments);
    }

    private void createToolbar(ArrayList<Department> departments) {
        // ==========================
        // WRAPPER (alignement global)
        // ==========================
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        wrapper.setOpaque(false);

        // ==========================
        // CARD FILTRE
        // ==========================
        JPanel card = new JPanel(new BorderLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Filtres");
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        border.setTitleColor(new Color(120,120,120));
        card.setBorder(border);
        card.setBackground(Color.WHITE);

        // -------------------------
        // Panel de recherche
        // -------------------------
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Recherche :");
        UIStyle.styleSubHeaderLabel(searchLabel);

        searchField = new JTextField(20);
        UIStyle.styleTextField(searchField, 28);

        JButton searchButton = new JButton("Rechercher");
        UIStyle.styleSecondaryButton(searchButton);

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // ==========================
        // Compteur intégré (BOTTOM)
        // ==========================
        countLabel = new JLabel("5 éléments");
        UIStyle.styleSmallLabel(countLabel);
        countLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));

        // ==========================
        // Layout final
        // ==========================
        card.add(searchPanel, BorderLayout.CENTER);
        card.add(countLabel, BorderLayout.SOUTH);

        wrapper.add(card, BorderLayout.CENTER);

        this.add(wrapper);

        searchButton.addActionListener(e -> searchDepartment(departments));
    }

    // -------------------------
    // Création du tableau
    // -------------------------
    private void createTable(ArrayList<Department> departments) {

        String[] colonnes = {"ID", "Nom", "Description", "Responsable", "Actions"};

        DefaultTableModel model = new DefaultTableModel(colonnes, 0);

        for (Department dept : departments) {

            model.addRow(new Object[]{
                    dept.getId(),
                    dept.getName(),
                    dept.getDescription(),
                    dept.getHandleBy()
            });
        }

        table = new JTable(model);
        UIStyle.styleTable(table);

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
                    String nom = (String) table.getValueAt(row, 1);
                    String description = (String) table.getValueAt(row, 2);
                    String responsable = (String) table.getValueAt(row, 3);

                    Department dept = new Department(id, nom, description, responsable);
                    new CreateOrEditDepartment(dept, table);
                },

                // DELETE
                row -> {
                    int id = (int) table.getValueAt(row, 0);
                    String name = table.getValueAt(row, 1).toString();
                    int confirm = JOptionPane.showConfirmDialog(
                            null,
                            "Voulez-vous supprimer le départment : " + name + " ?",
                            "Confirmation de suppression",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        int isDelete = deptDAO.delete((int) id);
                        if(isDelete == 1){
                            // suppression de la ligne du tableau
                            ((DefaultTableModel) table.getModel()).removeRow(row);
                            JOptionPane.showMessageDialog(new JFrame(), "Départment supprimer avec succès !");
                        }else{
                            JOptionPane.showMessageDialog(new JFrame(), "Erreur lors de la suppression du département", "Dialog",
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
        table.getColumn("Nom").setPreferredWidth(150);
        table.getColumn("Description").setPreferredWidth(300);
        table.getColumn("Responsable").setPreferredWidth(200);
        countLabel.setText(model.getRowCount() + " élément(s)");
    }

    // -------------------------
    // Filtrage par recherche
    // -------------------------
    private void searchDepartment(ArrayList<Department> departments) {

        String keyword = searchField.getText().trim().toLowerCase();

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (Department dept : departments) {
            String name = dept.getName().toLowerCase();
            String desc = dept.getDescription().toLowerCase();
            if (name.contains(keyword) || desc.contains(keyword)) {
                model.addRow(new Object[]{dept.getId(), dept.getName(), dept.getDescription(), dept.getHandleBy(), "Action"});
            }
        }

        countLabel.setText(model.getRowCount() + " élément(s)");
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
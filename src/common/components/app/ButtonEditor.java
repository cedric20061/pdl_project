package common.components.app;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.*;

public class ButtonEditor extends DefaultCellEditor {

    private JPanel panel;
    private final JButton editButton = new JButton();
    private final JButton deleteButton = new JButton();

    private int currentRow;

    public ButtonEditor(
            JCheckBox checkBox,
            RowAction onEdit,
            RowAction onDelete
    ) {
        super(checkBox);

        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        // Icônes + tooltips
        editButton.setIcon(IconUtils.load("/icons/edit.png", 16, 16));   // icône noire ou gris foncé
        editButton.setToolTipText("Modifier");

        deleteButton.setIcon(IconUtils.load("/icons/delete.png", 16, 16));
        deleteButton.setToolTipText("Supprimer");

        // Style avec fond coloré
        UIStyle.styleIconButton(editButton, UIStyle.PRIMARY_COLOR, UIStyle.PRIMARY_COLOR.brighter(), Color.WHITE);
        UIStyle.styleIconButton(deleteButton, UIStyle.DANGER_COLOR, UIStyle.DANGER_COLOR.brighter(), Color.WHITE);

        panel.add(editButton);
        panel.add(deleteButton);

        // -------------------------
        // Actions dynamiques
        // -------------------------
        editButton.addActionListener(e -> {
            if (onEdit != null) {
                onEdit.execute(currentRow);
            }
            fireEditingStopped();
        });

        deleteButton.addActionListener(e -> {
            if (onDelete != null) {
                fireEditingStopped();
                onDelete.execute(currentRow);
            }
            
        });
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value,
            boolean isSelected, int row, int column
    ) {
        currentRow = row;
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "";
    }
}
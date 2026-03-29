package common.components.app;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

public class ButtonRenderer extends JPanel implements TableCellRenderer {

    private final JButton editButton = new JButton();
    private final JButton deleteButton = new JButton();

    public ButtonRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

        // Icônes + tooltips
        editButton.setIcon(IconUtils.load("/icons/edit.png", 16, 16));   // icône noire ou gris foncé
        editButton.setToolTipText("Modifier");

        deleteButton.setIcon(IconUtils.load("/icons/delete.png", 16, 16));
        deleteButton.setToolTipText("Supprimer");

        // Style avec fond coloré
        UIStyle.styleIconButton(editButton, UIStyle.PRIMARY_COLOR, UIStyle.PRIMARY_COLOR.brighter(), Color.WHITE);
        UIStyle.styleIconButton(deleteButton, UIStyle.DANGER_COLOR, UIStyle.DANGER_COLOR.brighter(), Color.WHITE);

        add(editButton);
        add(deleteButton);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        // Couleur de fond de la ligne
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }

        return this;
    }

    /** Accès aux boutons pour brancher des listeners */
    public JButton getEditButton() {
        return editButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }
}
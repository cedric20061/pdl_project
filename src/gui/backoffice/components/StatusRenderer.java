package gui.backoffice.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class StatusRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        String status = value.toString().toUpperCase();

        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setOpaque(true);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(label.getBackground(), 1, true),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));

        // Reset
        label.setForeground(Color.WHITE);

        switch (status) {
            case "OPEN":
                label.setBackground(new Color(46, 204, 113)); // vert
                break;

            case "CLOSED":
                label.setBackground(new Color(149, 165, 166)); // gris
                break;

            case "VALIDATED":
                label.setBackground(new Color(39, 174, 96)); // vert foncé
                break;

            case "ARCHIVED":
                label.setBackground(new Color(52, 73, 94)); // gris foncé
                break;

            case "PROCESSING":
                label.setBackground(new Color(243, 156, 18)); // orange
                break;

            case "PLANNED":
                label.setBackground(new Color(52, 152, 219)); // bleu
                break;

            default:
                label.setBackground(Color.LIGHT_GRAY);
                label.setForeground(Color.BLACK);
        }

        return label;
    }
}
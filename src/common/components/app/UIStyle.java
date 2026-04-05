package common.components.app;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIStyle {

    // ===============================
    // Couleurs principales de l'application
    // ===============================
    public static final Color PRIMARY_COLOR = new Color(34, 139, 230);       // bleu
    public static final Color SECONDARY_COLOR = new Color(220, 220, 220);    // gris clair
    public static final Color DANGER_COLOR = new Color(220, 53, 69);         // rouge
    public static final Color SUCCESS_COLOR = new Color(40, 167, 69);        // vert
    public static final Color TEXT_COLOR = Color.BLACK;
    public static final Color BACKGROUND_COLOR = Color.WHITE;

    // ===============================
    // Style général des boutons
    // ===============================
    private static void baseButtonStyle(JButton button) {
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    // Bouton primaire (couleur principale)
    public static void stylePrimaryButton(JButton button) {
        baseButtonStyle(button);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        addHoverEffect(button, PRIMARY_COLOR.brighter(), PRIMARY_COLOR);
    }

    // Bouton secondaire (gris)
    public static void styleSecondaryButton(JButton button) {
        baseButtonStyle(button);
        button.setBackground(SECONDARY_COLOR);
        button.setForeground(TEXT_COLOR);

        addHoverEffect(button, SECONDARY_COLOR.darker(), SECONDARY_COLOR);
    }

    // Bouton danger (rouge)
    public static void styleDangerButton(JButton button) {
        baseButtonStyle(button);
        button.setBackground(DANGER_COLOR);
        button.setForeground(Color.WHITE);

        addHoverEffect(button, DANGER_COLOR.brighter(), DANGER_COLOR);
    }

    // Bouton icône seule
    public static void styleIconButton(JButton button, Color hoverColor) {
        baseButtonStyle(button);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setForeground(TEXT_COLOR);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setContentAreaFilled(true);
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setContentAreaFilled(false);
            }
        });
    }

    // ===============================
    // Hover effect générique
    // ===============================
    private static void addHoverEffect(JButton button, Color hoverColor, Color normalColor) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalColor);
            }
        });
    }

    // ===============================
    // Styles pour les labels
    // ===============================
    public static void styleHeaderLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 20)); // ⬅️ augmenté
        label.setForeground(TEXT_COLOR);
    }

    public static void styleSubHeaderLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
    }

    public static void styleSmallLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(TEXT_COLOR.darker());
    }

    // ===============================
    // Styles pour JTextField ou zones de texte
    // ===============================
    public static void styleTextField(JTextField field, int height) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, height));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR));
    }
    public static void styleComboBox(JComboBox<?> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
    }

    // ===============================
    // Bouton icône avec couleur fixe et hover
    // ===============================
    public static void styleIconButton(JButton button, Color backgroundColor, Color hoverColor, Color iconColor) {
        baseButtonStyle(button);

        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setBackground(backgroundColor);
        button.setForeground(iconColor); // couleur de l'icône (pour ImageIcon, on doit préparer l'icône avec la bonne couleur)
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
    }

    // ===============================
    // Style global des JTable
    // ===============================
    public static void styleTable(JTable table) {

        // -------------------------
        // Style général
        // -------------------------
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(Color.DARK_GRAY);

        // -------------------------
        // Header
        // -------------------------
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        header.setOpaque(true);

        // -------------------------
        // Sélection
        // -------------------------
        table.setSelectionBackground(new Color(34, 139, 230, 100));
        table.setSelectionForeground(Color.BLACK);

        // -------------------------
        // Grille
        // -------------------------
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // -------------------------
        // Padding des cellules
        // -------------------------
        DefaultTableCellRenderer padding = new DefaultTableCellRenderer();
        padding.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        table.setDefaultRenderer(Object.class, padding);

        // -------------------------
        // Couleurs alternées + hover
        // -------------------------
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(new Color(34, 139, 230, 80));
                } else {
                    c.setBackground(row % 2 == 0
                            ? new Color(245, 245, 245)
                            : Color.WHITE);
                }

                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                return c;
            }
        });
    }
    
    public static void styleTextField(JTextField field, int width, int height) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(Color.DARK_GRAY);
        field.setBackground(Color.WHITE);
        field.setPreferredSize(new Dimension(width, height));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200,200,200), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
    }

    public static void styleComboBox(JComboBox<?> comboBox, int width) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setForeground(Color.DARK_GRAY);
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(width, 30));
    }

    public static void styleDateChooser(com.toedter.calendar.JDateChooser dateChooser, int width) {
        dateChooser.setPreferredSize(new Dimension(width, 30));
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTextField editor = ((JTextField) dateChooser.getDateEditor().getUiComponent());
        styleTextField(editor, width, 30);
    }

    public static void styleFilterButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 152, 219));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void hideColumn(JTable table, int index) {
        table.getColumnModel().getColumn(index).setMinWidth(0);
        table.getColumnModel().getColumn(index).setMaxWidth(0);
        table.getColumnModel().getColumn(index).setWidth(0);
    }
}
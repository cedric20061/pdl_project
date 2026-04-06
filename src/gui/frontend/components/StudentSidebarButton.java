package gui.frontend.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

/**
 * Custom sidebar button for student frontend navigation.
 * Provides hover effects and consistent styling.
 * 
 * @author Cédric GUIDI && Baptiste DUCROCQ
 * @version 1.0
 */
public class StudentSidebarButton extends JPanel {

    private JButton button;
    private static final Color HOVER_COLOR = new Color(70, 70, 70);
    private static final Color DEFAULT_COLOR = new Color(55, 55, 55);
    private static final Color TEXT_COLOR = Color.WHITE;

    public StudentSidebarButton(String text, ActionListener actionListener) {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setOpaque(false);
        this.setMaximumSize(new Dimension(200, 40));
        this.setPreferredSize(new Dimension(200, 40));
        this.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBackground(DEFAULT_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(LEFT_ALIGNMENT);

        button.addActionListener(actionListener);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(DEFAULT_COLOR);
            }
        });

        this.add(button);
    }
}

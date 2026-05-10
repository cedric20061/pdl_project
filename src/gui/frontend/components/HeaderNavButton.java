package gui.frontend.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import common.components.app.IconUtils;

public class HeaderNavButton extends JButton {

    private String pageName;
    private boolean active = false;

    public HeaderNavButton(String text, String iconPath, String pageName) {
        super(text);

        this.pageName = pageName;

        // Icône
        if (iconPath != null && !iconPath.isEmpty()) {
            setIcon(IconUtils.load(iconPath, 20, 20));
            setHorizontalAlignment(SwingConstants.LEFT);
            setIconTextGap(10);
        }

        // Style général
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setForeground(Color.WHITE);
        setBackground(new Color(44, 62, 80));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        // Hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!active) {
                    setBackground(new Color(52, 73, 94));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!active) {
                    setBackground(new Color(44, 62, 80));
                }
            }
        });
    }

    public void setActive(boolean isActive) {
        this.active = isActive;
        if (active) {
            setBackground(new Color(52, 152, 219)); // couleur page active
            setForeground(Color.WHITE);
        } else {
            setBackground(new Color(44, 62, 80)); // couleur normale
            setForeground(Color.WHITE);
        }
    }

    public String getPageName() {
        return pageName;
    }
}
package gui.frontend.utils;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

public class Tools {
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(new Color(90, 90, 90));
        return label;
    }
}

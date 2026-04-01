package gui.backoffice.utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import common.components.app.UIStyle;

public class PanelsUtils {
    
    public static void createHeader(
            String titleText,
            String subtitleText,
            JPanel parentPanel,
            ActionListener onAddClick
    ) {

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel(titleText);
        UIStyle.styleHeaderLabel(title);

        JLabel subtitle = new JLabel(subtitleText);
        UIStyle.styleSmallLabel(subtitle);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        JButton addButton = new JButton("+ Ajouter");
        UIStyle.stylePrimaryButton(addButton);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // 👉 Action dynamique
        if (onAddClick != null) {
            addButton.addActionListener(onAddClick);
        }

        header.add(titlePanel, BorderLayout.WEST);
        header.add(addButton, BorderLayout.EAST);

        parentPanel.add(header);
    }
    
}

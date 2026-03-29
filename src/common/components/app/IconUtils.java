package common.components.app;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class IconUtils {

    public static ImageIcon load(String path) {
        URL resource = IconUtils.class.getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("Icon not found: " + path);
        }
        return new ImageIcon(resource);
    }

    public static ImageIcon load(String path, int width, int height) {
        ImageIcon icon = load(path);
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}
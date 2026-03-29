package common.components.app;


import javax.swing.*;

public class IconLabel extends JLabel {

    private String path;
    private int width;
    private int height;

    public IconLabel(String path, int width, int height) {
        this.path = path;
        this.width = width;
        this.height = height;

        setHorizontalAlignment(CENTER);
        setIcon(IconUtils.load(path, width, height));
    }

    public void setIconPath(String path) {
        this.path = path;
        updateIcon();
    }

    public void setIconSize(int width, int height) {
        this.width = width;
        this.height = height;
        updateIcon();
    }

    private void updateIcon() {
        setIcon(IconUtils.load(path, width, height));
    }
}
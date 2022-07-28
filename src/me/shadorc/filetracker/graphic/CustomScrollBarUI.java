package me.shadorc.filetracker.graphic;

import javax.swing.*;
import javax.swing.plaf.metal.MetalScrollBarUI;
import java.awt.*;
import java.net.URL;
import java.util.Objects;

public class CustomScrollBarUI extends MetalScrollBarUI {

    private static final JButton INVISIBLE_BUTTON = new JButton();

    static {
        INVISIBLE_BUTTON.setPreferredSize(new Dimension());
    }

    private final Image imageThumb;

    CustomScrollBarUI() {
        URL iconUrl = Objects.requireNonNull(this.getClass().getResource("/res/scrollbar.png"));
        this.imageThumb = new ImageIcon(iconUrl).getImage();
    }

    @Override
    protected void paintThumb(Graphics graphics, JComponent component, Rectangle thumbBounds) {
        graphics.drawImage(imageThumb, thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, null);
    }

    @Override
    protected void paintTrack(Graphics graphics, JComponent component, Rectangle trackBounds) {
        component.setOpaque(false);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return INVISIBLE_BUTTON;
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return INVISIBLE_BUTTON;
    }

}
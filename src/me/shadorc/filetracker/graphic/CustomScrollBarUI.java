package me.shadorc.filetracker.graphic;

import javax.swing.*;
import javax.swing.plaf.metal.MetalScrollBarUI;
import java.awt.*;

public class CustomScrollBarUI extends MetalScrollBarUI {

    private final Image imageThumb;

    CustomScrollBarUI() {
        this.imageThumb = new ImageIcon(this.getClass().getResource("/res/scrollbar.png")).getImage();
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        g.drawImage(imageThumb, thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, null);
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        c.setOpaque(false);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return this.createInvisibleButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return this.createInvisibleButton();
    }

    private JButton createInvisibleButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        return button;
    }
}
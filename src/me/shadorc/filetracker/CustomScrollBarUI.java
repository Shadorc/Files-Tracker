package me.shadorc.filetracker;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.metal.MetalScrollBarUI;

public class CustomScrollBarUI extends MetalScrollBarUI {

	private Image imageThumb;

	public CustomScrollBarUI() {
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
		return createInvisibleButton();
	}

	@Override    
	protected JButton createIncreaseButton(int orientation) {
		return createInvisibleButton();
	}

	private JButton createInvisibleButton() {
		JButton button = new JButton();
		button.setPreferredSize(new Dimension(0, 0));
		return button;
	}
}
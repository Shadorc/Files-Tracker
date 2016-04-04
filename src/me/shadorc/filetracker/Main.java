package me.shadorc.filetracker;

import javax.swing.SwingUtilities;

public class Main {

	private static Frame frame;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame = new Frame();
			}
		});
	}

	public static void updateFrame() {
		frame.revalidate();
		frame.repaint();
	}
}

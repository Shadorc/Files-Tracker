package me.shadorc.filetracker;

import java.io.IOException;

import javax.swing.SwingUtilities;

public class Main {

	private static Frame frame;

	public static void main(String[] args) {

		try {
			Storage.init();
		} catch (IOException e) {
			Utils.showErrorDialog("Aborting, an error occured while creating config file : " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

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

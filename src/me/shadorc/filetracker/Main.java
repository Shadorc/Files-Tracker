package me.shadorc.filetracker;

import java.awt.Font;
import java.util.Date;

import javax.swing.SwingUtilities;

public class Main {

	public static void main(String[] args) {

		//		Storage2.init();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Frame();
			}
		});
	}

	public static Font getFont() {
		return new Font("Tahoma", Font.PLAIN, 12);
	}

	public static int daysBetween(Date d1, Date d2){
		return (int) Math.abs((d2.getTime()-d1.getTime())/(1000*60*60*24));
	}
}

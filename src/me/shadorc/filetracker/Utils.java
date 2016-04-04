package me.shadorc.filetracker;

import java.awt.Font;
import java.io.File;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Utils {

	public static boolean isOlder(Date date, String time) {
		return Math.abs((new Date().getTime()-date.getTime())/(1000*60*60*24)) <= Integer.parseInt(time);
	}

	public static Font getFont() {
		return new Font("Tahoma", Font.PLAIN, 12);
	}

	public static String toReadableByteCount(long bytes) {
		if (bytes < 1000) return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(1000));
		char pre = "kMGTPE".charAt(exp-1);
		return String.format("%.1f %sB", bytes / Math.pow(1000, exp), pre);
	}

	public static int confirmDeletion(File file) {
		int choice = JOptionPane.showConfirmDialog(null, 
				"Do you really want to DEFINITIVELY delete this file : " + file + " ?",
				"Files Tracker - Delete file",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.PLAIN_MESSAGE,
				new ImageIcon(Utils.class.getResource("/res/icon.png")));

		return choice;
	}
}

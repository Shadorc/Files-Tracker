package me.shadorc.filetracker;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.DosFileAttributeView;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Utils {

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

	public static boolean isOlder(Date date, String time) {
		return Math.abs((new Date().getTime()-date.getTime())/(1000*60*60*24)) <= Integer.parseInt(time);
	}

	public static boolean isSystemFile(File file) {
		try {
			DosFileAttributeView dosAttr = Files.getFileAttributeView(file.toPath(), DosFileAttributeView.class);
			return dosAttr.readAttributes().isSystem();
		} catch (IOException e) {
			return false; //DosFileAttributeView not supported
		}
	}

	public static boolean delete(File file) {
		if(file.isDirectory()) {
			File[] files = file.listFiles();
			if(files != null){
				for(File f : files) {
					if(f.isDirectory()) {
						Utils.delete(f);
					} else {
						f.delete();
					}
				}
			}
		}
		return file.delete();
	}
}

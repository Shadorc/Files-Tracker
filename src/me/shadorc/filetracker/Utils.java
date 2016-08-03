package me.shadorc.filetracker;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.DosFileAttributeView;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import me.shadorc.filetracker.graphic.CustomNode;

public class Utils {

	public final static ImageIcon ICON = new ImageIcon(Utils.class.getResource("/res/icon.png"));
	public final static Font DEFAULT_FONT = new Font("Tahoma", Font.PLAIN, 12);

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
				ICON);

		return choice;
	}

	public static void showErrorDialog(Exception err, String message) {
		JOptionPane.showMessageDialog(null, message, "Files Tracker - Error", JOptionPane.ERROR_MESSAGE, ICON);
		if(err != null) err.printStackTrace();
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

	public static void collapseAll(JTree tree, TreePath path) {
		CustomNode node = (CustomNode) path.getLastPathComponent();

		if (node.getChildCount() >= 0) {
			Enumeration <?> enumeration = node.children();
			while (enumeration.hasMoreElements()) {
				CustomNode n = (CustomNode) enumeration.nextElement();
				TreePath p = path.pathByAddingChild(n);

				Utils.collapseAll(tree, p);
			}
		}

		tree.collapsePath(path);
	}
}

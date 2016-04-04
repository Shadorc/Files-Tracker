package me.shadorc.filetracker;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

public class CustomNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;

	public static CustomNode EMPTY_NODE = new CustomNode("(Empty)");

	private File file;
	private ImageIcon icon;
	private Color color;

	public CustomNode(Object userObject, File file) {
		super(userObject);
		this.file = file;
		this.color = Color.BLACK;

		String iconName = (file.isDirectory() ? "folder" : "file") + "-icon";
		if(Utils.isSystemFile(file)) iconName += "-locked";
		else if(file.isHidden()) 	 iconName += "-hidden";

		this.icon = new ImageIcon(this.getClass().getResource("/res/" + iconName + ".png"));
	}

	public CustomNode(Object userObject) {
		super(userObject);
	}

	public File getFile() {
		return file;
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public Color getColor() {
		return color;
	}

	public Date lastModifiedDate() {
		return new Date(file.lastModified());
	}

	public Date createdDate() {
		try {
			BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
			return new Date(attr.creationTime().toMillis());
		} catch (IOException ignored) { }
		return null;
	}

	public boolean isEmpty() {
		return this.toString().equals(EMPTY_NODE.toString());
	}

	public void setColor(Color color) {
		this.color = color;
	}
}

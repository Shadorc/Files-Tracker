package me.shadorc.filetracker;

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

	public CustomNode(Object userObject, File file) {
		super(userObject);
		this.file = file;
		this.icon = new ImageIcon(this.getClass().getResource("/res/" + (file.isDirectory() ? "folder" : "file") + "-icon" + (file.isHidden() ? "-hidden" : "") + ".png"));
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
	
	public Date lastModifiedDate() {
		return new Date(file.lastModified());
	}

	public Date createdDate() throws IOException {
		BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
		return new Date(attr.creationTime().toMillis());
	}
}

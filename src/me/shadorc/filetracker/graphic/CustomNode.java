package me.shadorc.filetracker.graphic;

import me.shadorc.filetracker.Utils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class CustomNode extends DefaultMutableTreeNode {

    public static final CustomNode EMPTY_NODE = new CustomNode("(Empty)");

    private File file;
    private ImageIcon icon;
    private Color color;

    public CustomNode(String name, File file) {
        super(name);

        this.file = file;
        this.color = Color.BLACK;

        final StringBuilder iconName = new StringBuilder();
        iconName.append(file.isDirectory() ? "folder" : "file");
        iconName.append("-icon");
        if (Utils.isSystemFile(file)) {
            iconName.append("-locked");
        } else if (file.isHidden()) {
            iconName.append("-hidden");
        }

        final URL iconUrl = this.getClass().getResource(String.format("/res/%s.png", iconName));
        if (iconUrl == null) {
            throw new RuntimeException(String.format("Icon %s not found", iconName));
        }
        this.icon = new ImageIcon(iconUrl);
    }

    public CustomNode(String name) {
        super(name);
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

    public LocalDateTime lastModifiedDate() {
        return Instant.ofEpochMilli(file.lastModified())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public LocalDateTime createdDate() {
        try {
            BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            return Instant.ofEpochMilli(attr.creationTime().toMillis())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (IOException err) {
            err.printStackTrace();
        }

        return null;
    }

    // TODO: Based on name is not really consistent
    public boolean isEmpty() {
        return this.toString().equals(EMPTY_NODE.toString());
    }

    public void setColor(Color color) {
        this.color = color;
    }
}

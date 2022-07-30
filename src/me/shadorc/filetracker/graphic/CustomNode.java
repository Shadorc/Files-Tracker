package me.shadorc.filetracker.graphic;

import me.shadorc.filetracker.Storage;
import me.shadorc.filetracker.Utils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomNode extends DefaultMutableTreeNode {

    public static final CustomNode EMPTY_NODE = new CustomNode("(Empty)");

    public static final Color DEFAULT_COLOR = Color.BLACK;
    public static final Color EMPTY_COLOR = Color.GRAY;
    public static final Color RECENTLY_CREATED_COLOR = new Color(0, 100, 0);
    public static final Color RECENTLY_MODIFIED_COLOR = new Color(255, 128, 0);

    private static final Map<String, ImageIcon> ICON_CACHE = new ConcurrentHashMap<>(6);

    private File file;
    private ImageIcon icon;
    private Color color;

    public CustomNode(String name, File file) {
        super(name);

        this.file = file;
        this.color = DEFAULT_COLOR;

        final StringBuilder iconName = new StringBuilder();
        iconName.append(file.isDirectory() ? "folder" : "file");
        iconName.append("-icon");
        if (Utils.isSystemFile(file)) {
            iconName.append("-locked");
        } else if (file.isHidden()) {
            iconName.append("-hidden");
        }

        this.icon = ICON_CACHE.computeIfAbsent(String.format("/res/%s.png", iconName),
                path -> {
                    URL url = this.getClass().getResource(path);
                    if (url == null) {
                        throw new RuntimeException(String.format("Icon %s not found", path));
                    }
                    return new ImageIcon(url);
                });
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

    public void updateColor() {
        Color color = this.color;

        // Empty file
        if (file.isDirectory() && (file.listFiles() == null || file.listFiles().length == 0)) {
            color = EMPTY_COLOR;
        }
        // Recently created
        else if (Storage.getBool(Storage.Data.SHOW_CREATED)
                && this.createdDate() != null
                && Utils.isOlder(this.createdDate(), Storage.getDuration(Storage.Data.CREATED_TIME_DAY))) {
            color = RECENTLY_CREATED_COLOR;
        }
        // Recently modified
        else if (Storage.getBool(Storage.Data.SHOW_MODIFIED)
                && this.lastModifiedDate() != null
                && Utils.isOlder(this.lastModifiedDate(), Storage.getDuration(Storage.Data.MODIFIED_TIME_DAY))) {
            color = RECENTLY_MODIFIED_COLOR;
        }

        this.setColor(color);
    }

    private void setColor(Color color) {
        this.color = color;

        if (color != CustomNode.DEFAULT_COLOR && color != CustomNode.EMPTY_COLOR) {
            // Set the same color for all its parents
            for (TreeNode parent : this.getPath()) {
                if (parent == this) {
                    break;
                }
                ((CustomNode) parent).setColor(color);
            }
        }
    }
}

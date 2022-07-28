package me.shadorc.filetracker;

import me.shadorc.filetracker.graphic.CustomNode;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.DosFileAttributeView;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.Objects;

public class Utils {

    public final static ImageIcon ICON = new ImageIcon(Objects.requireNonNull(
            Utils.class.getResource("/res/icon.png")));
    public final static Font DEFAULT_FONT = new Font("Tahoma", Font.PLAIN, 12);

    public static File getDefaultDisk() {
        File[] roots = File.listRoots();

        if (roots == null || roots.length == 0) {
            return null;
        }

        for (File file : roots) {
            if (file.listFiles() != null) {
                return file;
            }
        }

        return roots[0];
    }

    public static String toReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) {
            return String.format("%d B", bytes);
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        char pre = "kMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static int showConfirmDeletion(File file) {
        return JOptionPane.showConfirmDialog(null,
                "Do you really want to DEFINITIVELY delete this file: " + file + " ?",
                "Files Tracker - Delete file",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                ICON);
    }

    public static void showErrorDialog(Exception err, String message) {
        JOptionPane.showMessageDialog(
                null, message, "Files Tracker - Error", JOptionPane.ERROR_MESSAGE, ICON);

        if (err != null) {
            err.printStackTrace();
        }
    }

    public static boolean isOlder(LocalDateTime date, Duration duration) {
        return Duration.between(date, LocalDateTime.now()).compareTo(duration) <= 0;
    }

    public static boolean isSystemFile(File file) {
        try {
            DosFileAttributeView dosAttr = Files.getFileAttributeView(file.toPath(), DosFileAttributeView.class);
            return dosAttr.readAttributes().isSystem();
        } catch (IOException e) {
            return false; // DosFileAttributeView not supported
        }
    }

    public static boolean delete(File file) {
        boolean success = true;

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    if (subFile.isDirectory()) {
                        Utils.delete(subFile);
                    } else {
                        success &= subFile.delete();
                    }
                }
            }
        }

        success &= file.delete();
        return success;
    }

    public static void collapseAll(JTree tree, TreePath parentPath) {
        CustomNode parentNode = (CustomNode) parentPath.getLastPathComponent();

        if (parentNode.getChildCount() >= 0) {
            Enumeration<TreeNode> enumeration = parentNode.children();
            while (enumeration.hasMoreElements()) {
                CustomNode subNode = (CustomNode) enumeration.nextElement();
                TreePath subPath = parentPath.pathByAddingChild(subNode);

                Utils.collapseAll(tree, subPath);
            }
        }

        tree.collapsePath(parentPath);
    }
}

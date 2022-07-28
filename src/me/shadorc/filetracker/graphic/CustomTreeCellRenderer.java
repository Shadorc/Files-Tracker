package me.shadorc.filetracker.graphic;

import me.shadorc.filetracker.Storage;
import me.shadorc.filetracker.Storage.Data;
import me.shadorc.filetracker.Utils;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.time.LocalDateTime;

public class CustomTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        JPanel panel = new JPanel();
        panel.setBackground(selected ? Color.LIGHT_GRAY : null);

        CustomNode node = (CustomNode) value;

        if (node.getFile() != null && !node.getFile().exists()) {
            ((DefaultTreeModel) tree.getModel()).removeNodeFromParent(node);
            return panel;
        }

        node.setColor(Color.BLACK);

        //Empty file
        if (node.getChildCount() == 1 && ((CustomNode) node.getFirstChild()).isEmpty() || node.isEmpty()) {
            node.setColor(Color.GRAY);
        } else {
            // Recently modified
            final LocalDateTime lastModified = node.lastModifiedDate();
            if (lastModified != null
                    && Utils.isOlder(lastModified, Storage.getDuration(Data.MODIFIED_TIME_DAY))
                    && Storage.getBool(Data.SHOW_MODIFIED)) {
                node.setColor(new Color(255, 128, 0));
            }

            //Recently created
            final LocalDateTime lastCreated = node.createdDate();
            if (lastCreated != null
                    && Utils.isOlder(lastCreated, Storage.getDuration(Data.CREATED_TIME_DAY))
                    && Storage.getBool(Data.SHOW_CREATED)) {
                node.setColor(new Color(0, 100, 0));
            }
        }

        //Set the same color for all its parents
        if (node.getColor() != Color.BLACK && node.getColor() != Color.GRAY) {
            for (TreeNode parent : node.getPath()) {
                CustomNode cuParent = (CustomNode) parent;
                //Change color only if it has not already change
                if (cuParent.getColor() == Color.BLACK || cuParent.getColor() == Color.GRAY) {
                    cuParent.setColor(node.getColor());
                }
            }
        }

        JLabel label = new JLabel(String.valueOf(node.getUserObject()));
        label.setForeground(node.getColor());
        label.setFont(Utils.DEFAULT_FONT);
        label.setIcon(node.getIcon());
        panel.add(label);

        return panel;
    }
}
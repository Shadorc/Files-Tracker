package me.shadorc.filetracker.graphic;

import me.shadorc.filetracker.Utils;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

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

        JLabel label = new JLabel(node.getUserObject().toString());
        label.setForeground(node.getColor());
        label.setFont(Utils.DEFAULT_FONT);
        label.setIcon(node.getIcon());
        panel.add(label);

        return panel;
    }
}
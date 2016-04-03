package me.shadorc.filetracker;

import java.awt.Color;
import java.awt.Component;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import me.shadorc.filetracker.Storage.Data;

public class CustomTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

		JPanel panel = new JPanel();
		panel.setBackground(selected ? Color.LIGHT_GRAY : null);

		CustomNode node = (CustomNode) value; 

		if(!node.getFile().exists()) {
			((DefaultTreeModel) tree.getModel()).removeNodeFromParent(node);
			return panel;
		}

		//Empty file
		if(node.getChildCount() == 1 && ((CustomNode) node.getFirstChild()).isEmpty() || node.isEmpty()) {
			node.setColor(Color.GRAY);
		} 

		else {
			//Recently modified
			if(Utils.daysBetween(new Date(), node.lastModifiedDate()) <= Integer.parseInt(Storage.getData(Data.MODIFIED_TIME_DAY))) {
				node.setColor(new Color(255, 128, 0));
			}

			//Recently created
			if(node.createdDate() != null) {
				if(Utils.daysBetween(new Date(), node.createdDate()) <= Integer.parseInt(Storage.getData(Data.CREATED_TIME_DAY))) {
					node.setColor(new Color(0, 100, 0));
				}
			}
		}

		//Set the same color for all its parents
		if(node.getColor() != Color.BLACK) {
			for(TreeNode parent : node.getPath()) {
				((CustomNode) parent).setColor(node.getColor());
			}
		}

		JLabel label = new JLabel(String.valueOf(node.getUserObject()));
		label.setForeground(node.getColor());
		label.setFont(Utils.getFont());
		label.setIcon(node.getIcon());
		panel.add(label);

		return panel;
	}
}
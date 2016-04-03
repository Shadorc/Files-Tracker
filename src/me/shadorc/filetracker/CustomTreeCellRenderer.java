package me.shadorc.filetracker;

import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import me.shadorc.filetracker.Storage.Data;

public class CustomTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

		JPanel panel = new JPanel();
		panel.setBackground(null);

		CustomNode node = (CustomNode) value; 

		if(!node.getFile().exists()) {
			((DefaultTreeModel) tree.getModel()).removeNodeFromParent(node);
			return panel;
		}

		String text = (String) node.getUserObject();

		JLabel label = new JLabel(text);
		label.setForeground(Color.BLACK);
		label.setFont(Utils.getFont());
		label.setIcon(node.getIcon());
		panel.add(label);

		//Empty file
		if(node.getChildCount() == 1 && node.getFirstChild().toString().equals(CustomNode.EMPTY_NODE.toString()) || node.toString().equals(CustomNode.EMPTY_NODE.toString())) {
			label.setForeground(Color.GRAY);
		} 

		else {
			//Recently modified
			if(Utils.daysBetween(new Date(), node.lastModifiedDate()) <= Integer.parseInt(Storage.getData(Data.MODIFIED_TIME_DAY))) {
				label.setForeground(new Color(255, 128, 0));
			}

			//Recently created
			try {
				if(Utils.daysBetween(new Date(), node.createdDate()) <= Integer.parseInt(Storage.getData(Data.CREATED_TIME_DAY))) {
					label.setForeground(new Color(0, 100, 0));
				}
			} catch(IOException e) {
				System.out.println("[WARNING] Get file creation time isn't supported : " + e.getMessage());
			}
		}

		if(selected) {
			panel.setBackground(Color.LIGHT_GRAY);
		}

		return panel;
	}
}
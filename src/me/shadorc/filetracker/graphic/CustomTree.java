package me.shadorc.filetracker.graphic;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import me.shadorc.filetracker.Utils;

public class CustomTree extends JTree {

	private static final long serialVersionUID = 1L;

	CustomTree(TreeNode root) {
		super(root);

		this.setRowHeight(22);
		this.setCellRenderer(new CustomTreeCellRenderer());

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent event) {
				TreePath path = CustomTree.this.getPathForLocation(event.getX(), event.getY());

				if(path != null) {
					CustomTree.this.setSelectionPath(path);
					CustomNode node = (CustomNode) CustomTree.this.getLastSelectedPathComponent();
					File file = (node.getFile() != null) ? node.getFile() : ((CustomNode) node.getParent()).getFile();

					//Open right click drop-down menu
					if(event.isPopupTrigger()) {
						JPopupMenu menu = new JPopupMenu();
						menu.setBackground(Color.WHITE);

						JMenuItem openItem = new JMenuItem(new AbstractAction("Open folder") {
							private static final long serialVersionUID = 1L;

							public void actionPerformed(ActionEvent event) {
								if(Desktop.isDesktopSupported()) {
									try {
										Desktop.getDesktop().open(file.isDirectory() ? file : file.getParentFile());
									} catch (IOException err) {
										Utils.showErrorDialog(err, "Error while opening " + file + " : " + err.getMessage());
									}
								}
							}
						});
						openItem.setOpaque(false);
						openItem.setForeground(Color.BLACK);
						openItem.setFont(Utils.DEFAULT_FONT);
						menu.add(openItem);

						JMenuItem deleteItem = new JMenuItem(new AbstractAction("Delete") {
							private static final long serialVersionUID = 1L;

							public void actionPerformed(ActionEvent event) {
								if(Utils.isSystemFile(file)) {
									Utils.showErrorDialog(null, "Deleting a system file is not allowed.");
								} 
								else if(Utils.showConfirmDeletion(file) == JOptionPane.YES_OPTION) {
									Utils.delete(file);
									((DefaultTreeModel) CustomTree.this.getModel()).removeNodeFromParent(node);
								}
							}
						});
						deleteItem.setOpaque(false);
						deleteItem.setForeground(Color.BLACK);
						deleteItem.setFont(Utils.DEFAULT_FONT);
						menu.add(deleteItem);

						menu.show(event.getComponent(), event.getX(), event.getY());
					}
				}
			}
		});
	}

	public void add(CustomNode parent, CustomNode child) {
		DefaultTreeModel model = (DefaultTreeModel) this.getModel();

		//Directories are displayed at the top and files at the bottom
		int index = parent.getChildCount();
		if(child.getFile() != null && child.getFile().isDirectory()) {
			index = 0;
			for(int i = 0; i < parent.getChildCount(); i++) {
				if(parent.getChildAt(i).getChildCount() == 0) break;
				index++;
			}
		}

		model.insertNodeInto(child, parent, index);
	}
}

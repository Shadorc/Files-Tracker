package me.shadorc.filetracker;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class CustomTree extends JTree {

	private static final long serialVersionUID = 1L;

	public CustomTree(TreeNode root) {
		super(root);

		this.setRowHeight(22);
		this.setCellRenderer(new CustomTreeCellRenderer());

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent event) {
				TreePath path = CustomTree.this.getPathForLocation(event.getX(), event.getY());

				if (path != null) {
					CustomTree.this.setSelectionPath(path);

					//Open right click drop-down menu
					if(event.isPopupTrigger()) {
						JPopupMenu menu = new JPopupMenu();
						menu.setBackground(Color.WHITE);

						JMenuItem openItem = new JMenuItem(new AbstractAction("Open folder") {
							private static final long serialVersionUID = 1L;

							public void actionPerformed(ActionEvent event) {
								File file = ((CustomNode) CustomTree.this.getLastSelectedPathComponent()).getFile();
								if(file != null && Desktop.isDesktopSupported()) {
									try {
										Desktop.getDesktop().open(file.isDirectory() ? file : file.getParentFile());
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						});
						openItem.setOpaque(false);
						openItem.setForeground(Color.BLACK);
						openItem.setFont(Utils.getFont());
						menu.add(openItem);

						JMenuItem deleteItem = new JMenuItem(new AbstractAction("Delete") {
							private static final long serialVersionUID = 1L;

							public void actionPerformed(ActionEvent event) {
								File file = ((CustomNode) CustomTree.this.getLastSelectedPathComponent()).getFile();
								if(file != null) file.delete();
							}
						});
						deleteItem.setOpaque(false);
						deleteItem.setForeground(Color.BLACK);
						deleteItem.setFont(Utils.getFont());
						menu.add(deleteItem);

						menu.show(event.getComponent(), event.getX(), event.getY());
					}
				}
			}
		});
	}

	public void add(CustomNode parent, CustomNode child) {
		DefaultTreeModel model = (DefaultTreeModel) this.getModel();
		int index = (child.getFile() != null && child.getFile().isDirectory()) ? 0 : model.getChildCount(parent);
		model.insertNodeInto(child, parent, index);
	}
}

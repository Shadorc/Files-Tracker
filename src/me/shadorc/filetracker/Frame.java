package me.shadorc.filetracker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.tree.TreePath;

import me.shadorc.filetracker.Storage.Data;

public class Frame extends JFrame {

	private static final long serialVersionUID = 1L;

	private HashMap <File, CustomNode> directories;
	private long startTime, lastUpdate, filesCount;
	private boolean stop;

	private JPanel mainPanel;
	private JLabel infoLabel;
	private JButton scanButton;
	private CustomTree tree;

	Frame() {
		super("Files Tracker - Bêta");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		OptionsFrame optionsFrame = new OptionsFrame();
		stop = true;

		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(Color.WHITE);

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);

		JTextField jtf = new JTextField("C:\\");
		topPanel.add(jtf, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel(new GridLayout(1, 3));

		JButton browseButton = this.createBu("Browse", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception ignored) { }

				File defaultFile = new File("C://");
				JFileChooser chooser = new JFileChooser(defaultFile.exists() ? defaultFile : null);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					jtf.setText(chooser.getSelectedFile().getPath());
				}	

				try {
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				} catch (Exception ignore) { }
			}
		});
		buttonsPanel.add(browseButton);

		scanButton = this.createBu("Scan", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton bu = (JButton) e.getSource();
				if(stop) {
					File folder = new File(jtf.getText());
					if(!folder.exists()) {
						JOptionPane.showMessageDialog(null, "Sorry, the path you picked is not a directory or does not exist", "Files Tracker - Error", JOptionPane.ERROR_MESSAGE);
					} else {
						bu.setText("Stop");
						Frame.this.start(folder);
					}
				} else {
					bu.setText("Scan");
				}
				stop = !stop;
			}
		});
		buttonsPanel.add(scanButton);

		JButton options = this.createBu("Options", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				optionsFrame.setVisible(true);
			}
		});
		buttonsPanel.add(options, BorderLayout.PAGE_END);

		topPanel.add(buttonsPanel, BorderLayout.EAST);
		mainPanel.add(topPanel, BorderLayout.PAGE_START);

		JLabel loading = new JLabel("",  new ImageIcon(this.getClass().getResource("/res/large-icon.png")), JLabel.CENTER);
		mainPanel.add(loading, BorderLayout.CENTER);

		/*
		 * KEYS PANEL
		 */
		JPanel keysPanel = new JPanel(new GridLayout(17, 1));
		keysPanel.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Keys"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		JLabel normalFile = new JLabel("Normal", new ImageIcon(this.getClass().getResource("/res/file-icon.png")), JLabel.LEFT);
		normalFile.setFont(Utils.getFont());
		keysPanel.add(normalFile);

		JLabel hiddenFile = new JLabel("Hidden", new ImageIcon(this.getClass().getResource("/res/file-icon-hidden.png")), JLabel.LEFT);
		hiddenFile.setFont(Utils.getFont());
		keysPanel.add(hiddenFile);

		JLabel lockedFile = new JLabel("System", new ImageIcon(this.getClass().getResource("/res/file-icon-locked.png")), JLabel.LEFT);
		lockedFile.setFont(Utils.getFont());
		keysPanel.add(lockedFile);

		JLabel emptyFile = new JLabel("Empty");
		emptyFile.setForeground(Color.GRAY);
		emptyFile.setFont(Utils.getFont());
		keysPanel.add(emptyFile);

		JLabel modifiedFile = new JLabel("Recently modified");
		modifiedFile.setForeground(new Color(255, 128, 0));
		modifiedFile.setFont(Utils.getFont());
		keysPanel.add(modifiedFile);

		JLabel createdFile = new JLabel("Recently created");
		createdFile.setForeground(new Color(0, 100, 0));
		createdFile.setFont(Utils.getFont());
		keysPanel.add(createdFile);

		mainPanel.add(keysPanel, BorderLayout.EAST);

		/*
		 * ANALYZE PANEL
		 */
		JPanel infoPanel = new JPanel();
		infoPanel.setOpaque(false);

		infoLabel = new JLabel("Select a folder to scan...");
		infoLabel.setFont(Utils.getFont());
		infoLabel.setForeground(Color.BLACK);		
		infoPanel.add(infoLabel);

		mainPanel.add(infoPanel, BorderLayout.SOUTH);

		this.setContentPane(mainPanel);
		this.pack();
		this.setIconImage(new ImageIcon(this.getClass().getResource("/res/icon.png")).getImage());
		this.setMinimumSize(new Dimension(800, 600));
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private void start(File rootFile) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				directories = new HashMap <File, CustomNode> ();
				startTime = System.currentTimeMillis();
				filesCount = 0;

				//Remove the loading icon
				BorderLayout layout = (BorderLayout) mainPanel.getLayout();
				mainPanel.remove(layout.getLayoutComponent(BorderLayout.CENTER));

				CustomNode rootNode = new CustomNode(rootFile.getPath(), rootFile);
				directories.put(rootFile, rootNode);

				tree = new CustomTree(rootNode);
				JScrollPane jsp = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				jsp.getVerticalScrollBar().setUI(new CustomScrollBarUI());
				jsp.getHorizontalScrollBar().setUI(new CustomScrollBarUI());

				mainPanel.add(jsp, BorderLayout.CENTER);

				Frame.this.search(rootFile);
				scanButton.doClick(); //Reset scan button
			}
		}).start();
	}

	private void search(File parent) {
		File[] files = parent.listFiles();

		if(!stop && files != null) {
			for(File child : files) {
				//We check if file exists because there's some very weird bugs with $Recycle.Bin for example
				if(!child.exists() || (Utils.isSystemFile(child) && !Boolean.valueOf(Storage.getData(Data.SHOW_SYSTEM_DIR)))) continue;
				this.addFile(parent, child);
				if(child.isDirectory()) {
					this.search(child);
				}
			}
		}
	}

	private void addFile(File parent, File child) {
		tree.expandPath(new TreePath(tree.getModel().getRoot()));

		String size = child.isFile() ? "(" + Utils.toReadableByteCount(child.length()) + ")" : "";
		CustomNode childNode = new CustomNode(child.getName() + " " + size, child);

		tree.add(directories.get(parent), childNode);

		if(child.isDirectory()) {
			directories.put(child, childNode);

			//If the directory is empty
			File[] listFiles = child.listFiles();
			if(listFiles == null || listFiles.length == 0) {
				tree.add(childNode, (CustomNode) CustomNode.EMPTY_NODE.clone());
			}
		}

		filesCount++;
		if((System.currentTimeMillis() - lastUpdate) >= 100) {
			infoLabel.setText(filesCount + " files analyzed in " + String.format("%.1f", (System.currentTimeMillis() - startTime)/1000.0) + "s.");
			lastUpdate = System.currentTimeMillis();
		}
	}

	private JButton createBu(String text, ActionListener listener) {
		JButton button = new JButton(text);
		button.setFocusable(false);
		button.setFont(Utils.getFont());
		button.setBackground(Color.WHITE);
		button.setForeground(Color.BLACK);
		button.addActionListener(listener);
		return button;
	}
}

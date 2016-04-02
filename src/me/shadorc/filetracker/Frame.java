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

public class Frame extends JFrame {

	private static final long serialVersionUID = 1L;

	private HashMap <File, CustomNode> directories;
	private long startTime, lastUpdate, filesCount;

	private JPanel mainPanel;
	private JLabel infoLabel;
	private CustomTree tree;

	Frame() {
		super("Files Tracker");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		OptionsFrame optionsFrame = new OptionsFrame();
		directories = new HashMap <File, CustomNode> ();

		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(Color.WHITE);

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);

		JTextField jtf = new JTextField("C:\\");
		topPanel.add(jtf, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel(new GridLayout(1, 3));

		JButton browseButton = new JButton("Browse");
		browseButton.setFont(Main.getFont());
		browseButton.setBackground(Color.WHITE);
		browseButton.setForeground(Color.BLACK);
		browseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignore) { }

				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					jtf.setText(chooser.getSelectedFile().getPath());
				}	

				try {
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignore) { }
			}
		});
		buttonsPanel.add(browseButton);

		JButton scanButton = new JButton("Scan");
		scanButton.setFont(Main.getFont());
		scanButton.setBackground(Color.WHITE);
		scanButton.setForeground(Color.BLACK);
		scanButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File folder = new File(jtf.getText());
				if(!folder.exists()) {
					JOptionPane.showMessageDialog(null, "Sorry, the path you picked is not a directory or does not exist", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					Frame.this.start(folder);
				}
			}
		});
		buttonsPanel.add(scanButton);

		JButton options = new JButton("Options");
		options.setFont(Main.getFont());
		options.setBackground(Color.WHITE);
		options.setForeground(Color.BLACK);
		options.addActionListener(new ActionListener() {
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
		normalFile.setFont(Main.getFont());
		keysPanel.add(normalFile);

		JLabel hiddenFile = new JLabel("Hidden", new ImageIcon(this.getClass().getResource("/res/file-icon-hidden.png")), JLabel.LEFT);
		hiddenFile.setFont(Main.getFont());
		keysPanel.add(hiddenFile);

		JLabel emptyFile = new JLabel("Empty");
		emptyFile.setForeground(Color.GRAY);
		emptyFile.setFont(Main.getFont());
		keysPanel.add(emptyFile);

		JLabel modifiedFile = new JLabel("Recently modified");
		modifiedFile.setForeground(new Color(255, 128, 0));
		modifiedFile.setFont(Main.getFont());
		keysPanel.add(modifiedFile);

		JLabel createdFile = new JLabel("Recently created");
		createdFile.setForeground(new Color(0, 100, 0));
		createdFile.setFont(Main.getFont());
		keysPanel.add(createdFile);

		mainPanel.add(keysPanel, BorderLayout.EAST);

		/*
		 * ANALYZE PANEL
		 */
		JPanel infoPanel = new JPanel();
		infoPanel.setOpaque(false);

		infoLabel = new JLabel("Select a folder...");
		infoLabel.setFont(Main.getFont());
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
			}
		}).start();
	}

	private void search(File parent) {
		File[] files = parent.listFiles();

		if(files != null) {
			for(File child : files) {
				this.addFile(parent, child);
				if(child.isDirectory()) {
					this.search(child);
				}
			}
		}
	}

	private void addFile(File parent, File child) {
		tree.expandPath(new TreePath(tree.getModel().getRoot()));

		String size = child.isFile() ? "(" + humanReadableByteCount(child.length()) + ")" : "";
		CustomNode childNode = new CustomNode(child.getName() + " " + size, child);

		tree.add(directories.get(parent), childNode);

		if(child.isDirectory()) {
			directories.put(child, childNode);

			//If the directory is empty
			File[] listFiles = child.listFiles();
			if(listFiles == null || listFiles.length == 0) {
				tree.add(directories.get(child), (CustomNode) CustomNode.EMPTY_NODE.clone());
			}
		}

		filesCount++;
		if((System.currentTimeMillis() - lastUpdate) >= 100) {
			infoLabel.setText(filesCount + " files analyzed in " + String.format("%.1f", (System.currentTimeMillis() - startTime)/1000.0) + "s.");
			lastUpdate = System.currentTimeMillis();
		}
	}

	private String humanReadableByteCount(long bytes) {
		if (bytes < 1000) return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(1000));
		char pre = "kMGTPE".charAt(exp-1);
		return String.format("%.1f %sB", bytes / Math.pow(1000, exp), pre);
	}
}

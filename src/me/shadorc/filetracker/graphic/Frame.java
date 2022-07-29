package me.shadorc.filetracker.graphic;

import me.shadorc.filetracker.Storage;
import me.shadorc.filetracker.Storage.Data;
import me.shadorc.filetracker.Utils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Frame extends JFrame {

    private Map<File, CustomNode> directories;
    private long startTime;
    private long lastUpdate;
    private int fileCount;
    private boolean isSearching;

    private final JPanel mainPanel;
    private final JLabel infoLabel;
    private final JButton scanButton;
    private CustomTree tree;

    public Frame() {
        super("Files Tracker - Beta");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        OptionsFrame optionsFrame = new OptionsFrame();

        this.isSearching = false;

        this.mainPanel = new JPanel(new BorderLayout());
        this.mainPanel.setBackground(Color.WHITE);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        final File diskFile = Utils.getDefaultDisk();
        if (diskFile == null) {
            Utils.showErrorDialog(null, "No default filesystem root found");
            System.exit(-1);
        }

        JTextField jtf = new JTextField(diskFile.getPath());
        topPanel.add(jtf, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 4));

        JButton browseButton = this.createButton("Browse", event -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception err) {
                err.printStackTrace();
            }

            JFileChooser chooser = new JFileChooser(diskFile);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int choice = chooser.showOpenDialog(Frame.this);
            if (choice == JFileChooser.APPROVE_OPTION) {
                jtf.setText(chooser.getSelectedFile().getPath());
            }

            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception err) {
                err.printStackTrace();
            }
        });
        buttonsPanel.add(browseButton);

        this.scanButton = this.createButton("Scan", event -> {
            if (!isSearching) {
                File folder = new File(jtf.getText());
                if (!folder.exists()) {
                    Utils.showErrorDialog(null, "The selected path does not exist");
                    return;
                }
                Frame.this.start(folder);
            }
            Frame.this.switchButton();
        });
        buttonsPanel.add(scanButton);

        JButton collapse = this.createButton("Collapse", event -> {
            if (tree == null) {
                return;
            }

            //We remove UI during the operation to save A LOT of times
            tree.setUI(null);
            Utils.collapseAll(tree, new TreePath(tree.getModel().getRoot()));
            tree.updateUI();
            tree.expandPath(new TreePath(tree.getModel().getRoot()));
        });
        buttonsPanel.add(collapse);

        JButton options = this.createButton("Options", event -> {
            optionsFrame.setLocationRelativeTo(this);
            optionsFrame.setVisible(true);
        });
        buttonsPanel.add(options);

        topPanel.add(buttonsPanel, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.PAGE_START);

        URL loadingUrl = Objects.requireNonNull(this.getClass().getResource("/res/large-icon.png"));
        JLabel loading = new JLabel("", new ImageIcon(loadingUrl), JLabel.CENTER);
        mainPanel.add(loading, BorderLayout.CENTER);

        /*
         * KEYS PANEL
         */
        JPanel keysPanel = new JPanel(new GridLayout(17, 1));
        keysPanel.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Keys"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        URL normalFileIcon = Objects.requireNonNull(this.getClass().getResource("/res/file-icon.png"));
        JLabel normalFile = new JLabel("Normal", new ImageIcon(normalFileIcon), JLabel.LEFT);
        normalFile.setFont(Utils.DEFAULT_FONT);
        keysPanel.add(normalFile);

        URL hiddenFileIcon = Objects.requireNonNull(this.getClass().getResource("/res/file-icon-hidden.png"));
        JLabel hiddenFile = new JLabel("Hidden", new ImageIcon(hiddenFileIcon), JLabel.LEFT);
        hiddenFile.setFont(Utils.DEFAULT_FONT);
        keysPanel.add(hiddenFile);

        URL lockedFileIcon = Objects.requireNonNull(this.getClass().getResource("/res/file-icon-locked.png"));
        JLabel lockedFile = new JLabel("System", new ImageIcon(lockedFileIcon), JLabel.LEFT);
        lockedFile.setFont(Utils.DEFAULT_FONT);
        keysPanel.add(lockedFile);

        JLabel emptyFile = new JLabel("Empty");
        emptyFile.setForeground(Color.GRAY);
        emptyFile.setFont(Utils.DEFAULT_FONT);
        keysPanel.add(emptyFile);

        JLabel modifiedFile = new JLabel("Recently modified");
        modifiedFile.setForeground(new Color(255, 128, 0));
        modifiedFile.setFont(Utils.DEFAULT_FONT);
        keysPanel.add(modifiedFile);

        JLabel createdFile = new JLabel("Recently created");
        createdFile.setForeground(new Color(0, 100, 0));
        createdFile.setFont(Utils.DEFAULT_FONT);
        keysPanel.add(createdFile);

        mainPanel.add(keysPanel, BorderLayout.EAST);

        /*
         * ANALYZE PANEL
         */
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);

        infoLabel = new JLabel("Select a folder to scan...");
        infoLabel.setFont(Utils.DEFAULT_FONT);
        infoLabel.setForeground(Color.BLACK);
        infoPanel.add(infoLabel);

        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        this.setContentPane(mainPanel);
        this.pack();
        this.setIconImage(Utils.ICON.getImage());
        this.setMinimumSize(new Dimension(800, 600));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void start(File rootFile) {
        directories = new HashMap<>();
        startTime = System.currentTimeMillis();
        fileCount = 0;

        CustomNode rootNode = new CustomNode(rootFile.getPath(), rootFile);
        directories.put(rootFile, rootNode);

        // Remove the loading icon
        BorderLayout layout = (BorderLayout) mainPanel.getLayout();
        mainPanel.remove(layout.getLayoutComponent(BorderLayout.CENTER));

        tree = new CustomTree(rootNode);
        JScrollPane jsp = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsp.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        jsp.getHorizontalScrollBar().setUI(new CustomScrollBarUI());
        mainPanel.add(jsp, BorderLayout.CENTER);

        Thread thread = new Thread(() -> {
            this.search(rootFile);

            if (isSearching) {
                Frame.this.switchButton();
            }
        });
        thread.start();
    }

    private void search(File parent) {
        File[] files = parent.listFiles();
        if (isSearching && files != null) {
            for (File child : files) {
                // We check if file exists because there's some very weird bugs with $Recycle.Bin for example
                if (!child.exists() || (Utils.isSystemFile(child) && !Storage.getBool(Data.SHOW_SYSTEM_DIR))) {
                    continue;
                }

                this.addFile(parent, child);
                if (child.isDirectory()) {
                    this.search(child);
                }
            }
        }
    }

    private void addFile(File parent, File child) {
        tree.expandPath(new TreePath(tree.getModel().getRoot()));

        StringBuilder name = new StringBuilder(child.getName());
        if (child.isFile()) {
            name.append(' ')
                    .append('(')
                    .append(Utils.toReadableByteCount(child.length()))
                    .append(')');
        }
        CustomNode childNode = new CustomNode(name.toString(), child);

        tree.add(directories.get(parent), childNode);

        // Color needs to be updated after insertion into the tree for proper parent's configuration
        childNode.updateColor();

        if (child.isDirectory()) {
            directories.put(child, childNode);

            // If the directory is empty
            File[] files = child.listFiles();
            if (files == null || files.length == 0) {
                tree.add(childNode, (CustomNode) CustomNode.EMPTY_NODE.clone());
            }
        }

        fileCount++;
        if ((System.currentTimeMillis() - lastUpdate) >= 100) {
            infoLabel.setText(String.format("%d files analyzed in %.1fs.", fileCount, (System.currentTimeMillis() - startTime) / 1000.0));
            lastUpdate = System.currentTimeMillis();
        }
    }

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setFont(Utils.DEFAULT_FONT);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.addActionListener(listener);
        return button;
    }

    private void switchButton() {
        scanButton.setText(isSearching ? "Scan" : "Stop");
        isSearching = !isSearching;
    }
}

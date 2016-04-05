package me.shadorc.filetracker;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import me.shadorc.filetracker.Storage.Data;

public class OptionsFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel mainPanel;

	public OptionsFrame() {
		super("Files Tracker - Options");
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		mainPanel = new JPanel(new GridLayout(2, 1));

		JPanel spinners = new JPanel(new GridLayout(2, 1));
		spinners.add(this.createOption("Consider a file as new if it was created less than", Data.CREATED_TIME_DAY));
		spinners.add(this.createOption("Consider a file as recently modified if it was created less than", Data.MODIFIED_TIME_DAY));
		mainPanel.add(spinners);

		JPanel boxes = new JPanel(new GridLayout(2, 2));
		boxes.add(this.createBox("Show recently modified", Data.SHOW_MODIFIED));
		boxes.add(this.createBox("Show recently created", Data.SHOW_CREATED));
		boxes.add(this.createBox("Show system directories (not recommended)", Data.SHOW_SYSTEM_DIR));
		mainPanel.add(boxes);

		this.setContentPane(mainPanel);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setIconImage(new ImageIcon(this.getClass().getResource("/res/icon.png")).getImage());
	}

	private JPanel createOption(String labelText, Data data) {
		JPanel panel = new JPanel(new BorderLayout());

		JLabel label = new JLabel(labelText);
		label.setFont(Utils.getFont());
		panel.add(label, BorderLayout.CENTER);

		int value = (Storage.getData(data) == null) ? 0 : Integer.parseInt(Storage.getData(data));

		JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, 0, 365, 1));
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Storage.store(data, spinner.getValue());
				Main.updateFrame();
			}
		});

		JPanel spinnerPanel = new JPanel(new GridLayout(1, 2));
		spinnerPanel.add(spinner);
		JLabel spinLabel = new JLabel(" days ago ");
		spinLabel.setFont(Utils.getFont());
		spinnerPanel.add(spinLabel);

		panel.add(spinnerPanel, BorderLayout.EAST);

		return panel;
	}

	private JCheckBox createBox(String desc, Data data) {
		boolean value = Storage.getData(data) == null ? true : Boolean.parseBoolean(Storage.getData(data));
		JCheckBox box = new JCheckBox(desc, value);
		box.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Storage.store(data, box.isSelected());
				Main.updateFrame();
			}
		});
		return box;
	}
}
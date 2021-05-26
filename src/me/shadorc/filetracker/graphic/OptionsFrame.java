package me.shadorc.filetracker.graphic;

import me.shadorc.filetracker.Main;
import me.shadorc.filetracker.Storage;
import me.shadorc.filetracker.Storage.Data;
import me.shadorc.filetracker.Utils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class OptionsFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private JPanel mainPanel;

    OptionsFrame() {
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
        this.setIconImage(Utils.ICON.getImage());
    }

    private JPanel createOption(String labelText, Data data) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel(labelText);
        label.setFont(Utils.DEFAULT_FONT);
        panel.add(label, BorderLayout.CENTER);

        int value = (Storage.get(data) == null) ? 0 : Integer.parseInt(Storage.get(data));

        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, 0, 365, 1));
        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Storage.save(data, spinner.getValue());
                Main.updateFrame();
            }
        });

        JPanel spinnerPanel = new JPanel(new GridLayout(1, 2));
        spinnerPanel.add(spinner);
        JLabel spinLabel = new JLabel(" days ago ");
        spinLabel.setFont(Utils.DEFAULT_FONT);
        spinnerPanel.add(spinLabel);

        panel.add(spinnerPanel, BorderLayout.EAST);

        return panel;
    }

    private JCheckBox createBox(String desc, Data data) {
        boolean value = Storage.get(data) == null ? true : Boolean.parseBoolean(Storage.get(data));
        JCheckBox box = new JCheckBox(desc, value);
        box.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Storage.save(data, box.isSelected());
                Main.updateFrame();
            }
        });
        return box;
    }
}
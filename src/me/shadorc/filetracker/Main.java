package me.shadorc.filetracker;

import me.shadorc.filetracker.graphic.Frame;

import javax.swing.*;
import java.io.IOException;

public class Main {

    private static Frame frame;

    public static void main(String[] args) {

        try {
            Storage.init();
        } catch (IOException err) {
            Utils.showErrorDialog(err, "Aborting, an error occured while creating config file : " + err.getMessage());
            System.exit(1);
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame = new Frame();
            }
        });
    }

    public static void updateFrame() {
        frame.revalidate();
        frame.repaint();
    }
}

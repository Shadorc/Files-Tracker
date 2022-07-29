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
            err.printStackTrace();
            System.exit(-1);
        }

        SwingUtilities.invokeLater(() -> frame = new Frame());
    }

    public static void updateFrame() {
        frame.revalidate();
        frame.repaint();
    }
}

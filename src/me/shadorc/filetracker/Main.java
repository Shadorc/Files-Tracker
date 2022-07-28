package me.shadorc.filetracker;

import me.shadorc.filetracker.graphic.Frame;

import javax.swing.*;

public class Main {

    private static Frame frame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> frame = new Frame());
    }

    public static void updateFrame() {
        frame.revalidate();
        frame.repaint();
    }
}

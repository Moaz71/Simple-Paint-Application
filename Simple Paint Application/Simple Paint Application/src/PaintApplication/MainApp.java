package PaintApplication;

import java.awt.BorderLayout;
import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Simple Paint App");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            StartingScreen startingScreen = new StartingScreen(frame);
            frame.add(startingScreen, BorderLayout.CENTER);

            frame.setVisible(true);
        });
    }
}
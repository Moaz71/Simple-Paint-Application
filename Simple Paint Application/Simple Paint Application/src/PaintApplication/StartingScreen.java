package PaintApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class StartingScreen extends JPanel {
    private final JFrame parentFrame;

    public StartingScreen(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(3, 1));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome To Our Simple Paint Program", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Book Antiqua", Font.BOLD, 30));
        mainPanel.add(welcomeLabel);

        // Buttons
        addButton(buttonPanel, "Start Drawing", e -> switchToDrawingPanel());
        addButton(buttonPanel, "Open Image", e -> openImage());
        addButton(buttonPanel, "Exit", e -> System.exit(0));

        mainPanel.add(buttonPanel);

        // Team label
        JLabel projectTeamLabel = new JLabel("Produced by: Moaz Sabry && Gehad Ashry", SwingConstants.CENTER);
        projectTeamLabel.setFont(new Font("Book Antiqua", Font.ITALIC, 18));
        mainPanel.add(projectTeamLabel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void addButton(JPanel panel, String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 50));
        button.addActionListener(listener);
        panel.add(button);
    }

    private void switchToDrawingPanel() {
        parentFrame.getContentPane().removeAll();
        DrawingPanel drawingPanel = new DrawingPanel(parentFrame);
        parentFrame.add(drawingPanel, BorderLayout.CENTER);
        parentFrame.revalidate();
        parentFrame.repaint();
    }

    private void openImage() {
        JFileChooser chooseFile = new JFileChooser();
        chooseFile.setDialogTitle("Open File");
        FileFilter filter = new FileNameExtensionFilter("jpg,jpeg,png", "jpg", "jpeg", "png");
        chooseFile.setFileFilter(filter);
        int result = chooseFile.showOpenDialog(parentFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooseFile.getSelectedFile();
            parentFrame.getContentPane().removeAll();
            DrawingPanel drawingPanel = new DrawingPanel(parentFrame);
            drawingPanel.loadImage(selectedFile);
            parentFrame.add(drawingPanel, BorderLayout.CENTER);
            parentFrame.revalidate();
            parentFrame.repaint();
        }
    }
}
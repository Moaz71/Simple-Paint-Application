package PaintApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Toolbar extends JPanel {
    private final DrawingPanel drawingPanel;

    public Toolbar(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(150, getHeight()));

        // Add buttons
        addButton("Free Hand", e -> drawingPanel.setCurrentTool(Tool.FREE_HAND));
        addButton("Eraser", e -> drawingPanel.setCurrentTool(Tool.ERASER));

        // Shapes dropdown
        JButton shapesButton = new JButton("Shapes");
        JPopupMenu shapesMenu = new JPopupMenu();
        addMenuItem(shapesMenu, "Rectangle", Tool.RECTANGLE);
        addMenuItem(shapesMenu, "Oval", Tool.OVAL);
        addMenuItem(shapesMenu, "Line", Tool.LINE);
        shapesButton.addActionListener(e -> shapesMenu.show(shapesButton, 0, shapesButton.getHeight()));
        add(shapesButton);

        // Color picker
        addButton("Change Color", e -> {
            Color chosenColor = JColorChooser.showDialog(null, "Pick a Color", drawingPanel.getBackground());
            if (chosenColor != null) {
                drawingPanel.setCurrentColor(chosenColor);
            }
        });

        // Fill shapes checkbox
        JCheckBox fillCheckbox = new JCheckBox("Fill Shapes");
        fillCheckbox.addActionListener(e -> drawingPanel.setFillShapes(fillCheckbox.isSelected()));
        add(fillCheckbox);

        // Stroke style dropdown with a button
        JButton styleButton = new JButton("Style");
        JPopupMenu styleMenu = new JPopupMenu();
        addStyleMenuItem(styleMenu, "Solid", "Solid");
        addStyleMenuItem(styleMenu, "Dotted", "Dotted");
        addStyleMenuItem(styleMenu, "Dashed", "Dashed");
        styleButton.addActionListener(e -> styleMenu.show(styleButton, 0, styleButton.getHeight()));
        add(styleButton);

        // Clear button
        addButton("Clear", e -> drawingPanel.clearCanvas());

        // Stroke size slider
        JSlider strokeSlider = new JSlider(1, 20, 2);
        strokeSlider.setMajorTickSpacing(5);
        strokeSlider.setMinorTickSpacing(1);
        strokeSlider.setPaintTicks(true);
        strokeSlider.setPaintLabels(true);
        strokeSlider.addChangeListener(e -> drawingPanel.setStrokeWidth(strokeSlider.getValue()));
        add(new JLabel("Stroke Size:"));
        add(strokeSlider);

        // Undo/Redo buttons
        addButton("Undo", e -> drawingPanel.undo());
        addButton("Redo", e -> drawingPanel.redo());

        // Save button
        addButton("Save", e -> drawingPanel.saveDrawing());

        // Back button
        addButton("Back", e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(Toolbar.this);
            parentFrame.getContentPane().removeAll();
            StartingScreen startingScreen = new StartingScreen(parentFrame);
            parentFrame.add(startingScreen, BorderLayout.CENTER);
            parentFrame.revalidate();
            parentFrame.repaint();
        });
    }

    private void addButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        add(button);
    }

    private void addMenuItem(JPopupMenu menu, String text, Tool tool) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(e -> drawingPanel.setCurrentTool(tool));
        menu.add(menuItem);
    }

    private void addStyleMenuItem(JPopupMenu menu, String text, String strokeStyle) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(e -> drawingPanel.setStrokeStyle(strokeStyle));
        menu.add(menuItem);
    }
}
package PaintApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DrawingPanel extends JPanel {
    private final List<Drawable> drawables = new ArrayList<>(); // Stores all drawable objects
    private final Stack<List<Drawable>> history = new Stack<>(); // For undo functionality
    private final Stack<List<Drawable>> redoStack = new Stack<>(); // For redo functionality
    private Tool currentTool = Tool.FREE_HAND; // Current drawing tool
    private Color currentColor = Color.BLACK; // Current drawing color
    private float strokeWidth = 2.0f; // Current stroke width
    private Point startPoint; // Start point for shapes and lines
    private Point endPoint; // End point for shapes and lines
    private List<Point> currentLine = new ArrayList<>(); // Current line for free-hand and eraser
    private BufferedImage backgroundImage; // Background image for editing
    private final JFrame parentFrame; // Parent frame for navigation
    private boolean fillShapes = false; // Toggle for filling shapes
    private String strokeStyle = "Solid"; // Stroke style (Solid, Dotted, Dashed)

    public DrawingPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setBackground(Color.WHITE); // Set background to white
        setLayout(new BorderLayout());

        // Add the toolbar
        Toolbar toolbar = new Toolbar(this);
        add(toolbar, BorderLayout.WEST);

        // Add mouse listeners
        addMouseListeners();
    }

    private void addMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                saveState(); // Save current state for undo
                if (currentTool == Tool.FREE_HAND || currentTool == Tool.ERASER) {
                    currentLine = new ArrayList<>(); // Start a new line
                    currentLine.add(e.getPoint());
                } else if (currentTool == Tool.LINE || currentTool == Tool.RECTANGLE || currentTool == Tool.OVAL) {
                    startPoint = e.getPoint(); // Set start point for shapes
                    endPoint = e.getPoint(); // Initialize end point for preview
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentTool == Tool.FREE_HAND) {
                    drawables.add(new DrawableLine(new ArrayList<>(currentLine), currentColor, strokeWidth, strokeStyle));
                } else if (currentTool == Tool.ERASER) {
                    drawables.add(new DrawableLine(new ArrayList<>(currentLine), getBackground(), strokeWidth, strokeStyle));
                } else if (currentTool == Tool.LINE) {
                    drawables.add(new DrawableShape(Tool.LINE, startPoint.x, startPoint.y, e.getX(), e.getY(), currentColor, strokeWidth, strokeStyle, fillShapes));
                } else if (currentTool == Tool.RECTANGLE || currentTool == Tool.OVAL) {
                    int x = Math.min(startPoint.x, e.getX());
                    int y = Math.min(startPoint.y, e.getY());
                    int width = Math.abs(startPoint.x - e.getX());
                    int height = Math.abs(startPoint.y - e.getY());
                    drawables.add(new DrawableShape(currentTool, x, y, width, height, currentColor, strokeWidth, strokeStyle, fillShapes));
                }
                currentLine.clear(); // Clear the current line
                startPoint = null; // Reset start point
                endPoint = null; // Reset end point
                repaint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentTool == Tool.FREE_HAND || currentTool == Tool.ERASER) {
                    currentLine.add(e.getPoint()); // Add points to the current line
                } else if (currentTool == Tool.LINE || currentTool == Tool.RECTANGLE || currentTool == Tool.OVAL) {
                    endPoint = e.getPoint(); // Update end point for preview
                }
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Fill the background with white
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Draw the background image if it exists
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Draw all drawable objects
        for (Drawable drawable : drawables) {
            drawable.draw(g2);
        }

        // Draw the current line (for free-hand or eraser)
        if (!currentLine.isEmpty()) {
            g2.setColor(currentTool == Tool.ERASER ? getBackground() : currentColor);
            setStrokeStyle(g2, strokeStyle, strokeWidth);
            for (int i = 1; i < currentLine.size(); i++) {
                Point p1 = currentLine.get(i - 1);
                Point p2 = currentLine.get(i);
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }

        // Draw the current shape or line (preview while dragging)
        if (startPoint != null && endPoint != null && currentTool != Tool.FREE_HAND && currentTool != Tool.ERASER) {
            g2.setColor(currentColor);

            if (currentTool == Tool.LINE || !fillShapes) {
                setStrokeStyle(g2, strokeStyle, strokeWidth); // Apply stroke style for lines and non-filled shapes
            } else {
                g2.setStroke(new BasicStroke(strokeWidth)); // Use solid stroke for filled shapes
            }

            int x = Math.min(startPoint.x, endPoint.x);
            int y = Math.min(startPoint.y, endPoint.y);
            int width = Math.abs(startPoint.x - endPoint.x);
            int height = Math.abs(startPoint.y - endPoint.y);

            switch (currentTool) {
                case LINE:
                    g2.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
                    break;
                case RECTANGLE:
                    if (fillShapes) {
                        g2.fillRect(x, y, width, height);
                    } else {
                        g2.drawRect(x, y, width, height);
                    }
                    break;
                case OVAL:
                    if (fillShapes) {
                        g2.fillOval(x, y, width, height);
                    } else {
                        g2.drawOval(x, y, width, height);
                    }
                    break;
            }
        }
    }

    // Set the stroke style (Solid, Dotted, Dashed)
    private void setStrokeStyle(Graphics2D g2, String strokeStyle, float strokeWidth) {
        float[] dashPattern;
        switch (strokeStyle) {
            case "Dotted":
                dashPattern = new float[]{1, 3}; // Dotted pattern
                break;
            case "Dashed":
                dashPattern = new float[]{10, 5}; // Dashed pattern
                break;
            default: // Solid
                dashPattern = null;
                break;
        }
        if (dashPattern != null) {
            g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashPattern, 0));
        } else {
            g2.setStroke(new BasicStroke(strokeWidth));
        }
    }

    // Save the current state for undo
    private void saveState() {
        List<Drawable> state = new ArrayList<>(drawables);
        history.push(state);
        redoStack.clear(); // Clear redo stack when a new action is performed
    }

    // Undo the last action
    public void undo() {
        if (!history.isEmpty()) {
            redoStack.push(new ArrayList<>(drawables)); // Save current state for redo
            drawables.clear();
            drawables.addAll(history.pop());
            repaint();
        }
    }

    // Redo the last undone action
    public void redo() {
        if (!redoStack.isEmpty()) {
            history.push(new ArrayList<>(drawables)); // Save current state for undo
            drawables.clear();
            drawables.addAll(redoStack.pop());
            repaint();
        }
    }

    // Save the drawing as an image
    public void saveDrawing() {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        paintComponent(g2);
        g2.dispose();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Drawing");
        FileFilter filter = new FileNameExtensionFilter("PNG Images", "png");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                ImageIO.write(image, "png", fileToSave);
                JOptionPane.showMessageDialog(this, "Drawing saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving drawing: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Load an image for editing
    public void loadImage(File file) {
        try {
            backgroundImage = ImageIO.read(file);
            repaint();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error loading image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Getters and setters
    public void setCurrentTool(Tool tool) {
        this.currentTool = tool;
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setFillShapes(boolean fillShapes) {
        this.fillShapes = fillShapes;
    }

    public void setStrokeStyle(String strokeStyle) {
        this.strokeStyle = strokeStyle;
    }

    public void clearCanvas() {
        drawables.clear();
        history.clear();
        redoStack.clear();
        repaint();
    }
}
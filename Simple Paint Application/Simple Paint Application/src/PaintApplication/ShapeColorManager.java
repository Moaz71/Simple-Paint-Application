package PaintApplication;


import java.awt.Color;

public class ShapeColorManager {
    private Color currentColor;

    public ShapeColorManager(Color initialColor) {
        this.currentColor = initialColor;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }
}

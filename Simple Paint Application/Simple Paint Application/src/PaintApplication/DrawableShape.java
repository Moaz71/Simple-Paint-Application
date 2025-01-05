package PaintApplication;

import java.awt.*;

public class DrawableShape implements Drawable {
    private final Tool type;
    private final int x, y, width, height;
    private final Color color;
    private final float strokeWidth;
    private final String strokeStyle;
    private final boolean fill;

    public DrawableShape(Tool type, int x, int y, int width, int height, Color color, float strokeWidth, String strokeStyle, boolean fill) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.strokeStyle = strokeStyle;
        this.fill = fill;
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(color);

        // Apply stroke style only for non-filled shapes
        if (!fill) {
            setStrokeStyle(g2, strokeStyle, strokeWidth);
        } else {
            g2.setStroke(new BasicStroke(strokeWidth)); // Use solid stroke for filled shapes
        }

        switch (type) {
            case RECTANGLE:
                if (fill) {
                    g2.fillRect(x, y, width, height);
                } else {
                    g2.drawRect(x, y, width, height);
                }
                break;
            case OVAL:
                if (fill) {
                    g2.fillOval(x, y, width, height);
                } else {
                    g2.drawOval(x, y, width, height);
                }
                break;
            case LINE:
                setStrokeStyle(g2, strokeStyle, strokeWidth); // Always apply stroke style for lines
                g2.drawLine(x, y, width, height);
                break;
        }
    }

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
}
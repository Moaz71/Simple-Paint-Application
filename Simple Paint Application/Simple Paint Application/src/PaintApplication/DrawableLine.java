package PaintApplication;

import java.awt.*;
import java.util.List;

public class DrawableLine implements Drawable {
    private final List<Point> points;
    private final Color color;
    private final float strokeWidth;
    private final String strokeStyle;

    public DrawableLine(List<Point> points, Color color, float strokeWidth, String strokeStyle) {
        this.points = points;
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.strokeStyle = strokeStyle;
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(color);
        setStrokeStyle(g2, strokeStyle, strokeWidth);

        for (int i = 1; i < points.size(); i++) {
            Point p1 = points.get(i - 1);
            Point p2 = points.get(i);
            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }

    private void setStrokeStyle(Graphics2D g2, String strokeStyle, float strokeWidth) {
        float[] dashPattern;
        switch (strokeStyle) {
            case "Dotted":
                dashPattern = new float[]{1, 3};
                break;
            case "Dashed":
                dashPattern = new float[]{10, 5};
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
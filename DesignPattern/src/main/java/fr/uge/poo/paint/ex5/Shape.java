package fr.uge.poo.paint.ex5;

import java.awt.Graphics2D;

public sealed interface Shape extends Drawable {
    Point center();

    static Shape of(String[] parts) {
        var a = Integer.parseInt(parts[1]);
        var b = Integer.parseInt(parts[2]);
        var c = Integer.parseInt(parts[3]);
        var d = Integer.parseInt(parts[4]);
        return switch (parts[0]) {
            case "line" -> new Line(a, b, c, d);
            case "rectangle" -> new Rectangle(a, b, c, d);
            case "ellipse" -> new Ellipse(a, b, c, d);
            default -> throw new IllegalArgumentException("Unknown shape type : %s".formatted(parts[0]));
        };
    }

    record Line(int x1, int y1, int x2, int y2) implements Shape {
        @Override
        public void draw(Graphics2D g) {
            g.drawLine(x1, y1, x2, y2);
        }

        @Override
        public Point center() {
            return new Point((x1 + x2) / 2, (y1 + y2) / 2);
        }
    }

    record Rectangle(int x, int y, int width, int height) implements Shape {
        @Override
        public void draw(Graphics2D g) {
            g.drawRect(x, y, width, height);
        }

        @Override
        public Point center() {
            return new Point(x + (width / 2), y + (height / 2));
        }
    }

    record Ellipse(int x, int y, int width, int height) implements Shape {
        @Override
        public void draw(Graphics2D g) {
            g.drawOval(x, y, width, height);
        }

        @Override
        public Point center() {
            return new Point(x + (width / 2), y + (height / 2));
        }
    }
}

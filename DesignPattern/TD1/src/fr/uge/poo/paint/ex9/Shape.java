package fr.uge.poo.paint.ex9;

import fr.uge.poo.paint.ex9.engine.DrawEngine;

public sealed interface Shape extends Drawable {
    /**
     * @return (x, y)
     */
    Pair<Integer, Integer> center();

    /**
     * @return ((x, y), (width, height))
     */
    Rectangle superRect();

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
        public Pair<Integer, Integer> center() {
            return new Pair<>((x1 + x2) / 2, (y1 + y2) / 2);
        }

        @Override
        public Shape.Rectangle superRect() {
            var topLeft = new Pair<>(Math.min(x1, x2), Math.min(y1, y2));
            var bottomRight = new Pair<>(Math.max(x1, x2), Math.max(y1, y2));
            return new Shape.Rectangle(
                    topLeft.first(),
                    topLeft.second(),
                    bottomRight.first() - topLeft.first(),
                    bottomRight.second() - topLeft.second()
            );
        }

        @Override
        public void draw(DrawEngine engine, DrawEngine.Color color) {
            engine.drawLine(x1, y1, x2, y2, color);
        }
    }

    record Rectangle(int x, int y, int width, int height) implements Shape {
        @Override
        public Pair<Integer, Integer> center() {
            return new Pair<>(x + (width / 2), y + (height / 2));
        }

        @Override
        public Shape.Rectangle superRect() {
            return this;
        }

        @Override
        public void draw(DrawEngine engine, DrawEngine.Color color) {
            engine.drawRect(x, y, width, height, color);
        }
    }

    record Ellipse(int x, int y, int width, int height) implements Shape {
        @Override
        public Pair<Integer, Integer> center() {
            return new Pair<>(x + (width / 2), y + (height / 2));
        }

        @Override
        public Shape.Rectangle superRect() {
            return new Shape.Rectangle(x, y, width, height);
        }

        @Override
        public void draw(DrawEngine engine, DrawEngine.Color color) {
            engine.drawOval(x, y, width, height, color);
        }
    }
}

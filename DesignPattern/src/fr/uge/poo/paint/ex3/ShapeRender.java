package fr.uge.poo.paint.ex3;

import java.awt.Graphics2D;

@FunctionalInterface
public interface ShapeRender {
    void draw(Graphics2D g);

    static ShapeRender of(String[] parts) {
        var a = Integer.parseInt(parts[1]);
        var b = Integer.parseInt(parts[2]);
        var c = Integer.parseInt(parts[3]);
        var d = Integer.parseInt(parts[4]);
        return switch (parts[0]) {
            case "line" -> g -> g.drawLine(a, b, c, d);
            case "rectangle" -> g -> g.drawRect(a, b, c, d);
            case "ellipse" -> g -> g.drawOval(a, b, c, d);
            default -> throw new IllegalArgumentException("Unknown shape type : %s".formatted(parts[0]));
        };
    }
}

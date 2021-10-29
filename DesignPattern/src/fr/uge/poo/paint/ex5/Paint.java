package fr.uge.poo.paint.ex5;

import fr.uge.poo.simplegraphics.SimpleGraphics;
import java.awt.Color;

public final class Paint {
    private final static int CANVAS_WIDTH = 800;
    private final static int CANVAS_HEIGHT = 800;
    private final static String CONFIG_FILE = "resources/draw2.txt";

    public static void main(String[] args) {
        final var area = new SimpleGraphics("area", CANVAS_WIDTH, CANVAS_HEIGHT);
        area.clear(Color.WHITE);
        var shapeManager = ShapeManager.from(CONFIG_FILE);
        area.render(shapeManager::drawAll);
        area.waitForMouseEvents((x, y) -> area.render(g -> {
            var selected = shapeManager.getNearestFrom(new Point(x, y));
            selected.ifPresent(it -> shapeManager.select(it, g));
        }));
    }
}

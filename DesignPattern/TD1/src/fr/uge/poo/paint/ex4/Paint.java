package fr.uge.poo.paint.ex4;

import fr.uge.poo.simplegraphics.SimpleGraphics;

import java.awt.*;

public final class Paint {
    private final static int CANVAS_WIDTH = 800;
    private final static int CANVAS_HEIGHT = 800;
    private final static String CONFIG_FILE = "resources/draw2.txt";

    public static void main(String[] args) {
        SimpleGraphics area = new SimpleGraphics("area", CANVAS_WIDTH, CANVAS_HEIGHT);
        area.clear(Color.WHITE);

        var shapeManager = ShapeManager.from(CONFIG_FILE);
        area.render(shapeManager::drawAll);
    }
}

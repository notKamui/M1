package fr.uge.poo.paint.ex3;

import fr.uge.poo.simplegraphics.SimpleGraphics;

import java.awt.*;

public class Paint {
    private final static int CANVAS_WIDTH = 800;
    private final static int CANVAS_HEIGHT = 800;
    private final static String CONFIG_FILE = "resources/draw2.txt";

    public static void main(String[] args) {
        SimpleGraphics area = new SimpleGraphics("area", CANVAS_WIDTH, CANVAS_HEIGHT);
        area.clear(Color.WHITE);

        var config = Config.from(CONFIG_FILE);
        area.render(config::drawAll);
    }
}

package fr.uge.poo.simplegraphics;


import java.awt.*;

public class SimpleGraphicsExample {
    private final static int CANVAS_WIDTH = 800;
    private final static int CANVAS_HEIGHT = 600;

    private static void drawAll(Graphics2D graphics) {
        graphics.setColor(Color.BLACK);
        graphics.drawRect(100, 20, 40, 140);
        graphics.drawLine(0, 0, CANVAS_WIDTH - 1, CANVAS_HEIGHT - 1);
        graphics.drawLine(0, CANVAS_HEIGHT - 1, CANVAS_WIDTH - 1, 0);
        graphics.drawOval(100, 20, 40, 140);
    }

    public static void main(String[] args) {
        SimpleGraphics area = new SimpleGraphics("area", CANVAS_WIDTH, CANVAS_HEIGHT);
        area.clear(Color.WHITE);
        area.render(SimpleGraphicsExample::drawAll);
        //canvas.render(graphics -> drawAll(graphics));
    }
}

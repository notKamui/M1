package fr.uge.poo.paint.ex8.engine;

import fr.uge.poo.paint.ex8.Pair;
import fr.uge.poo.paint.ex8.ShapeManager;
import fr.uge.poo.simplegraphics.SimpleGraphics;
import java.util.Objects;

public final class DrawEngineSimpleAdapter implements DrawEngine {
    private final SimpleGraphics graphics;

    public DrawEngineSimpleAdapter(SimpleGraphics graphics) {
        this.graphics = Objects.requireNonNull(graphics);
    }

    private static java.awt.Color toRawColor(Color c) {
        Objects.requireNonNull(c);
        return switch (c) {
            case BLACK -> java.awt.Color.BLACK;
            case WHITE -> java.awt.Color.WHITE;
            case ORANGE -> java.awt.Color.ORANGE;
        };
    }

    @Override
    public void clear(Color c) {
        graphics.clear(toRawColor(c));
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2, Color c) {
        graphics.render(g -> {
            g.setColor(toRawColor(c));
            g.drawLine(x1, y1, x2, y2);
        });
    }

    @Override
    public void drawRect(int x, int y, int width, int height, Color c) {
        graphics.render(g -> {
            g.setColor(toRawColor(c));
            g.drawRect(x, y, width, height);
        });
    }

    @Override
    public void drawOval(int x, int y, int width, int height, Color c) {
        graphics.render(g -> {
            g.setColor(toRawColor(c));
            g.drawOval(x, y, width, height);
        });
    }

    @Override
    public void registerOnClick(ShapeManager sm) {
        graphics.clear(java.awt.Color.WHITE);
        sm.drawAll(this);
        graphics.waitForMouseEvents((x, y) -> graphics.render(g -> {
            var selected = sm.getNearestFrom(new Pair<>(x, y));
            selected.ifPresent(it -> sm.select(it, this));
        }));
    }
}

package fr.uge.poo.paint.ex9.engine;

import fr.uge.poo.paint.ex9.Pair;
import fr.uge.poo.paint.ex9.ShapeManager;
import fr.uge.poo.simplegraphics.SimpleGraphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import static java.util.Objects.requireNonNull;

public final class DrawEngineSimpleAdapter implements DrawEngine {
    private final SimpleGraphics graphics;
    private final ArrayList<Consumer<Graphics2D>> drawCalls = new ArrayList<>();

    public DrawEngineSimpleAdapter(SimpleGraphics graphics) {
        this.graphics = requireNonNull(graphics);
    }

    private static java.awt.Color toRawColor(Color c) {
        requireNonNull(c);
        return switch (c) {
            case BLACK -> java.awt.Color.BLACK;
            case WHITE -> java.awt.Color.WHITE;
            case ORANGE -> java.awt.Color.ORANGE;
        };
    }

    @Override
    public void refresh(Color background) {
        clear(background);
        graphics.render(g -> List.copyOf(drawCalls).forEach(it -> it.accept(g)));
        drawCalls.clear();
    }

    @Override
    public void clear(Color c) {
        graphics.clear(toRawColor(c));
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2, Color c) {
        drawCalls.add(g -> {
            g.setColor(toRawColor(c));
            g.drawLine(x1, y1, x2, y2);
        });
    }

    @Override
    public void drawRect(int x, int y, int width, int height, Color c) {
        drawCalls.add(g -> {
            g.setColor(toRawColor(c));
            g.drawRect(x, y, width, height);
        });
    }

    @Override
    public void drawOval(int x, int y, int width, int height, Color c) {
        drawCalls.add(g -> {
            g.setColor(toRawColor(c));
            g.drawOval(x, y, width, height);
        });
    }

    @Override
    public void registerOnClick(ShapeManager sm) {
        requireNonNull(sm);
        graphics.clear(java.awt.Color.WHITE);
        sm.drawAll(this);
        graphics.waitForMouseEvents((x, y) -> graphics.render(g -> {
            var selected = sm.getNearestFrom(new Pair<>(x, y));
            selected.ifPresent(it -> sm.select(it, this));
        }));
    }
}

package fr.uge.poo.paint.ex7;

import fr.uge.poo.simplegraphics.SimpleGraphics;

import java.awt.*;
import java.util.Objects;

public final class Canvas {
    private static final int MIN_WIDTH = 500;
    private static final int MIN_HEIGHT = 500;

    private final ShapeManager sm;
    private final int width;
    private final int height;

    public Canvas(ShapeManager sm) {
        this.sm = Objects.requireNonNull(sm);
        var size = sm.getSize();
        this.width = Math.max(MIN_WIDTH, size.first());
        this.height = Math.max(MIN_HEIGHT, size.second());
    }

    public void open() {
        final var area = new SimpleGraphics("area", width, height);
        area.clear(Color.WHITE);
        area.render(sm::drawAll);
        area.waitForMouseEvents((x, y) -> area.render(g -> {
            var selected = sm.getNearestFrom(new Pair<>(x, y));
            selected.ifPresent(it -> sm.select(it, g));
        }));
    }
}

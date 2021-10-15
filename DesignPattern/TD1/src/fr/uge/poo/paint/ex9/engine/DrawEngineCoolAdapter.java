package fr.uge.poo.paint.ex9.engine;

import com.evilcorp.coolgraphics.CoolGraphics;
import fr.uge.poo.paint.ex9.Pair;
import fr.uge.poo.paint.ex9.ShapeManager;

import java.util.ArrayList;
import java.util.Objects;

public final class DrawEngineCoolAdapter implements DrawEngine {
    private final CoolGraphics graphics;
    private final ArrayList<Runnable> drawCalls = new ArrayList<>();

    public DrawEngineCoolAdapter(CoolGraphics graphics) {
        this.graphics = Objects.requireNonNull(graphics);
    }

    private static CoolGraphics.ColorPlus toCoolColor(Color c) {
        return switch(c) {
            case BLACK -> CoolGraphics.ColorPlus.BLACK;
            case WHITE -> CoolGraphics.ColorPlus.WHITE;
            case ORANGE -> CoolGraphics.ColorPlus.ORANGE;
        };
    }

    @Override
    public void refresh(Color background) {
        clear(background);
        drawCalls.forEach(Runnable::run);
    }

    @Override
    public void clear(Color c) {
        graphics.repaint(toCoolColor(c));
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2, Color c) {
        drawCalls.add(() -> graphics.drawLine(x1, y1, x2, y2, toCoolColor(c)));
    }

    @Override
    public void drawOval(int x, int y, int width, int height, Color c) {
        drawCalls.add(() -> graphics.drawEllipse(x, y, width, height, toCoolColor(c)));
    }

    @Override
    public void registerOnClick(ShapeManager sm) {
        clear(Color.WHITE);
        sm.drawAll(this);
        graphics.waitForMouseEvents((x, y) -> {
            var selected = sm.getNearestFrom(new Pair<>(x, y));
            selected.ifPresent(it -> sm.select(it, this));
        });
    }
}

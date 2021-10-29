package fr.uge.poo.paint.ex9;

import com.evilcorp.coolgraphics.CoolGraphics;
import fr.uge.poo.paint.ex9.engine.DrawEngine;
import fr.uge.poo.paint.ex9.engine.DrawEngineCoolAdapter;
import fr.uge.poo.paint.ex9.engine.DrawEngineSimpleAdapter;
import fr.uge.poo.simplegraphics.SimpleGraphics;
import java.util.Objects;

public final class Canvas {
    private static final int MIN_WIDTH = 500;
    private static final int MIN_HEIGHT = 500;

    private final ShapeManager sm;
    private final DrawEngine engine;

    public Canvas(boolean legacy, ShapeManager sm) {
        this.sm = Objects.requireNonNull(sm);
        var size = sm.getSize();
        var width = Math.max(MIN_WIDTH, size.first());
        var height = Math.max(MIN_HEIGHT, size.second());
        if (legacy) {
            engine = new DrawEngineSimpleAdapter(new SimpleGraphics("area", width, height));
        } else {
            engine = new DrawEngineCoolAdapter(new CoolGraphics("area", width, height));
        }
    }

    public void open() {
        engine.clear(DrawEngine.Color.WHITE);
        sm.drawAll(engine);
        engine.registerOnClick(sm);
    }
}

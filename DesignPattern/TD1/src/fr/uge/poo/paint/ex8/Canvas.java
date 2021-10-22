package fr.uge.poo.paint.ex8;

import fr.uge.poo.paint.ex8.engine.DrawEngine;
import fr.uge.poo.paint.ex8.engine.DrawEngineFactory;
import fr.uge.poo.paint.ex8.engine.DrawEngineSimpleAdapterFactory;

import java.util.Objects;
import java.util.ServiceLoader;

public final class Canvas {
    private static final int MIN_WIDTH = 500;
    private static final int MIN_HEIGHT = 500;

    private final ShapeManager sm;
    private final DrawEngine engine;

    public Canvas(String name, ShapeManager sm) {
        Objects.requireNonNull(name);
        this.sm = Objects.requireNonNull(sm);
        var size = sm.getSize();
        var width = Math.max(MIN_WIDTH, size.first());
        var height = Math.max(MIN_HEIGHT, size.second());

        var deLoader = ServiceLoader.load(DrawEngineFactory.class);
        var factory = deLoader.findFirst().orElse(new DrawEngineSimpleAdapterFactory());
        engine = factory.withData(name, width, height);
    }

    public void open() {
        engine.clear(DrawEngine.Color.WHITE);
        sm.drawAll(engine);
        engine.registerOnClick(sm);
    }
}

package fr.uge.poo.paint.ex8.engine;

import fr.uge.poo.simplegraphics.SimpleGraphics;

public class DrawEngineSimpleAdapterFactory implements DrawEngineFactory {

    @Override
    public DrawEngine withData(String name, int width, int height) {
        return new DrawEngineSimpleAdapter(new SimpleGraphics(name, width, height));
    }
}

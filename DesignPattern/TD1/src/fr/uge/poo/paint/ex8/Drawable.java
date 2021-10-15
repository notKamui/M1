package fr.uge.poo.paint.ex8;

import fr.uge.poo.paint.ex8.engine.DrawEngine;

public interface Drawable {
    void draw(DrawEngine engine, DrawEngine.Color color);
}

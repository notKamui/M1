package fr.uge.poo.paint.ex7;

import fr.uge.poo.paint.ex7.engine.DrawEngine;

public interface Drawable {
    void draw(DrawEngine engine, DrawEngine.Color color);
}

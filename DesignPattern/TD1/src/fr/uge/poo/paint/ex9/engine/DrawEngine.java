package fr.uge.poo.paint.ex9.engine;

import fr.uge.poo.paint.ex9.ShapeManager;

public sealed interface DrawEngine permits DrawEngineCoolAdapter, DrawEngineSimpleAdapter {
    enum Color {
        BLACK, WHITE, ORANGE
    }

    void refresh(Color background);

    void clear(Color c);
    void drawLine(int x1, int y1, int x2, int y2, Color c);
    default void drawRect(int x, int y, int width, int height, Color c) {
        drawLine(x, y, x + width, y, c);
        drawLine(x, y, x, y + height, c);
        drawLine(x + width, y, x + width, y + height, c);
        drawLine(x, y + height, x + width, y + height, c);
    }
    void drawOval(int x, int y, int width, int height, Color c);

    void registerOnClick(ShapeManager sm);
}

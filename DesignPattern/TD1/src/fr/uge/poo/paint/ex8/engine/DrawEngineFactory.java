package fr.uge.poo.paint.ex8.engine;

public interface DrawEngineFactory {
    DrawEngine withData(String name, int width, int height);
}

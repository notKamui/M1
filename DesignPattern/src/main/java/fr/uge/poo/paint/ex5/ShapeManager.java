package fr.uge.poo.paint.ex5;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public final class ShapeManager {
    private final List<Shape> shapes = new ArrayList<>();

    private ShapeManager() {}

    public Optional<Shape> getNearestFrom(Point p) {
        Objects.requireNonNull(p);
        return shapes.stream().sorted(Comparator.comparingInt(shape ->
                p.squaredDistance(shape.center())
        )).findAny();
    }

    public void drawAll(Graphics2D g) {
        Objects.requireNonNull(g);
        g.setColor(Color.BLACK);
        shapes.forEach(shape -> shape.draw(g));
    }

    public void select(Shape shape, Graphics2D g) {
        Objects.requireNonNull(shape);
        Objects.requireNonNull(g);
        drawAll(g);
        g.setColor(Color.ORANGE);
        shape.draw(g);
    }

    public static ShapeManager from(String filename) {
        Objects.requireNonNull(filename);
        var config = new ShapeManager();
        Path path = Paths.get(filename);
        try(Stream<String> lines = Files.lines(path)) {
            lines.forEach(line -> {
                if (line.isBlank()) return;
                var parts = line.split(" ");
                config.shapes.add(Shape.of(parts));
            });
        } catch (IOException e) {
            System.err.printf("Cannot find file : %s%nAborting config creation%n", filename);
        }
        return config;
    }
}

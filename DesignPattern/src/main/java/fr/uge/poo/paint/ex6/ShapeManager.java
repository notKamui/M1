package fr.uge.poo.paint.ex6;

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

    private Shape selected = null;

    public Optional<Shape> getNearestFrom(Pair<Integer, Integer> p) {
        Objects.requireNonNull(p);
        return shapes.stream().min(Comparator.comparingInt(shape -> {
            var a = p.first() - shape.center().first();
            var b = p.second() - shape.center().second();
            return  a*a + b*b;
        }));
    }

    public void drawAll(Graphics2D g) {
        Objects.requireNonNull(g);
        var size = getSize();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, size.first(), size.second());
        shapes.forEach(shape -> {
            if (selected != null && shape == selected) {
                g.setColor(Color.ORANGE);
            } else {
                g.setColor(Color.BLACK);
            }
            shape.draw(g);
        });
    }

    public void select(Shape shape, Graphics2D g) {
        Objects.requireNonNull(shape);
        Objects.requireNonNull(g);
        selected = shape;
        drawAll(g);
    }

    public Optional<Shape> getSelected() {
        return Optional.ofNullable(selected);
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

    public Pair<Integer, Integer> getSize() {
        var minX = Integer.MAX_VALUE;
        var maxX = 0;
        var minY = Integer.MAX_VALUE;
        var maxY = 0;

        for (var shape : shapes) {
            var superRect = shape.superRect();
            var x = superRect.x();
            var y = superRect.y();
            var width = superRect.width();
            var height = superRect.height();
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x + width);
            maxY = Math.max(maxY, y + height);
        }

        return new Pair<>(maxX - minX, maxY - minY);
    }
}

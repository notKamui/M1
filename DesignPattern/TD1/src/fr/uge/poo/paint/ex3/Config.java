package fr.uge.poo.paint.ex3;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class Config {
    private final List<ShapeRender> shapes = new ArrayList<>();

    private Config() {}

    public void drawAll(Graphics2D g) {
        g.setColor(Color.BLACK);
        shapes.forEach(shape -> shape.draw(g));
    }

    public static Config from(String filename) {
        var config = new Config();
        Path path = Paths.get(filename);
        try(Stream<String> lines = Files.lines(path)) {
            lines.forEach(line -> {
                var parts = line.split(" ");
                config.shapes.add(ShapeRender.of(parts));
            });
        } catch (IOException e) {
            System.err.printf("Cannot find file : %s%nAborting config creation%n", filename);
        }
        return config;
    }
}

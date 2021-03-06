package fr.uge.poo.paint.ex7;

import java.util.Arrays;

public final class Paint {
    public static void main(String[] args) {
        var legacy = Arrays.asList(args).contains("-legacy");
        var sm = ShapeManager.from("resources/draw2.txt");
        var canvas = new Canvas(legacy, sm);
        canvas.open();
    }
}

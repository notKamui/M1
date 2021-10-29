package fr.uge.poo.paint.ex9;

import java.util.Arrays;

public final class Paint {
    public static void main(String[] args) {
        var legacy = Arrays.asList(args).contains("-legacy");
        var sm = ShapeManager.from("resources/draw-big.txt");
        var canvas = new Canvas(legacy, sm);
        canvas.open();
    }
}

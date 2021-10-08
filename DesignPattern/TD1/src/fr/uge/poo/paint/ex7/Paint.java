package fr.uge.poo.paint.ex7;

public final class Paint {
    public static void main(String[] args) {
        var sm = ShapeManager.from("resources/draw-big.txt");
        var canvas = new Canvas(sm);
        canvas.open();
    }
}

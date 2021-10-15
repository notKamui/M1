package fr.uge.poo.paint.ex8;

public final class Paint {
    public static void main(String[] args) {
        var sm = ShapeManager.from("resources/draw2.txt");
        var canvas = new Canvas("area", sm);
        canvas.open();
    }
}

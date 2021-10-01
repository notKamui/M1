package fr.uge.poo.paint.ex4;

public record Point(int x, int y) {
    public int squaredDistance(Point o) {
        var a = x - o.x;
        var b = y - o.y;
        return  a*a + b*b;
    }
}

package at.u4a.geometric_algorithms.geometric;

public class Line implements InterfaceGeometric {

    private final static double EPSILON = 0.1;

    public final Point a, b;

    public Line(Point a, Point b) {
        this.a = a;
        this.b = b;
    }

    public Line() {
        a = new Point();
        b = new Point();
    }

    public void set(Line other) {
        this.a.set(other.a);
        this.b.set(other.b);
    }

    public boolean contains(Point p) {
        return distance(p) <= EPSILON;
    }

    /** Formule de distance récuperé dans le Partiel 2016/2017 de PCOO */
    public double distance(Point p) {
        return ((b.x - a.x) * (a.y - p.y) - (a.x - p.x) * (b.y - a.y)) / Math.hypot(b.x - a.x, b.y - a.y);
    }
}

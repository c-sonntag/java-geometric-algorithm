package at.u4a.geometric_algorithms.geometric;

import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGeometricPainterVisitor;

public class Line implements InterfaceGeometric {

    private final static double EPSILON = 0.5;

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

    public void translate(Point p) {
        a.translate(p);
        b.translate(p);
    }

    public boolean contains(Point p) {
        return Math.abs(distance(p)) <= EPSILON;
    }

    @Override
    public InterfaceGeometric getContains(Point p) {
        if (contains(p))
            return this;
        else
            return null;
    }

    /** Formule de distance récuperé dans le Partiel 2016/2017 de PCOO */
    public double distance(Point p) {
        return ((b.x - a.x) * (a.y - p.y) - (a.x - p.x) * (b.y - a.y)) / Math.hypot(b.x - a.x, b.y - a.y);
    }

    @Override
    public void accept(InterfaceGeometricPainterVisitor visitor) {
        visitor.visit(this);
    }

}

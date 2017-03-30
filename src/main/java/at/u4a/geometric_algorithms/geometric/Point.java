package at.u4a.geometric_algorithms.geometric;

import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGeometricPainterVisitor;

public class Point implements InterfaceGeometric {

    public final static float POINT_RAYON = 1.5f;
    public final static float POINT_DIAMETRE = POINT_RAYON * 2;

    public double x, y;

    public Point(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
        this.x = 0;
        this.y = 0;
    }

    public void set(Point other) {
        this.x = other.x;
        this.y = other.y;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void translate(Point p) {
        this.x += p.x;
        this.y += p.y;
    }

    public boolean contains(Point p) {
        return (Math.abs(x - p.x) <= POINT_DIAMETRE) && (Math.abs(y - p.y) <= POINT_DIAMETRE);
        // return (x == p.x) && (y == p.y);
    }
    
    @Override
    public InterfaceGeometric getContains(Point p) {
        if (contains(p))
            return this;
        else
            return null;
    }

    // public boolean contains(Point p, float epsilon) {
    // return (Math.abs(x - p.x) <= epsilon) && (Math.abs(y - p.y) <= epsilon);
    // }

    public double distance(Point p) {
        double xDistance = p.x - x, yDistance = p.y - y;
        return Math.sqrt(xDistance * xDistance + yDistance * yDistance);
    }

    @Override
    public void accept(InterfaceGeometricPainterVisitor visitor) {
        visitor.visit(this);
    }

}

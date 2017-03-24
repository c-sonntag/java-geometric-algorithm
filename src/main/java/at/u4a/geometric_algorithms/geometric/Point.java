package at.u4a.geometric_algorithms.geometric;

import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGeometricPainterVisitor;

public class Point implements InterfaceGeometric {

    public final static double POINT_RAYON = 1.5; 
    
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

    public boolean contains(Point p) {
        
        return (x == p.x) && (y == p.y);
    }

    public double distance(Point p) {
        double xDistance = p.x - x, yDistance = p.y - y;
        return Math.sqrt(xDistance * xDistance + yDistance * yDistance);
    }

    @Override
    public void accept(InterfaceGeometricPainterVisitor visitor) {
        visitor.visit(this);
    }

}

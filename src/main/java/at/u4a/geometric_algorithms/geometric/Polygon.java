package at.u4a.geometric_algorithms.geometric;

import java.util.ArrayList;

public class Polygon extends AbstractShape {

    static public enum Type {
        Simple, Convex, Monotone
    }

    final public ArrayList<Point> perimeter = new ArrayList<Point>();

    public Polygon() {
        super();
    }

    public Polygon(Point origin) {
        super(origin);
    }

    public Type getType() {
        return Type.Simple; /** < @TODO */
    }

    public boolean contains(Point p) {
        return false; /** < @TODO */
    }

    public double distance(Point p) {
        return 0; /** < @TODO */
    }

}

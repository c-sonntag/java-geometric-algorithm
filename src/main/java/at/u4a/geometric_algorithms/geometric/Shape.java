package at.u4a.geometric_algorithms.geometric;

public abstract class Shape implements GeometricInterface {

    final public Point origin;

    public Shape(Point origin) {
        this.origin = origin;
    }

    public Shape() {
        origin = new Point(0, 0);
    }

    public void translate(Point newOrigin) {
        origin.set(newOrigin);
    }

}
package at.u4a.geometric_algorithms.geometric;

public interface Shape {

    boolean contains(double x, double y);

    void translate(double x, double y);
}
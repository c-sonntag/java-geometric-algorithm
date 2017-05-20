package at.u4a.geometric_algorithms.geometric;

import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGeometricPainterVisitor;

public class Segment extends Line {

    public Segment(Point a, Point b) {
        super(a, b);
    }

    public Segment() {
        super();
    }

    public String toString() {
        return "[" + a.toString() + ";" + b.toString() + "]";
    }

    public boolean contains(Point p) {
        return (a.contains(p) || b.contains(p)) ? true :
        /* minX */ ((p.x >= Math.min(a.x, b.x)) &&
        /* maxX */ (p.x <= Math.max(a.x, b.x)) &&
        /* minY */ (p.y >= Math.min(a.y, b.y)) &&
        /* maxY */ (p.y <= Math.max(a.y, b.y))) ?
        /* In Range */ super.contains(p) :
        /* Out Range */ false;
    }

    @Override
    public void accept(InterfaceGeometricPainterVisitor visitor) {
        visitor.visit(this);
    }

}

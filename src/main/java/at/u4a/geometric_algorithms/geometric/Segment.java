package at.u4a.geometric_algorithms.geometric;

import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGeometricPainterVisitor;

public class Segment extends Line {

    public Segment(Point a, Point b) {
        super(a, b);
    }

    public Segment() {
        super();
    }

    public boolean contains(Point p) {
        return
        /* minX */ ((p.x >= Math.min(a.x, b.x)) &&
        /* maxX */ (p.x <= Math.max(a.x, b.x)) &&
        /* minY */ (p.y >= Math.min(a.x, b.x)) &&
        /* maxY */ (p.y <= Math.max(a.x, b.x))) ?
        /* In Range */ super.contains(p) :
        /* Out Range */ false;
    }
    
    @Override
    public void accept(InterfaceGeometricPainterVisitor visitor) {
        visitor.visit(this);
    }
    
}

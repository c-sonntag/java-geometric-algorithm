package at.u4a.geometric_algorithms.geometric;

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
}

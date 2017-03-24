package at.u4a.geometric_algorithms.geometric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import at.u4a.geometric_algorithms.graphic_visitor.InterfaceShapePainterVisitor;

public class Polygon extends AbstractShape implements Iterable<Segment> {

    private class SegmentIterator implements Iterator<Segment> {

        private final Iterator<Point> perimeter_it = perimeter.iterator();
        private Point startPoint = null, point = null, lastPoint = null;
        // private final Segment genSegment = new Segment(); @TODO voir si possible avec un seul segment généré

        private SegmentIterator() {
            doNext();
            startPoint = point;
            doNext();
        }

        private void doNext() {
            lastPoint = point;
            if (perimeter_it.hasNext())
                point = perimeter_it.next();
            else if (startPoint != null) {
                point = startPoint;
                startPoint = null;
            } else
                point = null;
        }

        @Override
        public boolean hasNext() {
            return (point != null) && (lastPoint != null);
        }

        @Override
        public Segment next() {
            Segment s = new Segment(lastPoint, point);
            doNext();
            return s;
        }
    }

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

    public void addPoint(Point p) {
        perimeter.add(p);
    }

    public Iterator<Segment> iterator() {
        return new Polygon.SegmentIterator();
    }

    /*
     * { List<Segment> segments = new ArrayList<Segment>();
     * 
     * for (Point p : perimeter) { if (lastPoint != null) { segments.add(new
     * Segment(lastPoint, p)); } lastPoint = p; /* Point point =
     * point.set(origin.x + p.x, origin.y + p.y); if (lastPoint != null) {
     * segments.add(new Segment(new Point(origin.x + lastPoint.x, origin.y +
     * lastPoint.y), point)); } lastPoint = p;
     * 
     * } return segments; }
     */

    public boolean contains(Point p) {
        if (containPoint(p) != null)
            return true;
        if (containSegment(p) != null)
            return true;
        //
        return false;
    }

    public Point containPoint(Point p) {
        final Point pToOrigin = new Point(p);
        convertToOrigin(pToOrigin);
        for (Point q : perimeter)
            if (q.contains(pToOrigin))
                return q;
        return null;
    }

    public Segment containSegment(Point p) {
        final Point pToOrigin = new Point(p);
        convertToOrigin(pToOrigin);
        for (Segment s : this)
            if (s.contains(pToOrigin))
                return s;
        return null;
    }

    public double distance(Point p) {
        return 0; /** < @TODO */
    }

    @Override
    public void accept(InterfaceShapePainterVisitor visitor) {
        visitor.visit(this);
    }

}

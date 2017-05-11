package at.u4a.geometric_algorithms.geometric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import at.u4a.geometric_algorithms.geometric.mapper.InterfaceMapper;
import at.u4a.geometric_algorithms.geometric.mapper.MappedPoint;
import at.u4a.geometric_algorithms.geometric.mapper.MappedSegment;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceShapePainterVisitor;

public class Polygon extends AbstractShape implements InterfaceContainer<Segment> {

    /* INTERNAL CLASS */

    private class SegmentIterator implements Iterator<Segment> {

        private final Iterator<Point> perimeter_it = perimeter.iterator();
        private Point startPoint = null, point = null, lastPoint = null;

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

    /* STATIC */

    static public enum Type {
        Simple, Convex, Monotone
    }

    /* PUBLIC VARIABLES */

    final public Vector<Point> perimeter = new Vector<Point>();

    /* FUNCTION */

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

    @Override
    public boolean contains(Point p) {
        return (getContains(p) != null);
    }

    @Override
    /** @todo Touver la zone interieur */
    public InterfaceGeometric getContains(Point p) {
        Point isP = containPoint(p);
        if (isP != null)
            return isP;

        Segment isS = containSegment(p);
        if (isS != null)
            return isS;

        return null;
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

    @Override
    public void clear() {
        perimeter.clear();
    }

    /* */

    // @Override
    // public List<InterfaceMapper> getMappedComposition() {
    // ArrayList<InterfaceMapper> mappedComposition = new
    // ArrayList<InterfaceMapper>();
    //
    // // Points
    // for (Point q : perimeter)
    // mappedComposition.add(new MappedPoint((o) -> {
    // o.set(origin.x + q.x, origin.y + q.y);
    // }, (o) -> {
    // q.set(o.x - origin.x, o.y - origin.y);
    // }));
    //
    // // Segments
    // int listPointSize = mappedComposition.size();
    // int count = 0;
    //
    // //
    // MappedPoint lastPoint = null;
    // for (int i = 0; i < listPointSize; i++) {
    //
    // MappedPoint qm = (MappedPoint) mappedComposition.get(i);
    // if (lastPoint != null)
    // mappedComposition.add(new MappedSegment(lastPoint, qm));
    // //
    // lastPoint = qm;
    // }
    // if (listPointSize > 1)
    // mappedComposition.add(new MappedSegment((MappedPoint)
    // mappedComposition.get(0), (MappedPoint)
    // mappedComposition.get(listPointSize - 1)));
    //
    // //
    // return mappedComposition;
    // }

    @Override
    public InterfaceMapper getContainMappedComposition(Point pc) {

        // Points
        MappedPoint mp_first = null;
        MappedPoint mp_last = null;
        for (Point q : perimeter) {
            MappedPoint mp = new MappedPoint((o) -> {
                o.set(origin.x + q.x, origin.y + q.y);
            }, (o) -> {
                q.set(o.x - origin.x, o.y - origin.y);
            });

            if (mp.contains(pc))
                return mp;

            //

            if (mp_last != null) {
                MappedSegment ms = new MappedSegment(mp_last, mp);
                if (ms.contains(pc))
                    return ms;
            }

            //

            if (mp_first == null)
                mp_first = mp;
            mp_last = mp;
        }

        if ((mp_last != null) && (mp_first != null)) {
            MappedSegment ms = new MappedSegment(mp_last, mp_first);
            if (ms.contains(pc))
                return ms;
        }

        return null;
    }

}

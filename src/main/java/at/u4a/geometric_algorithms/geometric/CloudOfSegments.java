package at.u4a.geometric_algorithms.geometric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import at.u4a.geometric_algorithms.geometric.mapper.InterfaceMapper;
import at.u4a.geometric_algorithms.geometric.mapper.MappedPoint;
import at.u4a.geometric_algorithms.geometric.mapper.MappedSegment;
import at.u4a.geometric_algorithms.geometric.mapper.SimplePoint;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceShapePainterVisitor;

public class CloudOfSegments extends AbstractShape implements InterfaceContainer<Segment> {

    final public ArrayList<Segment> cloud = new ArrayList<Segment>();

    public ArrayList<Segment> getOnPosition(Point p) {
        ArrayList<Segment> segments = null;
        final Point pToOrigin = new Point(p);
        convertToOrigin(pToOrigin);
        for (Segment s : cloud) {
            if (s.contains(pToOrigin)) {
                if (segments == null)
                    segments = new ArrayList<Segment>();
                segments.add(s);
            }
        }
        return segments;
    }

    @Override
    public boolean contains(Point p) {
        return (getContains(p) != null);
    }

    @Override
    public InterfaceGeometric getContains(Point p) {
        final Point pToOrigin = new Point(p);
        convertToOrigin(pToOrigin);
        for (Segment s : cloud) {
            if (s.contains(pToOrigin))
                return s;
        }
        return null;
    }

    /** Distance entre un point et le segment du nuage le plus proche */
    public double distance(Point p) {
        if (cloud.isEmpty())
            throw new NoSuchElementException("No element in cloud");

        Iterator<Segment> cloudIt = cloud.iterator();

        double minDistance = cloudIt.next().distance(p);

        while (cloudIt.hasNext()) {
            double nextDistance = cloudIt.next().distance(p);
            if (nextDistance < minDistance)
                minDistance = nextDistance;
        }

        return minDistance;
    }

    @Override
    public void accept(InterfaceShapePainterVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Iterator<Segment> iterator() {
        return cloud.iterator();
    }

    @Override
    public void clear() {
        cloud.clear();
    }

    /* */

    // @Override
    // public List<InterfaceMapper> getMappedComposition() {
    // List<InterfaceMapper> mappedComposition = new
    // ArrayList<InterfaceMapper>();
    //
    // // Points on the top
    // for (Segment s : cloud) {
    // mappedComposition.add(new MappedPoint((o) -> {
    // o.set(origin.x + s.a.x, origin.y + s.a.y);
    // }, (o) -> {
    // s.a.set(o.x - origin.x, o.y - origin.y);
    // }));
    // mappedComposition.add(new MappedPoint((o) -> {
    // o.set(origin.x + s.b.x, origin.y + s.b.y);
    // }, (o) -> {
    // s.b.set(o.x - origin.x, o.y - origin.y);
    // }));
    // }
    //
    // // Lines at second on the top
    // int listPointSize = mappedComposition.size();
    // for (int i = 0; i < listPointSize; i += 2) {
    // mappedComposition.add(new MappedSegment(//
    // (MappedPoint) mappedComposition.get(i), //
    // (MappedPoint) mappedComposition.get(i + 1) //
    // ));
    // }
    //
    // //
    // return mappedComposition;
    // }

    public InterfaceMapper getContainMappedComposition(Point pc) {

        // Points on the top
        for (Segment s : cloud) {
            MappedPoint im_p1 = new MappedPoint((o) -> {
                o.set(origin.x + s.a.x, origin.y + s.a.y);
            }, (o) -> {
                s.a.set(o.x - origin.x, o.y - origin.y);
            });
            MappedPoint im_p2 = new MappedPoint((o) -> {
                o.set(origin.x + s.b.x, origin.y + s.b.y);
            }, (o) -> {
                s.b.set(o.x - origin.x, o.y - origin.y);
            });
            
            //
            
            if(im_p1.contains(pc))
                return im_p1;
            else if (im_p2.contains(pc))
                return im_p2;
            
            InterfaceMapper im_line = new MappedSegment(im_p1, im_p2);
            
            if(im_line.contains(pc))
                return im_line;
        }

        //
        return null;
    }

}

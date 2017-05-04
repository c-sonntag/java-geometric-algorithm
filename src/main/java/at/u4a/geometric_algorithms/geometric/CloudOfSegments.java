package at.u4a.geometric_algorithms.geometric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import at.u4a.geometric_algorithms.geometric.mapper.InterfaceMapper;
import at.u4a.geometric_algorithms.geometric.mapper.MappedSegment;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGeometricPainterVisitor;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceShapePainterVisitor;

public class CloudOfSegments extends AbstractShape implements InterfaceContainer<Segment> {

    final public ArrayList<Segment> cloud = new ArrayList<Segment>();

    public ArrayList<Segment> getOnPosition(Point p) {
        ArrayList<Segment> segments = null;

        for (Segment s : cloud) {
            if (s.contains(p)) {
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
        for (Segment s : cloud) {
            if (s.contains(p))
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

    @Override
    public List<InterfaceMapper> getMappedComposition() {
        List<InterfaceMapper> mappedComposition = new ArrayList<InterfaceMapper>();
        for (Segment s : cloud) {
            mappedComposition.add(new MappedSegment(s.a, s.b));
        }
        return mappedComposition;
    }

}

package at.u4a.geometric_algorithms.geometric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import at.u4a.geometric_algorithms.geometric.mapper.InterfaceMapper;
import at.u4a.geometric_algorithms.geometric.mapper.MappedPoint;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceShapePainterVisitor;

public class CloudOfPoints extends AbstractShape implements InterfaceContainer<Point> {

    final public ArrayList<Point> cloud = new ArrayList<Point>();

    public ArrayList<Point> getOnPosition(Point p) {
        ArrayList<Point> points = null;
        final Point pToOrigin = new Point(p);
        convertToOrigin(pToOrigin);
        for (Point pc : cloud) {
            if (pc.contains(pToOrigin)) {
                if (points == null)
                    points = new ArrayList<Point>();
                points.add(pc);
            }
        }
        return points;
    }

    @Override
    public boolean contains(Point p) {
        return (getContains(p) != null);
    }

    @Override
    public InterfaceGeometric getContains(Point p) {
        final Point pToOrigin = new Point(p);
        convertToOrigin(pToOrigin);
        for (Point s : cloud) {
            if (s.contains(pToOrigin))
                return s;
        }
        return null;
    }

    /** Distance entre un point et le point du nuage le plus proche */
    public double distance(Point p) {
        if (cloud.isEmpty())
            throw new NoSuchElementException("No element in cloud");

        Iterator<Point> cloudIt = cloud.iterator();

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
    public Iterator<Point> iterator() {
        return cloud.iterator();
    }

    @Override
    public void clear() {
        cloud.clear();
    }

    /* */

    //@Override
    //public List<InterfaceMapper> getMappedComposition() {
    //    List<InterfaceMapper> mappedComposition = new ArrayList<InterfaceMapper>();
    //    for (Point p : cloud) {
    //        mappedComposition.add(new MappedPoint((o) -> {
    //            o.set(origin.x + p.x, origin.y + p.y);
    //        }, (o) -> {
    //            p.set(o.x - origin.x, o.y - origin.y);
    //        }));
    //    }
    //    return mappedComposition;
    //}
    
    @Override
    public InterfaceMapper getContainMappedComposition(Point pc) {
        //List<InterfaceMapper> mappedComposition = new ArrayList<InterfaceMapper>();
        for (Point p : cloud) {
            InterfaceMapper im = new MappedPoint((o) -> {
                o.set(origin.x + p.x, origin.y + p.y);
            }, (o) -> {
                p.set(o.x - origin.x, o.y - origin.y);
            });
            if(im.contains(pc))
                return im;
        }
        return null;
    }

}

package at.u4a.geometric_algorithms.algorithm;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import at.u4a.geometric_algorithms.algorithm.InterfaceAlgorithmBuilder;
import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.CloudOfPoints;
import at.u4a.geometric_algorithms.geometric.CloudOfSegments;
import at.u4a.geometric_algorithms.geometric.Line;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Segment;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.AlgorithmLayer;
import at.u4a.geometric_algorithms.utils.Calc;

/**
 * Triangulation
 * 
 * @author Hip
 * @see Computational Geometry - Algorithms and Applications,
 *      SegmentIntersection
 *
 */
public class SegmentIntersection extends AbstractAlgorithm {

    public static class Builder implements InterfaceAlgorithmBuilder {

        @Override
        public String getName() {
            return SegmentIntersection.class.getSimpleName();
        }

        @Override
        public boolean canApply(AbstractLayer l) {
            return (l.getShape() instanceof CloudOfSegments);
        }

        static int SegmentIntersectionCount = 1;

        @Override
        public AbstractLayer builder(AbstractLayer l) {

            //
            AbstractShape as = l.getShape();
            if (!(as instanceof CloudOfSegments))
                throw new RuntimeException("SegmentIntersection need a CloudOfSegments Shape !");

            //
            CloudOfSegments cos = (CloudOfSegments) as;

            //
            AbstractLayer al = new AlgorithmLayer<SegmentIntersection>(new SegmentIntersection(cos.cloud, cos), Algorithm.SegmentIntersection, l);
            al.setLayerName("t" + String.valueOf(SegmentIntersectionCount));
            SegmentIntersectionCount++;
            return al;
        }

    };

    private final AbstractList<Segment> cloud;
    private final AbstractShape as;

    private final CloudOfPoints cop;

    public SegmentIntersection(AbstractList<Segment> cloud, AbstractShape as) {
        this.cloud = cloud;
        this.as = as;
        this.cop = new CloudOfPoints(as.origin);
        // this.cop = new MonotonePolygon(as.origin, points);
    }

    /* ************** */

    InterfaceGraphicVisitor mutableVisitorForDebugging = null;

    @Override
    public void accept(Vector<AbstractLayer> v, InterfaceGraphicVisitor visitor) {
        mutableVisitorForDebugging = visitor;
        makeSegmentInteraction();
        visitor.visit(cop);
    }

    /* ************** */

    public CloudOfPoints getCloudOfPoint() {
        makeSegmentInteraction();
        return cop;
    }

    /* ************** */

    @Override
    public AbstractShape getCompositeShape() {
        return getCloudOfPoint();
    }

    /* ************** */

    private int mutablePreviousLinesHash = 0;

    protected void makeSegmentInteraction() {

        int currentLinesHash = as.hashCode();
        if (currentLinesHash != mutablePreviousLinesHash) {

            //
            statusStartBuild();

            //
            // statusBuildIs(buildSegmentInteractionQuadratique());
            statusBuildIs(buildSegmentInteraction());

            //
            mutablePreviousLinesHash = currentLinesHash;
        }

    }

    /* ************** */

    /**
     * The left-to-right order of the segments along the sweep line corresponds
     * to the left-to-right order of the leaves in T.
     */
    private class SweepLineComparator implements Comparator<Segment> {

        @Override
        public int compare(Segment s1, Segment s2) {
            if (s1.equals(s2))
                return 0;
            //
            return 0; // fix it
        }
    };

    private class EventComparator implements Comparator<Event> {

        @Override
        public int compare(Event e1, Event e2) {
            if (e1.equals(e2))
                return 0;
            //
            return 0; // fix it
        }
    };

    private class ArrangementComparator implements Comparator<SegmentAssoc> {
        @Override
        public int compare(SegmentAssoc e1, SegmentAssoc e2) {
            return (//
            e1.upper.equals(e2.upper) ? 0 : ( //
            (e1.upper.y == e2.upper.y) ? //
                    ((e1.upper.x < e2.upper.x) ? -1 : 1) : //
                    ((e1.upper.y < e2.upper.y) ? -1 : 1)//
            ));
        }
    };

    /* ************** */

    /**
     * Contient le "cloud" qui est enregistré dans un "Set" avec comparator
     * "top-left" pour simuler le passage du "sweepline"
     */
    private final Set<SegmentAssoc> arrangements = new TreeSet<SegmentAssoc>(new ArrangementComparator());

    private final HashMap<Point, Vector<Segment>> intersections = new HashMap<Point, Vector<Segment>>();

    private final SortedMap<Segment, Segment> statusT = new TreeMap<Segment, Segment>(new SweepLineComparator());

    private final Set<Event> events = new TreeSet<Event>(new EventComparator());

    /* ************** */

    /**
     * Permet d'associer un Segment avec un Point et de trouver le point e plus
     * en haut ou a gauche
     */
    class SegmentAssoc {
        public Segment s;
        public Point upper, downer; // Upper point is also called "endpoint"

        public SegmentAssoc(Segment s) {
            this.s = s;
            if ((s.a.y > s.b.y) ? true : ((s.a.y < s.b.y) ? false : ((s.a.x >= s.b.x) ? true : false))) {
                upper = s.b;
                downer = s.a;
            } else {
                upper = s.a;
                downer = s.b;

            }
        }
    }

    class Event {

    }

    /* ************** */

    private void findIntersections(Segment s) {

        ArrayDeque<SegmentAssoc> Q = new ArrayDeque<SegmentAssoc>();
        Q.push(new SegmentAssoc(s));
        while (!Q.isEmpty())
            handleEventPoint(Q.pop());
    }

    private void handleEventPoint(SegmentAssoc sa) {

    }

    /* ************** */

    /**
     */
    private boolean buildSegmentInteraction() {

        //
        if (cloud.size() <= 2)
            return buildSegmentInteractionQuadratique();

        //
        cop.clear();
        arrangements.clear();

        //
        for (Segment s : cloud)
            arrangements.add(new SegmentAssoc(s));

        //
        drawArrangementTip();

        return true;
    }

    /* ************** */

    /**
     * Used for test and compare
     */
    protected boolean buildSegmentInteractionQuadratique() {

        //
        cop.clear();
        Set<Point> intersections = new HashSet<Point>();

        for (Segment s1 : cloud) {
            boolean firstS2 = true;

            for (Segment s2 : cloud) {

                if (firstS2) {
                    firstS2 = false;
                    continue;
                }

                //
                statusAddCounter();

                //
                Point pInter = Calc.intersection(s1, s2);
                if (pInter == null)
                    continue;

                if (!intersections.contains(pInter)) {
                    intersections.add(pInter);
                    cop.addPoint(pInter);
                }
            }
        }

        return true;
    }

    /* ********* DEBUG ONLY ********* */

    public void drawTextTip(String txt, Point p) {
        if (mutableVisitorForDebugging == null)
            return;
        final Point pToOrigin = new Point();
        pToOrigin.set(p);
        as.convertToStandard(pToOrigin);
        mutableVisitorForDebugging.drawTip(txt, pToOrigin);
    }

    public void drawTip(Point p) {
        if (mutableVisitorForDebugging == null)
            return;
        final Point pToOrigin = new Point();
        pToOrigin.set(p);
        as.convertToStandard(pToOrigin);
        mutableVisitorForDebugging.drawTip(p.toString(), pToOrigin);
    }

    public void drawArrangementTip() {
        int counter = 0;
        for (SegmentAssoc as : arrangements) {
            drawTextTip(String.valueOf(counter), as.upper);
            counter++;
        }
    }

};

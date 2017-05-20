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

    private class SweepLineComparator implements Comparator<SegmentAssoc> {

        @Override
        public int compare(SegmentAssoc s1, SegmentAssoc s2) {
            if (s1.equals(s2))
                return 0;
            //
            return 0; // fix it
        }
    };

    /**
     * The left-to-right order of the segments along the sweep line corresponds
     * to the left-to-right order of the leaves in T.
     */
    private class EventComparator implements Comparator<Event> {

        @Override
        public int compare(Event e1, Event e2) {
            if (e1.equals(e2))
                return 0;
            //
            return 0; // fix it
        }
    };

    private class ArrangementComparator_SegmentASSOC implements Comparator<SegmentAssoc> {
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

    private static class Arrangement extends TreeSet<EventPoint> {

        private static class ArrangementComparator implements Comparator<EventPoint> {

            public int compare(EventPoint psa1, EventPoint psa2) {
                return (psa1.p.equals(psa2.p) ? 0 : ( //
                (psa1.p.y == psa2.p.y) ? //
                        ((psa1.p.x < psa2.p.x) ? -1 : 1) : //
                        ((psa1.p.y < psa2.p.y) ? -1 : 1) //
                ));
            }
        };

        public Arrangement() {
            super(new ArrangementComparator());
        }

        public void init(AbstractList<Segment> ls) {
            clear();
            for (Segment s : ls) {
                add(new EventPoint(s, EventType.Upper));
                add(new EventPoint(s, EventType.Lower));
            }
        }

    }

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

    private final Arrangement arrangements = new Arrangement();

    // private final Set<SegmentAssoc> arrangements = new
    // TreeSet<SegmentAssoc>(new ArrangementComparator());
    // private final Set<SegmentAssoc> arrangements = new
    // TreeSet<SegmentAssoc>(new ArrangementComparator());

    private final Set<SegmentAssoc> sweepline = new TreeSet<SegmentAssoc>(new SweepLineComparator());
    private final Set<Event> sweeplineStatus = new TreeSet<Event>(new EventComparator());

    // private final Set<Point> intersectionsQueue = new TreeSet<Point>(new
    // Point.PointComparator());
    private final ArrayDeque<SegmentAssoc> intersectionsQueue = new ArrayDeque<SegmentAssoc>();

    // private final HashMap<Point, Vector<Segment>> intersectionsQueue = new
    // HashMap<Point, Vector<Segment>>();

    /* ************** */

    /**
     * Permet d'associer un «Segment» avec un «Point» et de trouver le point le
     * plus en haut ou à gauche
     */
    private static class SegmentAssoc {
        public Segment s;
        public Point upper, lower;

        public SegmentAssoc(Segment s) {
            this.s = s;
            if ((s.a.y > s.b.y) ? true : ((s.a.y < s.b.y) ? false : ((s.a.x >= s.b.x) ? true : false))) {
                upper = s.b;
                lower = s.a;
            } else {
                upper = s.a;
                lower = s.b;
            }
        }
    }

    public enum EventType {
        Upper, Lower, Intersection
    };

    
    private static class EventPoint {
        final public Segment s;
        final public EventType type;
        final public Point p;
        
        public EventPoint(Point p) {
            this.s = null;
            this.p = p;
            this.type = EventType.Intersection;
        }

        public EventPoint(Segment s, EventType type) {
            this.s = s;
            this.type = type;
            boolean aIsTop = true;
            if ((s.a.y > s.b.y) ? true : ((s.a.y < s.b.y) ? false : ((s.a.x >= s.b.x) ? true : false)))
                aIsTop = false;
            p = (type == EventType.Upper) ? (aIsTop ? s.a : s.b) : (aIsTop ? s.b : s.a);
        }
    }


    /**
     * Permet d’indiquer le statut d’un Point, entre autre, s’il est un «upper»,
     * alors il fait les tests d’intersectionsQueue avec la «sweepline», sinon
     * si, c’est un «lower», alors il faut arrêter ses tests, et donc le retirer
     * des évènements
     */
    private class Event {
        final public SegmentAssoc as;
        final public EventType type;
        final public Point p;

        public Event(SegmentAssoc as, EventType type) {
            this.as = as;
            this.type = type;
            this.p = (type == EventType.Upper) ? as.upper : as.lower;
            if (type == EventType.Upper)
                test();
        }

        private void test() {

        }
    }

    /* ************** */

    private void findIntersections() {
        ArrayDeque<Event> Q = new ArrayDeque<Event>();
        Q.push(new SegmentAssoc(arrangements.iterator().next()));
        while (!Q.isEmpty())
            handleEventPointBook(Q.pop());
    }

    private void handleEventPointBook(SegmentAssoc sa) {

    }

    /**
     * Il est nécessaire de vérifier si les segments sont adjacent et ou encore
     * non découvert
     */
    private void findNewEvent(Segment left, Segment right, Point p) {

        Point intersectionPoint = Calc.intersection(left, right);
        /** @todo check "on it" intersection */
        if (intersectionPoint != null) {
            if (intersectionPoint.x > p.x) {
                if (!intersectionsQueue.contains(new EventPoint(null, type)))
                    intersectionsQueue.push(intersectionPoint);
            }
        }

    }

    /* ************** */

    private void advanceSegment(SegmentAssoc as) {

        sweepline.add(as);
        sweeplineStatus.add(new Event(as));

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
        arrangements.init(cloud);

        //
        drawArrangementTip();
        
        //
        

        //
        // for (Segment s : cloud)
        // arrangements.add(new SegmentAssoc(s));

        // for (Segment s : cloud)
        // sweeplineStatus.add(new SegmentAssoc(s));

        //
        // drawArrangementTip();
        // drawEventTip();

        //
        // for (SegmentAssoc as : arrangements)
        // advanceSegment(as);

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

    /*
     * public void drawArrangementTip() { int counter = 0; for (SegmentAssoc as
     * : arrangements) { drawTextTip(String.valueOf(counter), as.upper);
     * counter++; } }
     */

    public void drawArrangementTip() {
        int counterU = 0;
        int counterL = 0;
        for (EventPoint e : arrangements) {
            switch (e.type) {
            case Upper:
                drawTextTip("Up(" + counterU + ")", e.p);
                counterU++;
                break;
            case Lower:
                drawTextTip("Low(" + counterL + ")", e.p);
                counterL++;
                break;
            }
        }
    }

    public void drawEventTip() {
        int counterU = 0;
        int counterL = 0;
        for (Event e : sweeplineStatus) {
            switch (e.type) {
            case Upper:
                drawTextTip("Up(" + counterU + ")", e.p);
                counterU++;
                break;
            case Lower:
                drawTextTip("Low(" + counterL + ")", e.p);
                counterL++;
                break;
            }
        }
    }

};

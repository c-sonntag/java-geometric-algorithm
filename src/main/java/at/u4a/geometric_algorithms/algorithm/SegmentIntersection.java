package at.u4a.geometric_algorithms.algorithm;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableSet;
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

    private static Segment swapToUpperIsOnPointA(Segment s) {
        if ((s.a.y > s.b.y) ? true : ((s.a.y < s.b.y) ? false : ((s.a.x >= s.b.x) ? true : false)))
            return new Segment(s.b, s.a);
        else
            return s;
    }

    /* ************** */

    private class SweepLineComparator implements Comparator<Segment> {
        @Override
        public int compare(Segment s1, Segment s2) {
            if (s1.a.equals(s1.a) && s2.b.equals(s2.b))
                return 0;
            final double wS1Decal = Math.abs(s1.a.x - s1.b.x), wS2Decal = Math.abs(s2.a.x - s2.b.x);
            return ((wS1Decal == wS2Decal) ? //
                    ((s1.a.x == s2.a.x) ? //
                            ((s1.b.x < s2.b.x) ? -1 : 1) : //
                            ((s1.a.x < s2.a.x) ? -1 : 1) //
                    ) : //
                    ((wS1Decal < wS2Decal) ? -1 : 1) //
            );
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
                Segment upperPointIsA = swapToUpperIsOnPointA(s);
                add(new EventPoint(upperPointIsA, EventType.Upper));
                add(new EventPoint(upperPointIsA, EventType.Lower));
            }
        }
    }

    /*
     * private class ArrangementComparator implements Comparator<SegmentAssoc> {
     * 
     * @Override public int compare(SegmentAssoc e1, SegmentAssoc e2) { return
     * (// e1.upper.equals(e2.upper) ? 0 : ( // (e1.upper.y == e2.upper.y) ? //
     * ((e1.upper.x < e2.upper.x) ? -1 : 1) : // ((e1.upper.y < e2.upper.y) ? -1
     * : 1)// )); } };
     */

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

    private final SweepLineComparator sweeplineComparator = new SweepLineComparator();
    private final Set<Segment> sweepline = new TreeSet<Segment>(sweeplineComparator);
    private final NavigableSet<Segment> sweeplineNavigator = (NavigableSet<Segment>) sweepline;

    private final Set<Event> sweeplineStatus = new TreeSet<Event>(new EventComparator());

    // private final Set<Point> intersectionsQueue = new TreeSet<Point>(new
    // Point.PointComparator());

    private final ArrayDeque<EventPoint> intersectionsQueue = new ArrayDeque<EventPoint>();

    private final Set<Point> intersectionsSet = new TreeSet<Point>(new Point.PointComparator());

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
        intersectionsQueue.clear();
        intersectionsQueue.push(arrangements.iterator().next());
        while (!intersectionsQueue.isEmpty())
            handleEventPoint(intersectionsQueue.pop());
    }

    private void addIfIntersection(Segment s1, Segment s2) {
        final Point intersectionPoint = Calc.intersection(s1, s2);
        if (intersectionPoint != null)
            intersectionsSet.add(intersectionPoint);
    }

    private void addInSweepline(Vector<Segment> ls) {
        for (Segment s : ls)
            addInSweepline(s);
    }

    private void addInSweepline(Segment s) {
        sweepline.add(swapToUpperIsOnPointA(s));
    }

    private void handleEventPoint(EventPoint ep) {

        statusAddCounter();

        Vector<Segment> upper = new Vector<Segment>();
        Vector<Segment> contain = new Vector<Segment>();
        Vector<Segment> lower = new Vector<Segment>();

        //
        for (Segment sa : sweepline) {
            if (sa.a.equals(ep.p))
                upper.add(sa);
            else if (sa.b.equals(ep.p))
                lower.add(sa);
            else
                contain.add(sa);
        }

        //
        if (!upper.contains(ep.s))
            upper.add(ep.s);

        //
        if ((contain.size() + upper.size() + lower.size()) > 1) {
            final Iterator<Segment> contain_it = contain.iterator(), upper_it = upper.iterator(), lower_it = lower.iterator();
            boolean iterate = true;
            while (iterate) {
                iterate = false;
                if (contain_it.hasNext()) {
                    addIfIntersection(ep.s, contain_it.next());
                    iterate = true;
                }
                if (upper_it.hasNext()) {
                    addIfIntersection(ep.s, upper_it.next());
                    iterate = true;
                }
                if (lower_it.hasNext()) {
                    addIfIntersection(ep.s, lower_it.next());
                    iterate = true;
                }
            }
        }

        // Why not ...
        sweepline.removeAll(lower);
        sweepline.removeAll(contain);

        // Why ???
        addInSweepline(upper);
        addInSweepline(contain);

        //
        /**
         * @todo ?? (∗ Deleting and re-inserting the segments of C(p) reverses
         *       their order. ∗) ??
         **/

        if ((contain.size() + upper.size()) == 0) {
            final Segment sLeft = sweeplineNavigator.lower(ep.s);
            final Segment sRight = sweeplineNavigator.higher(ep.s);
            if ((sLeft != null) && (sRight != null))
                findNewEvent(sLeft, sRight, ep.p);
            
        } else {

            Segment sLeftPrime = null, sRightPrime = null;

            final Iterator<Segment> contain_it = contain.iterator(), upper_it = upper.iterator();
            boolean iterate = true;
            while (iterate) {
                iterate = false;
                if (contain_it.hasNext()) {

                    final Segment cNext = contain_it.next();

                    if (sLeftPrime == null)
                        sLeftPrime = cNext;
                    else if (sweeplineComparator.compare(sLeftPrime, cNext) > 0)
                        sLeftPrime = cNext;
                    //
                    if (sRightPrime == null)
                        sRightPrime = cNext;

                    iterate = true;
                }
                if (upper_it.hasNext()) {

                    final Segment uNext = upper_it.next();

                    if (sLeftPrime == null)
                        sLeftPrime = uNext;

                    if (sRightPrime == null)
                        sRightPrime = uNext;
                    else if (sweeplineComparator.compare(sRightPrime, uNext) < 0)
                        sRightPrime = uNext;

                    iterate = true;
                }
            }

            if (sLeftPrime != null) {
                final Segment sLeft = sweeplineNavigator.lower(ep.s);
                if (sLeft != null)
                    findNewEvent(sLeft, sLeftPrime, ep.p);
            }
            if (sRightPrime != null) {
                final Segment sRight = sweeplineNavigator.higher(ep.s);
                if (sRight != null)
                    findNewEvent(sRightPrime, sRight, ep.p);
            }
        }

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
                EventPoint intersectionEventPoint = new EventPoint(intersectionPoint);
                if (!intersectionsQueue.contains(intersectionEventPoint))
                    intersectionsQueue.push(intersectionEventPoint);
            }
        }

    }

    /* ************** */

    private void advanceSegment(SegmentAssoc as) {

        // sweepline.add(as);
        // sweeplineStatus.add(new Event(as));

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
        findIntersections();

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

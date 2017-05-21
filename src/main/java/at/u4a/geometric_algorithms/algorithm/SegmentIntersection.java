package at.u4a.geometric_algorithms.algorithm;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import at.u4a.geometric_algorithms.algorithm.InterfaceAlgorithmBuilder;
import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.CloudOfPoints;
import at.u4a.geometric_algorithms.geometric.CloudOfSegments;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Segment;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.AlgorithmLayer;
import at.u4a.geometric_algorithms.utils.Calc;
import javafx.scene.input.SwipeEvent;
import javafx.scene.paint.Color;

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
            // statusBuildIs(buildSegmentInteractionQuadratique()); //<<- to
            // test
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

    /**
     * Contient le "cloud" qui est enregistré dans un "Set" avec comparator
     * "top-left" pour simuler le passage du "sweepline"
     */

    private static final SweepLineComparator sweeplineComparator = new SweepLineComparator();

    private final Arrangement arrangements = new Arrangement();

    private final Set<Segment> sweepline = new TreeSet<Segment>(sweeplineComparator);
    private final NavigableSet<Segment> sweeplineNavigator = (NavigableSet<Segment>) sweepline;

    private final ArrayDeque<EventPoint> intersectionsQueue = new ArrayDeque<EventPoint>();
    // private final Set<Point> intersectionsSet = new TreeSet<Point>(new
    // Point.PointComparator());

    /* ************** */

    private static class SweepLineComparator implements Comparator<Segment> {
        @Override
        public int compare(Segment s1, Segment s2) {
            if (s1.a.equals(s2.a) && s2.b.equals(s2.b))
                return 0;
            final double wS1Decal = (s1.a.x + s1.b.x) / 2, wS2Decal = (s2.a.x + s2.b.x) / 2;
            return ((wS1Decal == wS2Decal) ? //
                    ((s1.a.x == s2.a.x) ? //
                            ((s1.b.x < s2.b.x) ? -1 : 1) : //
                            ((s1.a.x < s2.a.x) ? -1 : 1) //
                    ) : //
                    ((wS1Decal < wS2Decal) ? -1 : 1) //
            );
        }
    };

    private static class Arrangement extends TreeSet<EventPoint> {

        private static final long serialVersionUID = -4678038553487397218L;

        private static class ArrangementComparator implements Comparator<EventPoint> {
            public int compare(EventPoint psa1, EventPoint psa2) {
                return (psa1.p.equals(psa2.p) ? 0 : ( //
                (psa1.p.y == psa2.p.y) ? //
                        ((psa1.p.x < psa2.p.x) ? -1 : 1) : //
                        ((psa1.p.y < psa2.p.y) ? -1 : 1) //
                ));
            }
        };

        /*
         * private static class SegmentComparator implements Comparator<Segment>
         * { public int compare(Segment s1, Segment s2) {
         * 
         * if (s1.a.equals(s2.a) && s1.b.equals(s2.b)) return 0;
         * 
         * final double wS1Decal = (s1.a.x + s1.b.x) / 2, wS2Decal = (s2.a.x +
         * s2.b.x) / 2; int xPosition = ((wS1Decal == wS2Decal) ? ((s1.a.x ==
         * s2.a.x) ? ((s1.b.x < s2.b.x) ? -1 : 1) : ((s1.a.x < s2.a.x) ? -1 :
         * 1)) : ((wS1Decal < wS2Decal) ? -1 : 1));
         * 
         * if(s1.a.y == s1.a.y) return xPosition;
         * 
         * 
         * 
         * 
         * int yPosition = 0;
         * 
         * } };
         */

        // public final Set<Segment> segments = new TreeSet<Segment>(new
        // SegmentComparator());
        // private final NavigableSet<Segment> segmentsNavigator =
        // (NavigableSet<Segment>) segments;

        public Arrangement() {
            super(new ArrangementComparator());
        }

        public void init(AbstractList<Segment> ls) {
            clear();
            // segments.clear();
            for (Segment s : ls) {
                if (s == null)
                    continue;
                else if ((s.a == null) || (s.b == null))
                    continue;
                //
                Segment upperPointIsA = swapToUpperIsOnPointA(s);
                // segments.add(upperPointIsA);
                add(new EventPoint(upperPointIsA, EventType.Upper));
                add(new EventPoint(upperPointIsA, EventType.Lower));
            }
        }

        /*
         * Vector<Segment> getContain(Point p) { Vector<Segment> vs = new
         * Vector<Segment>(); Segment s = new Segment(p, p); Segment sLower =
         * null; while ((sLower = segmentsNavigator.lower(s)) != null) { if
         * (sLower.b.y < p.y) break; vs.add(sLower); } Segment sHigher = null;
         * while ((sHigher = segmentsNavigator.higher(s)) != null) { if
         * (sHigher.a.y > p.y) break; vs.add(sHigher); } return vs; }
         */

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

    /* ************** */

    private void findIntersections() {
        intersectionsQueue.clear();
        if (arrangements.isEmpty())
            return;

        // Iterator<EventPoint> arrangements_it = arrangements.iterator();

        // while (arrangements_it.hasNext()) {
        for (EventPoint epArrNext : arrangements) {

            // EventPoint epArrNext = arrangements_it.next();
            intersectionsQueue.push(epArrNext);
            while (!intersectionsQueue.isEmpty()) {
                EventPoint epPop = intersectionsQueue.pop();
                handleEventPoint(epPop);
            }
        }
    }

    private void addIfIntersection(Segment s1, Segment s2) {
        statusAddCounter();
        final Point intersectionPoint = Calc.intersection(s1, s2);
        if (intersectionPoint != null) {
            // intersectionsSet.add(intersectionPoint);
            cop.addPoint(intersectionPoint);
        }
    }

    private void addInSweepline(Vector<Segment> ls) {
        for (Segment s : ls)
            addInSweepline(s);
    }

    private void addInSweepline(Segment s) {
        sweepline.add(swapToUpperIsOnPointA(s));
    }

    Set<Point> intersectionAlreadyHandle = new HashSet<Point>();

    private void handleEventPoint(EventPoint ep) {

        //if (!intersectionAlreadyHandle.contains(ep.p))
         //   intersectionAlreadyHandle.add(ep.p);

        //statusAddCounter();

        // drawEventPoint(ep);

        //
        Vector<Segment> upper = new Vector<Segment>();
        Vector<Segment> contain = new Vector<Segment>();
        Vector<Segment> lower = new Vector<Segment>();

        //
        for (Segment sa : sweepline) {
            if (sa.a.equals(ep.p))
                upper.add(sa);
            else if (sa.b.equals(ep.p))
                lower.add(sa);
            // else if ((sa.a.y > ep.p.y) && (sa.b.y < ep.p.y))
            else
                contain.add(sa);
        }

        //
        if (ep.type != EventType.Intersection) {

            if (ep.type == EventType.Upper)
                if (!upper.contains(ep.s))
                    upper.add(ep.s);
            if (ep.type == EventType.Lower)
                if (!lower.contains(ep.s))
                    lower.add(ep.s);

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

        }

        // drawSweepline();

        // drawLotsOfEventSegment(lower, EventType.Lower);

        // drawTopBottom(upper.iterator());
        drawTopBottom(contain.iterator());

        // Why not ...
        sweepline.removeAll(lower);
        sweepline.removeAll(contain);

        // Why ???
        addInSweepline(upper);
        addInSweepline(contain);

        // drawSweepline();

        //
        /**
         * @todo ?? (∗ Deleting and re-inserting the segments of C(p) reverses
         *       their order. ∗) ??
         **/
        if ((contain.size() + upper.size()) == 0) {

            if (ep.type == EventType.Intersection)
                return;

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
                    else if (sweeplineComparator.compare(sRightPrime, cNext) < 0)
                        sRightPrime = cNext;

                    iterate = true;
                }
                if (upper_it.hasNext()) {

                    final Segment uNext = upper_it.next();

                    if (sLeftPrime == null)
                        sLeftPrime = uNext;
                    else if (sweeplineComparator.compare(sLeftPrime, uNext) > 0)
                        sLeftPrime = uNext;

                    if (sRightPrime == null)
                        sRightPrime = uNext;
                    else if (sweeplineComparator.compare(sRightPrime, uNext) < 0)
                        sRightPrime = uNext;

                    iterate = true;
                }
            }

            if (sLeftPrime != null) {
                final Segment sLeft = sweeplineNavigator.lower(sLeftPrime);
                if (sLeft != null)
                    findNewEvent(sLeft, sLeftPrime, ep.p);
            }
            if (sRightPrime != null) {
                final Segment sRight = sweeplineNavigator.higher(sRightPrime);
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
        statusAddCounter();
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

    /**
     */
    private boolean buildSegmentInteraction() {

        //
        if (cloud.size() <= 2)
            return buildSegmentInteractionQuadratique();

        //
        cop.clear();
        arrangements.init(cloud);
        sweepline.clear();

        //
        // drawArrangementTip();
        // drawArrangementSegmentTip();

        //
        findIntersections();

        //
        // drawSweepline();

        //
        return true;
    }

    /* ************** */

    /**
     * Used for size <= 2
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

    public void drawSegment(Segment s) {
        if (mutableVisitorForDebugging == null)
            return;
        final Segment sToOrigin = new Segment();
        sToOrigin.set(s);
        as.convertToStandard(sToOrigin.a);
        as.convertToStandard(sToOrigin.b);
        mutableVisitorForDebugging.visit_unit(sToOrigin);
    }

    public void drawLotsOfEventSegment(AbstractList<Segment> as, EventType type) {
        for (Segment s : as) {
            if (type != EventType.Lower)
                drawPointWithEvent(s.a, type);
            if (type != EventType.Upper)
                drawPointWithEvent(s.b, type);
        }
    }

    public void drawEventPoint(EventPoint p) {
        drawPointWithEvent(p.p, p.type);
    }

    public void drawPointWithEvent(Point p, EventType type) {
        if (mutableVisitorForDebugging == null)
            return;

        final Point pToOrigin = new Point();
        pToOrigin.set(p);
        as.convertToStandard(pToOrigin);

        mutableVisitorForDebugging.getGraphicsContext().save();
        // mutableVisitorForDebugging.visit(pToOrigin);

        switch (type) {
        case Intersection:
            mutableVisitorForDebugging.getGraphicsContext().setStroke(Color.BROWN);
            break;
        case Lower:
            mutableVisitorForDebugging.getGraphicsContext().setStroke(Color.DARKMAGENTA);
            break;
        case Upper:
            mutableVisitorForDebugging.getGraphicsContext().setStroke(Color.DARKTURQUOISE);
            break;
        }
        mutableVisitorForDebugging.getGraphicsContext().strokeRect(pToOrigin.x - 10, pToOrigin.y + 5, 10, 20);
        mutableVisitorForDebugging.getGraphicsContext().restore();
    }

    public void drawSweepline() {
        drawTopBottom(sweepline.iterator());
    }

    private int count_drawTopBottom = 0;

    public void drawTopBottom(Iterator<Segment> ls_it) {
        if (mutableVisitorForDebugging == null)
            return;
        if (!ls_it.hasNext())
            return;

        // count_drawTopBottom++;

        // if((count_drawTopBottom%8)!=0)
        // return;

        //
        /*
         * Random rdn = new Random();
         * mutableVisitorForDebugging.getGraphicsContext().save();
         * mutableVisitorForDebugging.getGraphicsContext().setStroke(Color.color
         * (rdn.nextFloat() * 0.5 + 0.4, rdn.nextFloat() * 0.5 + 0.4,
         * rdn.nextFloat() * 0.5 + 0.4, 0.4));
         * mutableVisitorForDebugging.getGraphicsContext().setLineWidth(3); //
         * mutableVisitorForDebugging.getGraphicsContext().setStroke(Color.RED);
         */
        Segment s = ls_it.next();
        Point minY = s.a;
        Point maxY = s.b;
        int counter = 0;
        final Point pCenter = new Point();

        mutableVisitorForDebugging.getGraphicsContext().save();
        mutableVisitorForDebugging.getGraphicsContext().setStroke(Color.GREEN);

        boolean first = true;
        while (ls_it.hasNext()) {

            if (!first)
                s = ls_it.next();

            if (minY.y > s.a.y)
                minY = s.a;
            if (minY.y > s.b.y)
                minY = s.b;
            if (maxY.y < s.a.y)
                maxY = s.a;
            if (maxY.y < s.b.y)
                maxY = s.b;

            // drawSegment(s);

            pCenter.x = (s.a.x + s.b.x) / 2;
            pCenter.y = (s.a.y + s.b.y) / 2;
            drawTextTip("(" + counter + ")", pCenter);
            counter++;

            first = false;
        }

        final Point pMinToOrigin = new Point(), pMaxToOrigin = new Point();
        pMinToOrigin.set(minY);
        pMaxToOrigin.set(maxY);

        as.convertToStandard(pMinToOrigin);
        as.convertToStandard(pMaxToOrigin);

        mutableVisitorForDebugging.getGraphicsContext().setLineWidth(1);
        mutableVisitorForDebugging.getGraphicsContext().setStroke(Color.CORAL);
        mutableVisitorForDebugging.getGraphicsContext().strokeLine(pMinToOrigin.x - 300, pMinToOrigin.y - 2, pMinToOrigin.x + 300, pMinToOrigin.y - 2);
        mutableVisitorForDebugging.getGraphicsContext().setStroke(Color.SEAGREEN);
        mutableVisitorForDebugging.getGraphicsContext().strokeLine(pMaxToOrigin.x - 300, pMaxToOrigin.y + 2, pMaxToOrigin.x + 300, pMaxToOrigin.y + 2);

        mutableVisitorForDebugging.getGraphicsContext().restore();
    }

    public void drawArrangementTip() {
        int counter = 0;
        int counterU = 0;
        int counterL = 0;
        for (EventPoint e : arrangements) {
            switch (e.type) {
            case Upper:
                drawTextTip("Up(" + counterU + ")(" + counter + ")[" + e.p.toString() + "]", e.p);
                counterU++;
                break;
            case Lower:
                drawTextTip("Low(" + counterL + ")(" + counter + ")[" + e.p.toString() + "]", e.p);
                counterL++;

                break;
            }
            counter++;
        }
    }

    /*
     * public void drawArrangementSegmentTip() { if (mutableVisitorForDebugging
     * == null) return; int counter = 0; final Point pCenter = new Point();
     * mutableVisitorForDebugging.getGraphicsContext().save();
     * mutableVisitorForDebugging.getGraphicsContext().setStroke(Color.RED); for
     * (Segment s : arrangements.segments) { pCenter.x = (s.a.x + s.b.x) / 2;
     * pCenter.y = (s.a.y + s.b.y) / 2; drawTextTip("(" + counter + ")",
     * pCenter); counter++; }
     * mutableVisitorForDebugging.getGraphicsContext().restore(); }
     */

    /*
     * public void drawEventTip() { int counterU = 0; int counterL = 0; for
     * (Event e : sweeplineStatus) { switch (e.type) { case Upper:
     * drawTextTip("Up(" + counterU + ")", e.p); counterU++; break; case Lower:
     * drawTextTip("Low(" + counterL + ")", e.p); counterL++; break; } } }
     */

};

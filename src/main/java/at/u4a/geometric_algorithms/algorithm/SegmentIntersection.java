package at.u4a.geometric_algorithms.algorithm;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import at.u4a.geometric_algorithms.algorithm.InterfaceAlgorithmBuilder;
import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.CloudOfPoints;
import at.u4a.geometric_algorithms.geometric.CloudOfSegments;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Polygon;
import at.u4a.geometric_algorithms.geometric.Segment;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.AlgorithmLayer;
import at.u4a.geometric_algorithms.utils.Calc;
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
        public boolean canApply(AbstractList<AbstractLayer> layers) {
            if(layers.size() != 1)
                return false;
            //
            AbstractShape as = layers.get(0).getShape();
            return ((as instanceof CloudOfSegments) || (as instanceof Polygon));
        }

        static int SegmentIntersectionCount = 1;

        @Override
        public AbstractLayer builder(AbstractList<AbstractLayer> layers) {

            if(layers.isEmpty())
                throw new RuntimeException("Need one layer !");
            
            //
            AbstractShape as = layers.get(0).getShape();
            Iterable<Segment> cloud = null;

            //
            if (as instanceof CloudOfSegments)
                cloud = ((CloudOfSegments) as).cloud;
            else if (as instanceof Polygon)
                cloud = ((Polygon) as);
            else
                throw new RuntimeException("SegmentIntersection need a CloudOfSegments Shape, or Polygon Shape !");

            //
            AbstractLayer al = new AlgorithmLayer<SegmentIntersection>(new SegmentIntersection(cloud, as), Algorithm.SegmentIntersection, layers);
            al.setLayerName("si" + String.valueOf(SegmentIntersectionCount));
            SegmentIntersectionCount++;
            return al;
        }

    };

    static double NOT_EXACTLY_MIN = 0.01;

    private final Iterable<Segment> cloud;
    private final AbstractShape as;
    private final CloudOfPoints cop;
    private final boolean notExactly;

    public SegmentIntersection(Iterable<Segment> cloud, AbstractShape as) {
        this(cloud, as, false);
    }

    public SegmentIntersection(Iterable<Segment> cloud, AbstractShape as, boolean notExactly) {
        super("intersections tests");
        this.cloud = cloud;
        this.as = as;
        this.cop = new CloudOfPoints(as.origin);
        this.notExactly = notExactly;
    }

    /* ************** */

    InterfaceGraphicVisitor mutableVisitorForDebugging = null;

    @Override
    public void accept(AbstractList<AbstractLayer> subLayers, InterfaceGraphicVisitor visitor) {
        // mutableVisitorForDebugging = visitor;
        makeSegmentInteraction();
        visitor.visit(cop);
    }

    @Override
    public int hashCode() {
        return as.hashCode();
    }

    /* ************** */

    public CloudOfPoints getCloudOfPoint() {
        makeSegmentInteraction();
        return cop;
    }

    public boolean haveIntersections() {
        makeSegmentInteraction();
        return !cop.cloud.isEmpty();
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

    /**
     * Si le test ne doit pas comprendre les intersection au niveau des points d'extremités
     */
    private boolean checkNotExactly(double i, double j) {
        return Math.abs(i - j) <= NOT_EXACTLY_MIN;
    }

    private Point getIntersection(Segment s1, Segment s2) {
        statusAddCounter();

        //
        final Point intersectionPoint = notExactly ? Calc.intersectionOnLine(s1, s2) : Calc.intersection(s1, s2);
        if (notExactly)
            if ((intersectionPoint != null))
                if (checkNotExactly(intersectionPoint.x, s1.a.x) || checkNotExactly(intersectionPoint.x, s1.b.x) || checkNotExactly(intersectionPoint.x, s2.a.x) || checkNotExactly(intersectionPoint.x, s2.b.x) || //
                        checkNotExactly(intersectionPoint.y, s1.a.y) || checkNotExactly(intersectionPoint.y, s1.b.y) || checkNotExactly(intersectionPoint.y, s2.a.y) || checkNotExactly(intersectionPoint.y, s2.b.y))//
                    return null;
        //
        return intersectionPoint;
    }

    /* ************** */

    private static final SweepLineComparator sweeplineComparator = new SweepLineComparator();

    private final Arrangement arrangements = new Arrangement();

    private final Set<Segment> sweepline = new TreeSet<Segment>(sweeplineComparator);
    private final NavigableSet<Segment> sweeplineNavigator = (NavigableSet<Segment>) sweepline;

    private final ArrayDeque<EventPoint> intersectionsQueue = new ArrayDeque<EventPoint>();

    /* ************** */

    private static class SweepLineComparator implements Comparator<Segment> {
        @Override
        public int compare(Segment s1, Segment s2) {
            if (s1.a.equals(s2.a) && s1.b.equals(s2.b))
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
                return (psa1.s.equals(psa2.s) ? 0 : ( //
                (psa1.p.y == psa2.p.y) ? //
                        ((psa1.p.x < psa2.p.x) ? -1 : 1) : //
                        ((psa1.p.y < psa2.p.y) ? -1 : 1) //
                ));
            }
        };

        // public final NavigableSet<EventPoint> navigator =
        // (NavigableSet<EventPoint>) this;

        public Arrangement() {
            super(new ArrangementComparator());
        }

        public void init(Iterable<Segment> ls) {
            clear();
            for (Segment s : ls) {
                if (s == null)
                    continue;
                else if ((s.a == null) || (s.b == null))
                    continue;
                //
                Segment upperPointIsA = swapToUpperIsOnPointA(s);
                add(new EventPoint(upperPointIsA, EventType.Upper));
                add(new EventPoint(upperPointIsA, EventType.Lower));
            }
        }

    }

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
        for (EventPoint ep : arrangements) {
            intersectionsQueue.push(ep);
            while (!intersectionsQueue.isEmpty()) {
                EventPoint epPop = intersectionsQueue.pop();
                handleEventPoint(epPop);
            }
        }
    }

    private void addIfIntersection(Segment s1, Segment s2) {
        final Point intersectionPoint = getIntersection(s1, s2);
        if (intersectionPoint != null) {
            cop.addPoint(intersectionPoint);
        }
    }

    private Vector<Segment> upper = new Vector<Segment>();
    private Vector<Segment> contain = new Vector<Segment>();
    private Vector<Segment> lower = new Vector<Segment>();

    private void handleEventPoint(EventPoint ep) {

        //
        upper.clear();
        contain.clear();
        lower.clear();

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
        if (ep.type != EventType.Intersection) {

            if (ep.type == EventType.Upper)
                upper.add(ep.s);
            if (ep.type == EventType.Lower)
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

        //
        sweepline.removeAll(lower);
        // sweepline.removeAll(contain);

        //
        sweepline.addAll(upper);
        sweepline.addAll(contain);

        //
        /**
         * @todo ?? (∗ Deleting and re-inserting the segments of C(p) reverses
         *       their order. ∗) ??
         **/

        //
        if (contain.isEmpty() && upper.isEmpty()) {

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
        final Point intersectionPoint = getIntersection(left, right);

        if (notExactly) {
            if (left.a == right.a || left.a == right.b || left.b == right.a || left.b == right.b)
                return;
        }

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

    // int countForcePush = 0;

    /**
     */
    private boolean buildSegmentInteraction() {

        //
        cop.clear();
        arrangements.init(cloud);
        sweepline.clear();

        //
        // countForcePush = 0;
        findIntersections();

        // System.out.println("CountForcePush : " + countForcePush);
        // System.out.println("Size : " + arrangements.size());

        //
        return true;
    }

    /*
     * ****************************** DEBUG ONLY ******************************
     */

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

    public void drawTopBottom(Iterator<Segment> ls_it) {
        drawTopBottom(ls_it, 0);
    }

    Color[] upperColor = { Color.CORAL, Color.CHOCOLATE, Color.AQUAMARINE };
    Color[] lowerColor = { Color.SEAGREEN, Color.CRIMSON, Color.CHARTREUSE };

    public void drawTopBottom(Iterator<Segment> ls_it, int flag) {
        if (mutableVisitorForDebugging == null)
            return;
        if (!ls_it.hasNext())
            return;

        Segment s = ls_it.next();
        Point minY = s.a;
        Point maxY = s.b;
        int counter = 0;
        final Point pCenter = new Point();

        mutableVisitorForDebugging.getGraphicsContext().save();

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
        mutableVisitorForDebugging.getGraphicsContext().setStroke(upperColor[flag]);
        mutableVisitorForDebugging.getGraphicsContext().strokeLine(pMinToOrigin.x - 300, pMinToOrigin.y - (2 + 2 * flag), pMinToOrigin.x + 300, pMinToOrigin.y - (2 + 2 * flag));
        mutableVisitorForDebugging.getGraphicsContext().setStroke(lowerColor[flag]);
        mutableVisitorForDebugging.getGraphicsContext().strokeLine(pMaxToOrigin.x - 300, pMaxToOrigin.y + (2 + 2 * flag), pMaxToOrigin.x + 300, pMaxToOrigin.y + (2 + 2 * flag));

        mutableVisitorForDebugging.getGraphicsContext().restore();
    }

    public void drawSquare(Point p, int flag) {
        if (mutableVisitorForDebugging == null)
            return;

        final Point pToOrigin = new Point();

        pToOrigin.set(p);
        as.convertToStandard(pToOrigin);

        mutableVisitorForDebugging.getGraphicsContext().restore();
        mutableVisitorForDebugging.getGraphicsContext().setStroke(upperColor[flag]);
        mutableVisitorForDebugging.getGraphicsContext().setFill(lowerColor[flag]);

        mutableVisitorForDebugging.getGraphicsContext().strokeRect(pToOrigin.x - 5, pToOrigin.y - 5, 10, 10);
        mutableVisitorForDebugging.getGraphicsContext().fillRect(pToOrigin.x - 5, pToOrigin.y - 5, 10, 10);

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

};

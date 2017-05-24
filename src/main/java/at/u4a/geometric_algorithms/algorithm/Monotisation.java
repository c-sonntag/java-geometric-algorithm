package at.u4a.geometric_algorithms.algorithm;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import at.u4a.geometric_algorithms.algorithm.InterfaceAlgorithmBuilder;
import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Polygon;
import at.u4a.geometric_algorithms.geometric.Polygon.MonotonePolygon;
import at.u4a.geometric_algorithms.geometric.Segment;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.AlgorithmLayer;
import at.u4a.geometric_algorithms.utils.Calc;
import at.u4a.geometric_algorithms.utils.Collection;
import javafx.scene.paint.Color;

/**
 * Monotisation
 * 
 * @author Hip
 *
 */
public class Monotisation extends AbstractAlgorithm {

    public static class Builder implements InterfaceAlgorithmBuilder {

        @Override
        public String getName() {
            return Monotisation.class.getSimpleName();
        }

        @Override
        public boolean canApply(AbstractLayer l) {
            return (l.getShape() instanceof Polygon);
        }

        static int MonotisationCount = 1;

        @Override
        public AbstractLayer builder(AbstractLayer l) {

            //
            AbstractShape as = l.getShape();
            if (!(as instanceof Polygon))
                throw new RuntimeException("Monotisation need a Polygon Shape !");

            //
            Polygon poly = (Polygon) as;

            //
            AbstractLayer al = new AlgorithmLayer<Monotisation>(new Monotisation(poly.perimeter, poly), Algorithm.Monotisation, l);
            al.setLayerName("m" + String.valueOf(MonotisationCount));
            MonotisationCount++;
            return al;
        }

    };

    private final AbstractList<Point> points;
    private final AbstractShape as;

    private final Vector<MonotonePolygon> mp_v;

    public Monotisation(Vector<Point> points, AbstractShape as) {
        this.points = points;
        this.as = as;
        this.mp_v = new Vector<MonotonePolygon>();
        // this.mp_v = new MonotonePolygon(as.origin, points);
    }

    /* ************** */

    private InterfaceGraphicVisitor mutableVisitorForDebugging = null;

    private int countStroboscope = 0;

    private Color colorStrop[] = { Color.CORAL, Color.CRIMSON, Color.AQUA, Color.AQUAMARINE, Color.AZURE, Color.BEIGE, Color.BISQUE };

    @Override
    public void accept(Vector<AbstractLayer> v, InterfaceGraphicVisitor visitor) {

        mutableVisitorForDebugging = visitor;

        countStroboscope = 0;

        makeMonotisation();

        mutableVisitorForDebugging.getGraphicsContext().save();

        if (haveMake) {
            for (MonotonePolygon mp : mp_v) {

                mutableVisitorForDebugging.getGraphicsContext().setStroke(colorStrop[countStroboscope]);

                visitor.visit(mp);

                countStroboscope = (countStroboscope + 1) % colorStrop.length;

            }
        }

        mutableVisitorForDebugging.getGraphicsContext().restore();

    }

    /* ************** */

    public boolean haveMadeSubMonotonePolyon() {
        makeMonotisation();
        return haveMake;
    }

    public Vector<MonotonePolygon> getMonotonesPolygon() {
        makeMonotisation();
        return (haveMake) ? mp_v : null;
    }

    /* ************** */

    @Override
    public AbstractShape getCompositeShape() {
        return null;
    }

    /* ************** */

    private int mutablePreviousPolyHash = 0;
    private boolean haveMake = false;

    protected void makeMonotisation() {

        int currentPolyHash = as.hashCode();
        if (currentPolyHash != mutablePreviousPolyHash) {

            //
            statusStartBuild();

            //
            if (haveMake = buildMonotisation()) {
                statusFinishBuild();
            } else
                statusInteruptBuild();

            //
            mutablePreviousPolyHash = currentPolyHash;
        }

    }

    /* ************** */

    private static class StatusComparator_1 implements Comparator<Segment> {
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

    private static class StatusComparator_2 implements Comparator<Segment> {
        @Override
        public int compare(Segment s1, Segment s2) {
            if (s1.a.equals(s2.a) && s2.b.equals(s2.b))
                return 0;

            final double wS1Decal = (s1.a.x + s1.b.x) / 2, wS2Decal = (s2.a.x + s2.b.x) / 2;
            final double hS1Decal = (s1.a.y + s1.b.y) / 2, hS2Decal = (s2.a.y + s2.b.y) / 2;

            final double hS1 = Math.abs(s1.a.y - s1.b.y), hS2 = Math.abs(s2.a.y - s2.b.y);
            final double s1Top = Math.max(s1.a.y, s1.b.y), s1Bottom = Math.max(s1.a.y, s1.b.y);
            final double s2Top = Math.max(s2.a.y, s2.b.y), s2Bottom = Math.max(s2.a.y, s2.b.y);

            // final boolean s2isGreaterRight = wS1Decal < wS2Decal;
            final boolean s2isGreaterRight = Math.min(s1.a.x, s1.b.x) < Math.min(s2.a.x, s2.b.x);
            final boolean s2isInHeightRange = (hS2 <= hS1) && (s1Top <= s2Top) && (s1Bottom >= s2Bottom);

            if (s2isGreaterRight) {
                return s2isInHeightRange ? 1 : -1;
            } else {
                return 1;
            }
        }
    };

    private static class StatusComparator_3 implements Comparator<Segment> {
        @Override
        public int compare(Segment s1, Segment s2) {
            if (s1.a.equals(s2.a) && s1.b.equals(s2.b))
                return 0;
            final double s1x = Math.max(s1.a.x, s1.b.x), s2x = Math.max(s2.a.x, s2.b.x);
            final double s1y = Math.max(s1.a.y, s1.b.y), s2y = Math.max(s2.a.y, s2.b.y);
            return (s1x != s2x) ? ((s1x < s2x) ? -1 : 1) : ((s1y <= s2y) ? -1 : 1);
        }
    };

    private static class StatusComparator implements Comparator<Segment> {

        @Override
        public int compare(Segment s1, Segment s2) {

            if (s1.a.equals(s2.a) && s1.b.equals(s2.b))
                return 0;

            /*
             * final double s1x = Math.max(s1.a.x, s1.b.x); final double s2x =
             * Math.max(s2.a.x, s2.b.x); final double s1y = Math.max(s1.a.y,
             * s1.b.y); final double s2y = Math.max(s2.a.y, s2.b.y);
             */

            final double s1xMax = Math.max(s1.a.x, s1.b.x);
            final double s2xMax = Math.max(s2.a.x, s2.b.x);

            final double s1xMin = Math.min(s1.a.x, s1.b.x);
            final double s2xMin = Math.min(s2.a.x, s2.b.x);

            if ((s1xMin < s2xMin) || (s1xMax < s2xMax))
                return -1;

            if ((s1xMin == s2xMin) && (s1xMax == s2xMax))
                return ((Math.max(s1.a.y, s1.b.y) < Math.max(s2.a.y, s2.b.y)) ? -1 : 1);

            return +1;

            // turn (s1xMax != s2xMax) ? xMax : yMax;

            /*
             * int xMin = ((Math.min(s1.a.x, s1.b.x) < Math.min(s2.a.x, s2.b.x))
             * ? -1 : 1); int xMax = (s1xMax < s2xMax) ? -1 : 1;
             * 
             * if ((xMin < 0) || (xMax < 0)) return -1;
             * 
             * int yMax = ((Math.max(s1.a.x, s1.b.x) < Math.max(s2.a.x, s2.b.x))
             * ? -1 : 1);
             * 
             * return (s1xMax != s2xMax) ? xMax : yMax;
             */

            /*
             * final double s1x = Math.max(s1.a.x, s1.b.x); final double s2x =
             * Math.max(s2.a.x, s2.b.x); final double s1y = Math.max(s1.a.y,
             * s1.b.y); final double s2y = Math.max(s2.a.y, s2.b.y);
             */

            // final double s1x = Math.abs(s1.a.x - s1.b.x), s2x =
            // Math.abs(s2.a.x - s2.b.x);
            // final double s1y = Math.abs(s1.a.y - s1.b.y), s2y =
            // Math.abs(s2.a.y - s2.b.y);

            /*
             * final double s1x = Math.max(s1.a.x, s1.b.x) + Math.abs(s1.a.x -
             * s1.b.x); final double s2x = Math.max(s2.a.x, s2.b.x) +
             * Math.abs(s2.a.x - s2.b.x); final double s1y = Math.max(s1.a.y,
             * s1.b.y) + Math.abs(s1.a.y - s1.b.y); final double s2y =
             * Math.max(s2.a.y, s2.b.y) + Math.abs(s2.a.y - s2.b.y);
             */

            // return (s1x != s2x) ? ((s1x < s2x) ? -1 : 1) : ((s1y <= s2y) ? -1
            // : 1);

            // final double wS1Decal = (s1.a.x + s1.b.x) / 2, wS2Decal = (s2.a.x
            // + s2.b.x) / 2;
            // final boolean s2isGreaterRight = wS1Decal < wS2Decal;

            // double s1X = Math.min(s1.a.x, s1.b.x);
            // double s2X = Math.min(s2.a.x, s2.b.x);

            // final boolean s2isGreaterRight = Math.min(s1.a.x, s1.b.x) <=
            // Math.min(s2.a.x, s2.b.x);

            // return (s2isGreaterRight) ? 1 : -1;
        }
    };

    /*
     * private static class VertexInformComparator implements
     * Comparator<VertexInform> { private Comparator<Point> comparator = new
     * Point.PointUpLeftAlignementComparator();
     * 
     * public int compare(VertexInform vi1, VertexInform vi2) { return
     * comparator.compare(vi1, vi2); } };
     */

    /* ************** */

    private final Set<Edge> status = new TreeSet<Edge>(new StatusComparator());
    private final NavigableSet<Edge> statusNavigator = (NavigableSet<Edge>) status;

    // private final PriorityQueue<VertexInform> intersectionsQueue = new
    // PriorityQueue<VertexInform>();
    // private final PriorityQueue<VertexInform> intersectionsQueue = new
    // PriorityQueue<VertexInform>();

    private final Set<VertexInform> verticesInform = new TreeSet<VertexInform>(new Point.PointUpLeftAlignementComparator());
    // private final Vector<VertexInform> vertices = new Vector<VertexInform>();

    private VertexInform firstVertexInform = null;

    private final Set<VertexInform> helper = new TreeSet<VertexInform>(new Point.PointUpLeftAlignementComparator());

    private final Vector<Segment> bordersBySegment = new Vector<Segment>();
    private final Map<VertexInform, HashSet<VertexInform>> bordersMap = new HashMap<VertexInform, HashSet<VertexInform>>();

    /* ************** */

    static enum VertexType {
        Unknown, Start, End, Split, Merge, RegularLeft, RegularRight,
    };

    class Edge extends Segment {

        public VertexInform helper = null;

        public Edge(VertexInform a, VertexInform b) {
            super(a, b);
        }
    };

    class VertexInform extends Point {
        public VertexType type = VertexType.Unknown;
        public VertexInform back = null, next = null;

        public VertexInform divergeTo = null, divergeFrom = null;

        public Edge nextEdge = null;

        public VertexInform(Point p) {
            super(p);
        }

        public Edge getNextEdge() {
            if (nextEdge == null)
                nextEdge = new Edge(this, next);
            return nextEdge;
        }
        /*
         * public Edge getNextEdge() { if (nextEdge == null) nextEdge = new
         * Edge(back, this); return nextEdge; }
         */

    };

    /* ************** */

    class NotDirectLeftFindException extends Exception {
        private static final long serialVersionUID = -1665815895025535555L;
    }

    private void attachBorder(VertexInform vi1, VertexInform vi2) {

        vi1.divergeTo = vi2;
        vi2.divergeFrom = vi1;
        bordersBySegment.add(new Segment(vi1, vi2));

        // drawBorder(bordersBySegment.lastElement());

        //
        HashSet<VertexInform> bm1 = bordersMap.get(vi1);
        if (bm1 == null) {
            bm1 = new HashSet<VertexInform>();
            bordersMap.put(vi1, bm1);
        }
        bm1.add(vi2);

        //
        HashSet<VertexInform> bm2 = bordersMap.get(vi2);
        if (bm2 == null) {
            bm2 = new HashSet<VertexInform>();
            bordersMap.put(vi2, bm2);
        }
        bm2.add(vi1);
    }

    StatusComparator sc = new StatusComparator();

    private final Edge getDirectLeft(VertexInform vi) throws NotDirectLeftFindException {
        final Edge viSearch = new Edge(vi, vi);
        final Edge eDirectLeftOfVi = statusNavigator.floor(viSearch);
        //
        if (eDirectLeftOfVi != null)
            return eDirectLeftOfVi;
        else {

            final Edge eDirectRightOfVi = statusNavigator.higher(viSearch);

            System.out.println("Not find eDirectLeft Of vi(" + vi.toString() + "), eDirectRightOfVi(" + eDirectRightOfVi + ") sc(" + sc.compare(viSearch, eDirectRightOfVi) + ")");

            throw new NotDirectLeftFindException();
        }
    }

    /* ************** */

    private void handleStartVertex(VertexInform vi) {
        final Edge e = vi.getNextEdge();
        e.helper = vi;
        status.add(e);
    }

    private void handleEndVertex(VertexInform vi) {
        final Edge eBack = vi.back.getNextEdge();
        if (eBack.helper != null) {
            if (eBack.helper.type == VertexType.Merge)
                attachBorder(vi, eBack.helper);
        }
        status.remove(eBack);
    }

    private void handleSplitVertex(VertexInform vi) throws NotDirectLeftFindException {
        final Edge eDirectLeftOfVi = getDirectLeft(vi);
        if (eDirectLeftOfVi.helper != null)
            attachBorder(vi, eDirectLeftOfVi.helper);
        eDirectLeftOfVi.helper = vi;

        //
        final Edge e = vi.getNextEdge();
        e.helper = vi;
        status.add(e);
    }

    private void handleMergeVertex(VertexInform vi) throws NotDirectLeftFindException {
        final Edge eBack = vi.back.getNextEdge();
        if (eBack.helper != null) {
            if (eBack.helper.type == VertexType.Merge)
                attachBorder(vi, eBack.helper);
        }
        status.remove(eBack);

        //
        final Edge eDirectLeftOfVi = getDirectLeft(vi);
        if (eDirectLeftOfVi.helper != null) {
            if (eDirectLeftOfVi.helper.type == VertexType.Merge)
                attachBorder(vi, eDirectLeftOfVi.helper);
        }
        eDirectLeftOfVi.helper = vi;
    }

    private void handleRegularVertex(VertexInform vi) throws NotDirectLeftFindException {
        if (vi.type == VertexType.RegularLeft) {
            //
            final Edge eBack = vi.back.getNextEdge();
            if (eBack.helper != null) {
                if (eBack.helper.type == VertexType.Merge)
                    attachBorder(vi, eBack.helper);
            }

            //
            status.remove(eBack);
            final Edge e = vi.getNextEdge();
            e.helper = vi;
            status.add(e);

        } else {
            final Edge eDirectLeftOfVi = getDirectLeft(vi);
            if (eDirectLeftOfVi.helper != null) {
                if (eDirectLeftOfVi.helper.type == VertexType.Merge)
                    attachBorder(vi, eDirectLeftOfVi.helper);
            }
        }
    }

    /* ************** */

    private boolean partitionPolygon() {

        bordersBySegment.clear();
        bordersMap.clear();
        status.clear();

        // int counter = 0;

        //
        try {

            for (VertexInform vi : verticesInform) {
                statusAddCounter();

                switch (vi.type) {
                case End:
                    handleEndVertex(vi);
                    break;
                case Merge:
                    handleMergeVertex(vi);
                    break;
                case RegularLeft:
                case RegularRight:
                    handleRegularVertex(vi);
                    break;
                case Split:
                    handleSplitVertex(vi);
                    break;
                case Start:
                    handleStartVertex(vi);
                    break;
                }

                // drawStatusTip(counter++);
                drawStatusMinus();

            }

        } catch (NotDirectLeftFindException useless) {
            return false;
        }

        return true;
    }

    /* ************** */

    private void markTypeOfVertexInform(VertexInform vi) {

        final double produit = Calc.resumeProduitVectorielZ(vi.back, vi, vi.next);

        final boolean haveUpperNeighbour = (vi.y >= vi.back.y) && (vi.y > vi.next.y);
        final boolean haveLowerNeighbour = (vi.y <= vi.back.y) && (vi.y < vi.next.y);

        final boolean isLesserThanPi = produit >= 0;

        if (haveLowerNeighbour)
            vi.type = isLesserThanPi ? VertexType.Start : VertexType.Split;
        else if (haveUpperNeighbour)
            vi.type = isLesserThanPi ? VertexType.End : VertexType.Merge;
        else
            vi.type = (vi.back.y < vi.next.y) ? VertexType.RegularLeft : VertexType.RegularRight;
    }

    private boolean createVertexInform() {

        if (points.size() < 3)
            return false;

        verticesInform.clear();
        firstVertexInform = null;

        //
        double sumVecZ = Calc.getClockwise(points);
        if (sumVecZ == 0)
            return false;

        System.out.println("sumVecZ=" + sumVecZ);

        //
        final Iterator<Point> p_it = (sumVecZ < 0) ? points.iterator() : new Collection.ReverseIterable<Point>(points).iterator();

        //
        final VertexInform firstVI = new VertexInform(p_it.next());
        VertexInform lastVI = firstVI, newVI = null, lastLastVi = null;

        //
        while (p_it.hasNext()) {
            statusAddCounter();

            newVI = new VertexInform(p_it.next());
            newVI.back = lastVI;
            lastVI.next = newVI;

            //
            if (lastVI.back != null) {
                markTypeOfVertexInform(lastVI);
            }

            //
            verticesInform.add(newVI);

            lastLastVi = lastVI;
            lastVI = newVI;
        }

        //
        lastLastVi.next = newVI;
        newVI.back = lastLastVi;
        newVI.next = firstVI;
        markTypeOfVertexInform(newVI);

        //
        firstVI.back = newVI;
        markTypeOfVertexInform(firstVI);
        verticesInform.add(firstVI);
        firstVertexInform = firstVI;

        //
        return true;
    }

    /* ************** */

    class NotComputeVecZException extends Exception {
        private static final long serialVersionUID = -959335325844810586L;
    }

    private final MonotonePolygon createEmptyMonotonePolygon() {
        final MonotonePolygon mp = new MonotonePolygon(as.origin, new Vector<Point>());
        mp_v.add(mp);
        return mp;
    }

    private int mutableDebugLotsLoop = 0;

    private boolean seeParcours = false;
    private int parcoursCount = 0;

    private void courseVertices_1(final MonotonePolygon mpStart, final VertexInform viStart, final VertexInform viToStopAndCloseMp) throws NotComputeVecZException {
        VertexInform vi = viStart;
        MonotonePolygon mp = mpStart;

        while (vi != viToStopAndCloseMp) {

            if (seeParcours)
                drawTextTipPosDecal("●" + (parcoursCount++), vi, -3);

            if (mutableDebugLotsLoop > 200)
                break;

            mutableDebugLotsLoop++;

            statusAddCounter();

            mp.addPoint(vi);

            /*
             * if (vi.divergeTo != null) drawTextTipPosDecal("├ ", vi.divergeTo,
             * 2); if (vi.divergeFrom != null) drawTextTipPosDecal(" ┤",
             * vi.divergeFrom, 2);
             */

            if (vi.divergeTo != null) {

                final MonotonePolygon subMp = createEmptyMonotonePolygon();
                subMp.addPoint(vi);
                courseVertices(subMp, vi.next, vi.divergeTo);

                mp.addPoint(vi.divergeTo);
                vi = vi.divergeTo.next;

            } else if (vi.divergeFrom != null) {

                final MonotonePolygon subMp = mp;
                subMp.addPoint(vi.divergeFrom);
                courseVertices(subMp, vi.divergeFrom.next, viStart);

                mp = createEmptyMonotonePolygon();
                mp.addPoint(vi);

                vi = vi.next;

            } else if (((vi.divergeTo != null) || (vi.divergeFrom != null)) && false) {

                drawTextTipPosDecal("100", vi.back, 1);
                drawTextTipPosDecal("000", vi, 1);
                drawTextTipPosDecal("001", vi.next, 1);

                if (vi.divergeTo != null)
                    drawTextTipPosDecal("├", vi.divergeTo, 2);
                if (vi.divergeFrom != null)
                    drawTextTipPosDecal("┤", vi.divergeFrom, 2);

                VertexInform subViEnd = (vi.divergeTo != null) ? vi.divergeTo : vi.divergeFrom;

                final MonotonePolygon subMp = createEmptyMonotonePolygon();
                subMp.addPoint(vi);

                //
                seeParcours = true;
                courseVertices(subMp, vi.next, subViEnd);
                parcoursCount = 0;
                seeParcours = false;

                //
                vi = subViEnd;

                // drawTextTipPosDecal(String.valueOf(System.identityHashCode(vi.divergeTo)),
                // vi.divergeTo, -1);
                /*
                 * final MonotonePolygon subMp = createEmptyMonotonePolygon();
                 * subMp.addPoint(vi); courseVertices(subMp, vi.next,
                 * vi.divergeTo);
                 * 
                 * // // mp.addPoint(vi.divergeTo); vi = vi.divergeTo;
                 */
            } else
                vi = vi.next;
        }

        if (seeParcours)
            drawTextTipPosDecal((parcoursCount++) + "∆", vi, -3);

        mp.addPoint(vi);

        drawPolygon(mp);

        // sumOfVectorialZProd += resumeProduitVectorielZ(pA, pO, pB);
    }

    private boolean createSubMonotone_1() {
        mp_v.clear();
        if (firstVertexInform == null)
            return false;

        mutableDebugLotsLoop = 0;

        /*
         * final ArrayDeque<>
         * 
         * Vector<Point> perimeter = new Vector<Point>();
         * 
         * Iterator<Point> p_it = pl.iterator(); Point pA = p_it.next(); Point
         * pO = p_it.next(); while (p_it.hasNext()) { Point pB = p_it.next();
         * sumOfVectorialZProd += resumeProduitVectorielZ(pA, pO, pB); } return
         * sumOfVectorialZProd;
         * 
         * double sumOfVectorialZProd = 0;
         */
        // VertexInform viFirst = vertices.iterator().next();

        try {
            courseVertices_1(createEmptyMonotonePolygon(), firstVertexInform, firstVertexInform.back);

        } catch (NotComputeVecZException useless) {
            return false;
        }

        return true;
    }

    /*
     * private void courseVertices(final MonotonePolygon mpStart, final
     * VertexInform viStart, final VertexInform viToStopAndCloseMp) throws
     * NotComputeVecZException { VertexInform vi = viStart; MonotonePolygon mp =
     * mpStart;
     * 
     * while (vi != viToStopAndCloseMp) {
     * 
     * 
     * 
     * vi = vi.next;
     * 
     * }
     * 
     * }
     */

    class CourseEntry {
        final MonotonePolygon mp;
        final VertexInform start;

        public CourseEntry(VertexInform start) {
            this.mp = createEmptyMonotonePolygon();
            this.start = start;
        }
    }

    private boolean createSubMonotone() {
        mp_v.clear();
        if (firstVertexInform == null)
            return false;

        ArrayDeque<CourseEntry> queue = new ArrayDeque<CourseEntry>();

        //
        CourseEntry curentCourse = new CourseEntry(firstVertexInform);
        VertexInform vi = firstVertexInform;
        VertexInform viToStopAndCloseMp = firstVertexInform.back;

        //
        queue.push(curentCourse);

        //
        while (vi != viToStopAndCloseMp) {
            curentCourse.mp.addPoint(vi);

            //
            final HashSet<VertexInform> borders = bordersMap.get(vi);
            if (borders != null) {

                System.out.print("(" + queue.size() + ") ");

                //
                final VertexInform oppositeBorder = curentCourse.start;
                if (borders.contains(oppositeBorder)) {

                    System.out.print(vi.toString() + "|- ");

                    curentCourse = queue.pop();
                    curentCourse.mp.addPoint(vi);
                }

                //
                for (VertexInform borderVi : borders) {
                    if (borderVi != oppositeBorder) {

                        System.out.print(vi.toString() + "|+ ");

                        curentCourse = new CourseEntry(vi);
                        curentCourse.mp.addPoint(vi);
                        queue.push(curentCourse);

                    }
                }
            }

            vi = vi.next;
        }

        curentCourse.mp.addPoint(vi);

        System.out.println();

        System.out.println("NbPolygon(" + mp_v.size() + ")");

        /*
         * // try { courseVertices(createEmptyMonotonePolygon(),
         * firstVertexInform, firstVertexInform.back); return true; } catch
         * (NotComputeVecZException useless) { return false; }
         */

        return true;
    }

    /* ************** */

    private boolean buildMonotisation() {

        /** @todo find if the polygon have colision */
        System.out.println();

        //
        if (!createVertexInform())
            return false;

        //
        drawVertexInformType();

        //
        if (!partitionPolygon())
            return false;

        //
        // drawBordersOfSubMonotones();
        // drawVertexInformDiverge();

        drawBordersByMap();

        //
        if (!createSubMonotone())
            return false;

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
        pToOrigin.y += 20;
        as.convertToStandard(pToOrigin);
        mutableVisitorForDebugging.drawTip(txt, pToOrigin);
    }

    public void drawTextEdge(Edge e) {
        final Point pCenter = new Point();
        pCenter.x = (e.a.x + e.b.x) / 2;
        pCenter.y = (e.a.y + e.b.y) / 2;
        drawTextTipPosDecal(Integer.toHexString(System.identityHashCode(e)), pCenter, -1);
    }

    public void drawTextTipPosDecal(String txt, Point p, int pos) {
        if (mutableVisitorForDebugging == null)
            return;
        final Point pToOrigin = new Point();
        pToOrigin.set(p);
        pToOrigin.y += 20 + pos * 15;
        as.convertToStandard(pToOrigin);
        mutableVisitorForDebugging.drawTip(txt, pToOrigin);
    }

    public void drawPolygon(Polygon p) {
        if (mutableVisitorForDebugging == null)
            return;
        mutableVisitorForDebugging.getGraphicsContext().save();
        mutableVisitorForDebugging.getGraphicsContext().setLineWidth(2);
        mutableVisitorForDebugging.getGraphicsContext().setStroke(Color.HOTPINK);
        mutableVisitorForDebugging.visit(p);
        mutableVisitorForDebugging.getGraphicsContext().restore();

    }

    public void drawAngle(String txt, Point p) {
        if (mutableVisitorForDebugging == null)
            return;
        final Point pToOrigin = new Point();
        pToOrigin.set(p);
        pToOrigin.y -= 20;
        as.convertToStandard(pToOrigin);
        mutableVisitorForDebugging.drawTip(txt, pToOrigin);
    }

    public void drawBordersBySegment() {
        if (mutableVisitorForDebugging == null)
            return;

        mutableVisitorForDebugging.getGraphicsContext().save();
        mutableVisitorForDebugging.getGraphicsContext().setLineWidth(5);
        mutableVisitorForDebugging.getGraphicsContext().setStroke(Color.RED);

        final Segment sToOrigin = new Segment();
        for (Segment s : bordersBySegment) {
            sToOrigin.set(s);
            as.convertToStandard(sToOrigin.a);
            as.convertToStandard(sToOrigin.b);
            mutableVisitorForDebugging.visit_unit(sToOrigin);
        }
        mutableVisitorForDebugging.getGraphicsContext().restore();
    }

    public void drawBordersByMap() {
        if (mutableVisitorForDebugging == null)
            return;

        mutableVisitorForDebugging.getGraphicsContext().save();
        mutableVisitorForDebugging.getGraphicsContext().setStroke(Color.RED);

        final Segment sToOrigin = new Segment();

        for (final Entry<VertexInform, HashSet<VertexInform>> entry : bordersMap.entrySet()) {
            sToOrigin.a.set(entry.getKey());
            as.convertToStandard(sToOrigin.a);

            System.out.print(entry.getKey() + " : ");

            for (final VertexInform b : entry.getValue()) {
                sToOrigin.b.set(b);
                as.convertToStandard(sToOrigin.b);

                System.out.print(b + " ");

                mutableVisitorForDebugging.getGraphicsContext().setLineWidth(5);
                mutableVisitorForDebugging.visit_unit(sToOrigin);

                mutableVisitorForDebugging.getGraphicsContext().setLineWidth(1);
                drawDivergeTip(b);
            }

            System.out.println();

            drawDivergeTip(entry.getKey());
        }

        System.out.println("--");
        mutableVisitorForDebugging.getGraphicsContext().restore();
    }

    public void drawBorder(Segment s) {
        if (mutableVisitorForDebugging == null)
            return;

        mutableVisitorForDebugging.getGraphicsContext().save();
        mutableVisitorForDebugging.getGraphicsContext().setLineWidth(5);
        mutableVisitorForDebugging.getGraphicsContext().setStroke(Color.RED);
        final Segment sToOrigin = new Segment();
        sToOrigin.set(s);
        as.convertToStandard(sToOrigin.a);
        as.convertToStandard(sToOrigin.b);
        mutableVisitorForDebugging.visit_unit(sToOrigin);
        mutableVisitorForDebugging.getGraphicsContext().restore();

        drawDivergeTip((VertexInform) s.a);
        drawDivergeTip((VertexInform) s.b);
    }

    public void drawDivergeTip(VertexInform vi) {
        if (vi.divergeTo != null)
            drawTextTipPosDecal("├ ", vi.divergeTo, 1);
        if (vi.divergeFrom != null)
            drawTextTipPosDecal(" ┤", vi.divergeFrom, 1);
    }

    public void drawVertexInformDiverge() {
        if (mutableVisitorForDebugging == null)
            return;

        for (VertexInform vi : verticesInform) {

            if (vi.divergeTo != null)
                drawTextTipPosDecal("├ ", vi.divergeTo, 1);
            if (vi.divergeFrom != null)
                drawTextTipPosDecal(" ┤", vi.divergeFrom, 1);

        }
    }

    public void drawVertexInformType() {
        if (mutableVisitorForDebugging == null)
            return;

        int counter = 0;

        for (VertexInform vi : verticesInform) {

            final Point pToOrigin = new Point();
            pToOrigin.set(vi);
            as.convertToStandard(pToOrigin);

            String type = "☺";

            switch (vi.type) {
            case End:
                type = "■";
                break;
            case Merge:
                type = "▼";
                break;
            case RegularLeft:
                type = "←●";
                break;
            case RegularRight:
                type = "●→";
                break;
            case Split:
                type = "▲";
                break;
            case Start:
                type = "□";
                break;
            }

            pToOrigin.y -= 5;

            mutableVisitorForDebugging.drawTip(type, pToOrigin);

            /*
             * if (vi.divergeTo != null) drawTextTipPosDecal("├ ", vi.divergeTo,
             * 1); if (vi.divergeFrom != null) drawTextTipPosDecal(" ┤",
             * vi.divergeFrom, 1);
             */

            pToOrigin.y -= 10;
            mutableVisitorForDebugging.drawTip(vi.toString(), pToOrigin);
            pToOrigin.y -= 10;
            mutableVisitorForDebugging.drawTip(String.valueOf(counter++), pToOrigin);

            /*
             * drawTextTipPosDecal( //
             * 
             * Integer.toHexString(System.identityHashCode(vi.back)) + "->" + //
             * Integer.toHexString(System.identityHashCode(vi)) + "->" + //
             * Integer.toHexString(System.identityHashCode(vi.next)) , vi, 4);
             */

        }
    }

    public void drawStatusTip(int l) {
        int counter = 0;
        final Point pCenter = new Point();

        for (Edge e : status) {

            pCenter.x = (e.a.x + e.b.x) / 2 + (l % 2) * 30;
            pCenter.y = (e.a.y + e.b.y) / 2 + (l / 2) * 12;
            drawTextTip("(" + l + "," + counter + ")", pCenter);

            /*
             * switch (e.type) { case Upper: drawTextTip("Up(" + counterU + ")("
             * + counter + ")[" + e.p.toString() + "]", e.p); counterU++; break;
             * case Lower: drawTextTip("Low(" + counterL + ")(" + counter + ")["
             * + e.p.toString() + "]", e.p); counterL++; break; }
             */
            counter++;
        }
    }

    public void drawStatusMinus() {
        final Point pCenter = new Point();
        for (Edge e : status) {
            pCenter.x = (e.a.x + e.b.x) / 2;
            pCenter.y = (e.a.y + e.b.y) / 2;
            drawTextTip("--", pCenter);
        }
    }

};

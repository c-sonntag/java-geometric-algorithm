package at.u4a.geometric_algorithms.algorithm;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
        public boolean canApply(AbstractList<AbstractLayer> layers) {
            if(layers.size() != 1)
                return false;
            //
            AbstractShape as = layers.get(0).getShape();
            return (as instanceof Polygon);
        }

        static int MonotisationCount = 1;

        @Override
        public AbstractLayer builder(AbstractList<AbstractLayer> layers) {

            if(layers.isEmpty())
                throw new RuntimeException("Need one layer !");
            
            //
            AbstractShape as = layers.get(0).getShape();
            if (!(as instanceof Polygon))
                throw new RuntimeException("Monotisation need a Polygon Shape !");

            //
            Polygon poly = (Polygon) as;

            //
            AbstractLayer al = new AlgorithmLayer<Monotisation>(new Monotisation(poly.perimeter, poly), Algorithm.Monotisation, layers);
            al.setLayerName("m" + String.valueOf(MonotisationCount));
            MonotisationCount++;
            return al;
        }

    };

    private final AbstractList<Point> points;
    private final Polygon poly;

    private final Vector<MonotonePolygon> mp_v;
    private final SegmentIntersection intersectionTester;

    public Monotisation(Vector<Point> points, Polygon poly) {
        this.points = points;
        this.poly = poly;
        this.mp_v = new Vector<MonotonePolygon>();
        this.intersectionTester = new SegmentIntersection(poly, poly, true);
    }

    /* ************** */

    public void accept(AbstractList<AbstractLayer> subLayers, InterfaceGraphicVisitor visitor) {

        makeMonotisation();
        if (haveMake) {
            final Segment sToOrigin = new Segment();
            for (Segment s : bordersBySegment) {
                sToOrigin.set(s);
                poly.convertToStandard(sToOrigin.a);
                poly.convertToStandard(sToOrigin.b);
                visitor.visit_unit(sToOrigin);
            }
        }
    }

    private InterfaceGraphicVisitor mutableVisitorForDebugging = null;

    public void accept_3(AbstractList<AbstractLayer> subLayers, InterfaceGraphicVisitor visitor) {

        mutableVisitorForDebugging = visitor;

        makeMonotisation();

        if (haveMake) {

            drawVertexInformType();

            final Segment sToOrigin = new Segment();
            for (Segment s : bordersBySegment) {
                sToOrigin.set(s);
                poly.convertToStandard(sToOrigin.a);
                poly.convertToStandard(sToOrigin.b);
                visitor.visit_unit(sToOrigin);
            }
        }
    }

    public void acceptDebug(AbstractList<AbstractLayer> subLayers, InterfaceGraphicVisitor visitor) {

        int countStroboscope = 0;
        Color colorStrop[] = { Color.CORAL, Color.BLUEVIOLET, Color.MEDIUMSPRINGGREEN, Color.HOTPINK, Color.YELLOWGREEN, Color.FIREBRICK, Color.GREEN };

        mutableVisitorForDebugging = visitor;

        makeMonotisation();
        drawVertexInformType();

        if (haveMake) {

            final Segment sToOrigin = new Segment();
            for (Segment s : bordersBySegment) {
                sToOrigin.set(s);
                poly.convertToStandard(sToOrigin.a);
                poly.convertToStandard(sToOrigin.b);
                visitor.visit_unit(sToOrigin);
            }

            visitor.getGraphicsContext().save();
            visitor.getGraphicsContext().setLineWidth(10);
            for (MonotonePolygon mp : mp_v) {
                Color sC = colorStrop[countStroboscope];
                visitor.getGraphicsContext().setStroke(Color.color(sC.getRed(), sC.getGreen(), sC.getBlue(), 0.2));
                visitor.visit(mp);
                countStroboscope = (countStroboscope + 1) % colorStrop.length;
            }
            System.out.println("NbPolygon(" + mp_v.size() + ")");
            visitor.getGraphicsContext().restore();

        }
    }

    /* ************** */

    @Override
    public int hashCode() {
        return poly.hashCode();
    }

    /* ************** */

    public boolean haveMadeSubMonotonePolyon() {
        makeMonotisation();
        return haveMake;
    }

    public Vector<MonotonePolygon> getMonotonesPolygon() {
        makeMonotisation();

        if (bordersBySegment.isEmpty() && haveMake) {
            Vector<MonotonePolygon> mp_v_once = new Vector<MonotonePolygon>();
            mp_v_once.add(new MonotonePolygon(poly.origin, (Vector<Point>) points));
            return mp_v_once;
        }

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

        int currentPolyHash = poly.hashCode();
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

    private static class StatusComparator implements Comparator<Segment> {

        @Override
        public int compare(Segment s1, Segment s2) {

            if (s1.a.equals(s2.a) && s1.b.equals(s2.b))
                return 0;

            final double s1xMax = Math.max(s1.a.x, s1.b.x);
            final double s2xMax = Math.max(s2.a.x, s2.b.x);

            final double s1xMin = Math.min(s1.a.x, s1.b.x);
            final double s2xMin = Math.min(s2.a.x, s2.b.x);

            if ((s1xMin < s2xMin) && (s1xMax < s2xMax))
                return -1;

            if ((s1xMin == s2xMin) && (s1xMax == s2xMax))
                return ((Math.max(s1.a.y, s1.b.y) < Math.max(s2.a.y, s2.b.y)) ? -1 : 1);

            return +1;
        }
    };

    /* ************** */

    private final Set<Edge> status = new TreeSet<Edge>(new StatusComparator());
    private final NavigableSet<Edge> statusNavigator = (NavigableSet<Edge>) status;

    private final Set<VertexInform> verticesInform = new TreeSet<VertexInform>(new Point.PointUpLeftAlignementComparator());

    private VertexInform firstVertexInform = null;

    private final Vector<Segment> bordersBySegment = new Vector<Segment>();
    private final Map<VertexInform, HashSet<VertexInform>> bordersByMap = new HashMap<VertexInform, HashSet<VertexInform>>();

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

        public Edge nextEdge = null;

        public VertexInform(Point p) {
            super(p);
        }

        public Edge getNextEdge() {
            if (nextEdge == null)
                nextEdge = new Edge(this, next);
            return nextEdge;
        }
    };

    /* ************** */

    class NotDirectLeftFindException extends Exception {
        private static final long serialVersionUID = -1665815895025535555L;
    }

    private void attachBorder(VertexInform vi1, VertexInform vi2) {

        //
        bordersBySegment.add(new Segment(vi1, vi2));

        //
        HashSet<VertexInform> bm1 = bordersByMap.get(vi1);
        if (bm1 == null) {
            bm1 = new HashSet<VertexInform>();
            bordersByMap.put(vi1, bm1);
        }
        bm1.add(vi2);

        //
        HashSet<VertexInform> bm2 = bordersByMap.get(vi2);
        if (bm2 == null) {
            bm2 = new HashSet<VertexInform>();
            bordersByMap.put(vi2, bm2);
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

            /*
             * final Edge eDirectRightOfVi = statusNavigator.higher(viSearch);
             * System.out.println("Not find eDirectLeft Of vi(" + vi.toString()
             * + "), eDirectRightOfVi(" + eDirectRightOfVi + ") sc(" +
             * sc.compare(viSearch, eDirectRightOfVi) + ")"); final Edge
             * eDirectLeftOfViBis = statusNavigator.floor(viSearch);
             */

            statusAddCause("Can't find direct left segment");
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
        else
            System.out.println("Normaly need a helper for eDirectLeftOfVi("+eDirectLeftOfVi.toString()+")");
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
            eDirectLeftOfVi.helper = vi;
        }
    }

    /* ************** */

    private boolean partitionPolygon() {

        bordersBySegment.clear();
        bordersByMap.clear();
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
                // drawStatusMinus();

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
        if (sumVecZ == 0) {
            statusAddCause("Unable to find sens of polygon");
            return false;
        }

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
        final MonotonePolygon mp = new MonotonePolygon(poly.origin, new Vector<Point>());
        mp_v.add(mp);
        return mp;
    }

    class CourseEntry {
        final MonotonePolygon mp;
        final VertexInform start;

        public CourseEntry(VertexInform start) {
            this.mp = createEmptyMonotonePolygon();
            this.start = start;
        }

        public void add(VertexInform vi) {
            mp.addPoint(vi);
        }
    }

    private boolean createSubMonotone() {
        mp_v.clear();
        if (firstVertexInform == null)
            return false;

        ArrayDeque<CourseEntry> queue = new ArrayDeque<CourseEntry>();

        //
        CourseEntry curentCourse = new CourseEntry(null);
        VertexInform vi = firstVertexInform;
        VertexInform viToStopAndCloseMp = firstVertexInform.back;

        // int counter = 0;

        //
        while (true) {
            curentCourse.add(vi);

            // drawTextTipPosDecal(String.valueOf(counter++), vi, -3);

            //
            final HashSet<VertexInform> borders = bordersByMap.get(vi);
            if (borders != null) {

                // System.out.print(" (" + queue.size() + ") ");
                boolean haveRemove;

                //
                do {
                    //
                    final VertexInform oppositeBorder = curentCourse.start;
                    haveRemove = false;

                    //
                    /*
                     * if (oppositeBorder != null) { System.out.print("[start:"
                     * + oppositeBorder.toString() + "] "); }
                     */

                    //
                    if (borders.contains(oppositeBorder)) {

                        //
                        // System.out.print(vi.toString() + "|- ");
                        borders.remove(oppositeBorder);

                        //
                        curentCourse = queue.pop();
                        curentCourse.add(vi);
                        haveRemove = true;
                    }

                } while (haveRemove);

                //
                final int bordersSize = borders.size();
                for (int i = 0; i < bordersSize; i++) {
                    // System.out.print(vi.toString() + "|+ ");
                    queue.push(curentCourse);
                    curentCourse = new CourseEntry(vi);
                    curentCourse.add(vi);
                }
            }

            //
            if (vi == viToStopAndCloseMp)
                break;
            else
                vi = vi.next;
        }

        // curentCourse.mp.addPoint(vi);

        // drawTextTipPosDecal(String.valueOf(counter++), vi, -3);

        // System.out.println();

        // System.out.println("NbPolygon(" + mp_v.size() + ")");

        /*
         * // try { courseVertices(createEmptyMonotonePolygon(),
         * firstVertexInform, firstVertexInform.back); return true; } catch
         * (NotComputeVecZException useless) { return false; }
         */

        return true;
    }

    /* ************** */

    private boolean buildMonotisation() {

        //
        if (intersectionTester.haveIntersections()) {
            statusAddCause("Polygon have intersection");
            return false;
        }

        //
        if (!createVertexInform())
            return false;

        //
        // drawVertexInformType();

        //
        if (!partitionPolygon())
            return false;

        //
        // drawBordersOfSubMonotones();
        // drawVertexInformDiverge();
        // drawBordersByMap();

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
        poly.convertToStandard(pToOrigin);
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
        poly.convertToStandard(pToOrigin);
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
        poly.convertToStandard(pToOrigin);
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
            poly.convertToStandard(sToOrigin.a);
            poly.convertToStandard(sToOrigin.b);
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

        for (final Entry<VertexInform, HashSet<VertexInform>> entry : bordersByMap.entrySet()) {
            sToOrigin.a.set(entry.getKey());
            poly.convertToStandard(sToOrigin.a);

            System.out.print(entry.getKey() + " : ");

            for (final VertexInform b : entry.getValue()) {
                sToOrigin.b.set(b);
                poly.convertToStandard(sToOrigin.b);

                System.out.print(b + " ");
                mutableVisitorForDebugging.getGraphicsContext().setLineWidth(5);
                mutableVisitorForDebugging.visit_unit(sToOrigin);
                mutableVisitorForDebugging.getGraphicsContext().setLineWidth(1);
            }

            // System.out.println();
        }

        // System.out.println("--");
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
        poly.convertToStandard(sToOrigin.a);
        poly.convertToStandard(sToOrigin.b);
        mutableVisitorForDebugging.visit_unit(sToOrigin);
        mutableVisitorForDebugging.getGraphicsContext().restore();
    }

    public void drawVertexInformType() {
        if (mutableVisitorForDebugging == null)
            return;

        // int counter = 0;

        for (VertexInform vi : verticesInform) {

            final Point pToOrigin = new Point();
            pToOrigin.set(vi);
            poly.convertToStandard(pToOrigin);

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

            /*
             * pToOrigin.y -= 10;
             * mutableVisitorForDebugging.drawTip(String.valueOf(counter++),
             * pToOrigin);
             */

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

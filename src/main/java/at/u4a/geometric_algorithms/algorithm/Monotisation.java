package at.u4a.geometric_algorithms.algorithm;

import java.util.AbstractList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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

    @Override
    public void accept(Vector<AbstractLayer> v, InterfaceGraphicVisitor visitor) {

        mutableVisitorForDebugging = visitor;

        makeMonotisation();
        if (haveMake) {
            for (MonotonePolygon mp : mp_v) {
                visitor.visit(mp);
            }
        }
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

    private static class OldStatusComparator implements Comparator<Segment> {
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

    private static class StatusComparator implements Comparator<Segment> {
        @Override
        public int compare(Segment s1, Segment s2) {
            if (s1.a.equals(s2.a) && s2.b.equals(s2.b))
                return 0;

            final double wS1Decal = (s1.a.x + s1.b.x) / 2, wS2Decal = (s2.a.x + s2.b.x) / 2;
            final double hS1Decal = (s1.a.y + s1.b.y) / 2, hS2Decal = (s2.a.y + s2.b.y) / 2;

            final double hS1 = Math.abs(s1.a.y - s1.b.y), hS2 = Math.abs(s2.a.y - s2.b.y);
            final double s1Top = Math.max(s1.a.y, s1.b.y), s1Bottom = Math.max(s1.a.y, s1.b.y);
            final double s2Top = Math.max(s2.a.y, s2.b.y), s2Bottom = Math.max(s2.a.y, s2.b.y);

            final boolean s2isGreaterRight = wS1Decal < wS2Decal;
            final boolean s2isInHeightRange = (hS2 <= hS1) && (s1Top <= s2Top) && (s1Bottom >= s2Bottom);

            if (s2isGreaterRight) {
                return s2isInHeightRange ? 1 : -1;
            } else {
                return 1;
            }
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

    private final Vector<Segment> bordersOfSubMonotones = new Vector<Segment>();

    /* ************** */

    static enum VertexType {
        Unknown, Start, End, Split, Merge, RegularLeft, RegularRight,
    };

    static enum VertexType_1 {
        Unknown/* *****/(0b0000000), //
        Start/* *******/(0b0000001), //
        End/* *********/(0b0000010), //
        Split/* *******/(0b0000100), //
        Merge/* *******/(0b0001000), //
        Regular/* *****/(0b0010000), //
        RegularLeft/* */(0b0110000), //
        RegularRight/**/(0b1010000); //

        final int v;

        VertexType_1(int v) {
            this.v = v;
        }
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

    };

    /* ************** */

    private void attachBorder(VertexInform vi1, VertexInform vi2) {
        vi1.divergeTo = vi2;
        vi2.divergeFrom = vi1;
        bordersOfSubMonotones.add(new Segment(vi1, vi2));
    }

    /* ************** */

    private void handleStartVertex(VertexInform vi) {
        final Edge e = vi.getNextEdge();
        e.helper = vi;
        status.add(e);
    }

    private void handleEndVertex(VertexInform vi) {
        final Edge eBack = vi.back.getNextEdge();
        if (eBack.helper != null)
            if (eBack.helper.type == VertexType.Merge)
                attachBorder(vi, eBack.helper);
        status.remove(eBack);
    }

    private void handleSplitVertex(VertexInform vi) {
        final Edge viSearch = new Edge(vi, vi);
        /** @todo check it */
        final Edge eDirectLeftOfVi = statusNavigator.lower(viSearch);
        if (eDirectLeftOfVi != null) {
            if (eDirectLeftOfVi.helper != null) {
                attachBorder(vi, eDirectLeftOfVi.helper);
                eDirectLeftOfVi.helper = vi;
            }
        }

        //
        final Edge e = vi.getNextEdge();
        e.helper = vi;
        status.add(e);
    }

    private void handleMergeVertex(VertexInform vi) {
        final Edge eBack = vi.back.getNextEdge();
        if (eBack.helper != null)
            if (eBack.helper.type == VertexType.Merge)
                attachBorder(vi, eBack.helper);
        status.remove(eBack);

        //
        final Edge viSearch = new Edge(vi, vi);
        /** @todo check it */
        final Edge eDirectLeftOfVi = statusNavigator.lower(viSearch);
        if (eDirectLeftOfVi != null) {
            if (eDirectLeftOfVi.helper != null) {
                if (eDirectLeftOfVi.helper.type == VertexType.Merge)
                    attachBorder(vi, eDirectLeftOfVi.helper);
            }
            eDirectLeftOfVi.helper = vi;
        }
    }

    private void handleRegularVertex(VertexInform vi) {
        if (vi.type == VertexType.RegularLeft) {
            //
            final Edge eBack = vi.back.getNextEdge();
            if (eBack.helper != null)
                if (eBack.helper.type == VertexType.Merge)
                    attachBorder(vi, eBack.helper);
            //
            status.remove(eBack);
            final Edge e = vi.getNextEdge();
            e.helper = vi;
            status.add(e);
        } else {

            final Edge viSearch = new Edge(vi, vi);
            /** @todo check it */
            final Edge eDirectLeftOfVi = statusNavigator.lower(viSearch);
            if (eDirectLeftOfVi != null) {
                if (eDirectLeftOfVi.helper != null) {
                    if (eDirectLeftOfVi.helper.type == VertexType.Merge)
                        attachBorder(vi, eDirectLeftOfVi.helper);
                }
                eDirectLeftOfVi.helper = vi;
            }
        }
    }

    /* ************** */

    private boolean partitionPolygon() {

        bordersOfSubMonotones.clear();
        status.clear();

        int count = 0;

        //
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

            //drawStatusTip(count++);
        }
        return true;
    }

    /* ************** */

    private void markTypeOfVertexInform(VertexInform vi) {

        final double produit = Calc.resumeProduitVectorielZ(vi.back, vi, vi.next);

        final boolean haveUpperNeighbour = (vi.y > vi.back.y) && (vi.y > vi.next.y);
        final boolean haveLowerNeighbour = (vi.y < vi.back.y) && (vi.y < vi.next.y);

        final boolean isLesserThanPi = produit > 0;

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

        //
        final Iterator<Point> p_it = sumVecZ > 0 ? points.iterator() : new Collection.ReverseIterable<Point>(points).iterator();

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
            // vertices.add(newVI);
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
        // vertices.add(firstVI);

        firstVertexInform = firstVI;

        //
        /** @todo find if the polygon have colision */
        return true;
    }

    /* ************** */

    private final MonotonePolygon createEmptyMonotonePolygon() {
        final MonotonePolygon mp = new MonotonePolygon(as.origin, new Vector<Point>());
        mp_v.add(mp);
        return mp;
    }

    private int mutableDebugLotsLoop = 0;
    
    private boolean courseVertices(final MonotonePolygon mp, final VertexInform viStart, final VertexInform viToStopAndCloseMp) {
        VertexInform vi = viStart;

        while (vi != viToStopAndCloseMp) {
            
            if(mutableDebugLotsLoop > 300)
                break; 
            
            mutableDebugLotsLoop++;
            
            statusAddCounter();
            mp.addPoint(vi);
            
            if (vi.divergeTo != null) {
                
                drawTextTip("100", vi.back);
                drawTextTip("000", vi);
                drawTextTip("001", vi.next);
                drawTextTip("♦", vi.divergeTo);
                
                final MonotonePolygon subMp = createEmptyMonotonePolygon();
                subMp.addPoint(vi);
                courseVertices(subMp, vi.next, vi.divergeTo);
                //
                //mp.addPoint(vi.divergeTo);
                vi = vi.divergeTo;
            } else {
                vi = vi.next;
            }
        }
        mp.addPoint(vi);

        drawPolygon(mp);

        // sumOfVectorialZProd += resumeProduitVectorielZ(pA, pO, pB);

        return true;
    }

    private boolean createSubMonotone() {
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

        return courseVertices(createEmptyMonotonePolygon(), firstVertexInform, firstVertexInform.back);
    }

    /* ************** */

    private boolean buildMonotisation() {

        //
        if (!createVertexInform())
            return false;

        //
        drawVertexInformType();

        //
        if (!partitionPolygon())
            return false;

        //
        drawBordersOfSubMonotones();

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
    
    public void drawTextTipPosDecal(String txt, Point p, int pos) {
        if (mutableVisitorForDebugging == null)
            return;
        final Point pToOrigin = new Point();
        pToOrigin.set(p);
        pToOrigin.y += 20 + pos * 20;
        as.convertToStandard(pToOrigin);
        mutableVisitorForDebugging.drawTip(txt, pToOrigin);
    }

    public void drawPolygon(Polygon p) {
        if (mutableVisitorForDebugging == null)
            return;
        mutableVisitorForDebugging.getGraphicsContext().save();
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

    public void drawBordersOfSubMonotones() {
        if (mutableVisitorForDebugging == null)
            return;

        mutableVisitorForDebugging.getGraphicsContext().save();
        mutableVisitorForDebugging.getGraphicsContext().setLineWidth(5);
        mutableVisitorForDebugging.getGraphicsContext().setStroke(Color.RED);

        final Segment sToOrigin = new Segment();

        for (Segment s : bordersOfSubMonotones) {
            sToOrigin.set(s);
            as.convertToStandard(sToOrigin.a);
            as.convertToStandard(sToOrigin.b);
            mutableVisitorForDebugging.visit_unit(sToOrigin);
        }

        mutableVisitorForDebugging.getGraphicsContext().restore();
    }

    public void drawVertexInformType() {
        if (mutableVisitorForDebugging == null)
            return;

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

};

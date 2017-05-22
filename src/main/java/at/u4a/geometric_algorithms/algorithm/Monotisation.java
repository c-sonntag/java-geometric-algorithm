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

    private static class StatusComparator implements Comparator<Segment> {
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

    /*
     * private static class VertexInformComparator implements
     * Comparator<VertexInform> { private Comparator<Point> comparator = new
     * Point.PointUpLeftAlignementComparator();
     * 
     * public int compare(VertexInform vi1, VertexInform vi2) { return
     * comparator.compare(vi1, vi2); } };
     */

    /* ************** */

    private final Set<Segment> status = new TreeSet<Segment>(new StatusComparator());
    private final NavigableSet<Segment> statusNavigator = (NavigableSet<Segment>) status;

    // private final PriorityQueue<VertexInform> intersectionsQueue = new
    // PriorityQueue<VertexInform>();
    // private final PriorityQueue<VertexInform> intersectionsQueue = new
    // PriorityQueue<VertexInform>();

    private final Set<VertexInform> vertexInform = new TreeSet<VertexInform>(new Point.PointUpLeftAlignementComparator());

    /* ************** */

    static enum VertexType {
        Unknown, Start, End, Regular, Split, Merge
    };

    class VertexInform extends Point {
        public VertexType type = VertexType.Unknown;
        public VertexInform back = null, next = null;

        public VertexInform(Point p) {
            super(p);
        }

    };

    /* ************** */

    private void handleStartVertex(Point v) {

    }

    private boolean partitionPolygon() {

        for (VertexInform p : vertexInform) {
            switch (p.type) {
            case End:
                break;
            case Merge:
                break;
            case Regular:
                break;
            case Split:
                break;
            case Start:
                break;
            }
        }
        return true;
    }

    /* ************** */

    private void markTypeOfVertexInform(VertexInform vi) {
        //
        /*
         * final boolean isVerticalUpDown = ((vi.back.y < vi.y) && (vi.y <
         * vi.next.y)); final boolean isVerticalDownUp = ((vi.back.y > vi.y) &&
         * (vi.y > vi.next.y)); final boolean isHorizontalLeftRight =
         * ((vi.back.x < vi.x) && (vi.x < vi.next.x)); final boolean
         * isHorizontalRightLeft = ((vi.back.x > vi.x) && (vi.x > vi.next.x));
         */

        /*
         * final boolean isVerticalUpDown = ((vi.back.y < vi.y) && (vi.y <
         * vi.next.y)); final boolean isVerticalDownUp = ((vi.back.y > vi.y) &&
         * (vi.y > vi.next.y)); final boolean isHorizontalLeftRight = (vi.back.x
         * < vi.next.x); final boolean isHorizontalRightLeft = (vi.back.x >
         * vi.next.x);
         */

        /*
         * final boolean normalSens = isVerticalUpDown || isHorizontalLeftRight;
         * final VertexInform leftOrUp = (normalSens) ? vi.back : vi.next; final
         * VertexInform rightOrDown = (normalSens) ? vi.next : vi.back;
         */

        /*
         * final VertexInform leftOrUp = (isVerticalUpDown ||
         * isHorizontalLeftRight) ? vi.back : vi.next; final VertexInform
         * rightOrDown = (isVerticalDownUp || isHorizontalRightLeft) ? vi.next :
         * vi.back;
         */

        final double produit = Calc.resumeProduitVectorielZ(vi.back, vi, vi.next);

        final boolean haveUpperNeighbour = (vi.y > vi.back.y) && (vi.y > vi.next.y);
        final boolean haveLowerNeighbour = (vi.y < vi.back.y) && (vi.y < vi.next.y);

        final boolean isLesserThanPi = produit > 0;

        if (haveLowerNeighbour)
            vi.type = isLesserThanPi ? VertexType.Start : VertexType.Split;
        else if (haveUpperNeighbour)
            vi.type = isLesserThanPi ? VertexType.End : VertexType.Merge;
        else
            vi.type = VertexType.Regular;

        drawAngle(String.valueOf(produit) + " " + (isLesserThanPi ? "1" : "0"), vi);
        // drawAngle(String.valueOf(produit) + " " + (isLesserThanPi ? "1" :
        // "0")+ " " + (normalSens ? "→" : "←"), vi);

        /*
         * if ((vi.y < left.y) && (vi.y < right.y)) // is : Start or Split
         * vi.type = isLesserThanPi ? VertexType.Start : VertexType.Split; else
         * if ((vi.y > left.y) && (vi.y > right.y)) // is : End or Merge vi.type
         * = isLesserThanPi ? VertexType.End : VertexType.Merge; else vi.type =
         * VertexType.Regular;
         * 
         * 
         * 
         * if(isVerticalUpDown || isVerticalDownUp) { // 1 voisin au-dessu et
         * l'autre en-dessous } else if (isHorizontalLeftRight ||
         * isHorizontalRightLeft) { // 2 voisin au-dessu ou en-dessous
         * 
         * } drawAngle(String.valueOf(produit) + " " + (isLesserThanPi ? "1" :
         * "0"), vi);
         * 
         * /**
         * 
         * @todo find bug with resumeProduitVectorielZ /** @todo find the
         * ambiguity with (Start or Split) and (End or Merge)
         */

        //
        /*
         * if ((vi.y < left.y) && (vi.y < right.y)) // is : Start or Split
         * vi.type = isLesserThanPi ? VertexType.Start : VertexType.Split; else
         * if ((vi.y > left.y) && (vi.y > right.y)) // is : End or Merge vi.type
         * = isLesserThanPi ? VertexType.End : VertexType.Merge; else vi.type =
         * VertexType.Regular;
         * 
         * // /* if ((vi.y < left.y) && (vi.y < right.y)) // is : Start or Split
         * vi.type = VertexType.Split; else if ((vi.y > left.y) && (vi.y >
         * right.y)) // is : End or Merge vi.type = VertexType.Merge; else
         * vi.type = VertexType.Regular;
         */

    }

    private boolean createVertexInform() {

        if (points.size() < 3)
            return false;

        vertexInform.clear();

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
            newVI = new VertexInform(p_it.next());
            newVI.back = lastVI;
            lastVI.next = newVI;

            //
            if (lastVI.back != null) {
                markTypeOfVertexInform(lastVI);
            }

            //
            vertexInform.add(newVI);
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
        vertexInform.add(firstVI);

        //
        /** @todo find if the polygon have colision */
        return true;
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

    public void drawAngle(String txt, Point p) {
        if (mutableVisitorForDebugging == null)
            return;
        final Point pToOrigin = new Point();
        pToOrigin.set(p);
        pToOrigin.y -= 20;
        as.convertToStandard(pToOrigin);
        mutableVisitorForDebugging.drawTip(txt, pToOrigin);
    }

    public void drawVertexInformType() {
        if (mutableVisitorForDebugging == null)
            return;

        for (VertexInform vi : vertexInform) {

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
            case Regular:
                type = "●";
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

};

package at.u4a.geometric_algorithms.algorithm;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import at.u4a.geometric_algorithms.algorithm.InterfaceAlgorithmBuilder;
import at.u4a.geometric_algorithms.algorithm.Monotisation.VertexInform;
import at.u4a.geometric_algorithms.algorithm.Triangulation.PointTipped;
import at.u4a.geometric_algorithms.algorithm.Triangulation.PointTipped.Tip;
import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Polygon;
import at.u4a.geometric_algorithms.geometric.Polygon.MonotonePolygon;
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
 *
 */
public class Triangulation extends AbstractAlgorithm {

    public static class Builder implements InterfaceAlgorithmBuilder {

        @Override
        public String getName() {
            return Triangulation.class.getSimpleName();
        }

        @Override
        public boolean canApply(AbstractLayer l) {
            return (l.getShape() instanceof Polygon);
        }

        static int TriangulationCount = 1;

        @Override
        public AbstractLayer builder(AbstractLayer l) {

            //
            AbstractShape as = l.getShape();
            if (!(as instanceof Polygon))
                throw new RuntimeException("Triangulation need a Polygon Shape !");

            //
            Polygon poly = (Polygon) as;

            //
            AbstractLayer al = new AlgorithmLayer<Triangulation>(new Triangulation(poly.perimeter, poly), Algorithm.Triangulation, l);
            al.setLayerName("t" + String.valueOf(TriangulationCount));
            TriangulationCount++;
            return al;
        }

    };

    private final AbstractList<Point> points;
    private final Polygon poly;
    private final MonotonePolygon mp;

    private final SegmentIntersection intersectionTester;

    public Triangulation(Vector<Point> points, Polygon poly) {
        this(points, poly, true);
    }

    public Triangulation(Vector<Point> points, Polygon poly, boolean checkIntersection) {
        this.points = points;
        this.poly = poly;

        this.mp = new MonotonePolygon(poly.origin, points);
        intersectionTester = checkIntersection ? new SegmentIntersection(poly, poly, true) : null;
    }

    /* ************** */

    InterfaceGraphicVisitor mutableVisitorForDebugging = null;

    @Override
    public void accept(Vector<AbstractLayer> v, InterfaceGraphicVisitor visitor) {

        //mutableVisitorForDebugging = visitor;

        makeTriangulation();

        //
        if (actualType == Polygon.Type.Monotone) {

            //
            final Segment sToOrigin = new Segment();
            for (Segment tf : triangulationFusion) {
                sToOrigin.set(tf);
                poly.convertToStandard(sToOrigin.a);
                poly.convertToStandard(sToOrigin.b);
                visitor.visit_unit(sToOrigin);
            }

        }
    }

    @Override
    public int hashCode() {
        return poly.hashCode();
    }

    /* ************** */

    public boolean isMonotone() {
        makeTriangulation();
        return actualType == Polygon.Type.Monotone;
    }

    public MonotonePolygon getPolygon() {
        makeTriangulation();
        if (actualType == Polygon.Type.Monotone)
            return mp;
        else
            return null;
    }

    /* ************** */

    @Override
    public AbstractShape getCompositeShape() {
        return null;
    }

    /* ************** */

    private int mutablePreviousPolyHash = 0;

    protected void makeTriangulation() {

        int currentPolyHash = poly.hashCode();
        if (currentPolyHash != mutablePreviousPolyHash) {

            //
            statusStartBuild();

            //
            actualType = Polygon.Type.Simple;
            if (buildTriangulation()) {
                actualType = Polygon.Type.Monotone;
                statusFinishBuild();
            } else
                statusInteruptBuild();

            //
            mutablePreviousPolyHash = currentPolyHash;
        }

    }

    /* ************** */

    /**
     * Index du point le plus haut et le plus bas dans la listes des points
     */
    int topPointIndex, bottomPointIndex;
    Point topPoint, bottomPoint;

    /**
     * La liste des points affiché.
     */
    private Vector<PointTipped> fusionPoints = new Vector<PointTipped>();

    /**
     * Le tableau des triangulation affiché en fonction de fusion.
     */
    public Vector<Segment> triangulationFusion = new Vector<Segment>();

    /**
     * indique si le poligone est monotone
     */
    public Polygon.Type actualType = Polygon.Type.Simple;

    /* ************** */

    static class PointTipped extends Point {

        static enum Tip {
            None("?", 0b000), Left("L", 0b001), Right("R", 0b010), Up("U", 0b0111), Down("D", 0b1011);

            public final String str;
            public final int code;

            private Tip(String value, int code) {
                this.str = value;
                this.code = code;
            }

            public String toString() {
                return this.str;
            }
        }

        public PointTipped(Point p) {
            set(p);
            tip = Tip.None;
        }

        public PointTipped(Point p, Tip t) {
            set(p);
            tip = t;
        }

        public String toString() {
            return tip.toString() + "(" + super.toString() + ")";
        }

        /**
         * Position dans la le polygone monotone
         */
        public Tip tip;

    };

    /**
     * Algorithme qui recherche l'index du point en haut et en bas
     */
    private boolean searchUpDonwSide() {

        topPointIndex = -1;
        bottomPointIndex = -1;

        final int size = points.size();

        if (size < 3) {
            statusAddCause("Needs more than 3 points");
            return false;
        }

        //
        double topY = 0, bottomY = 0;

        //
        Point precPoint = null, precPrecPoint = null, currentPoint;
        int countAngle = 0;

        for (int i = 0; i < size + 2; i++) {

            //
            statusAddCounter();

            currentPoint = points.get(i % size);

            if (precPoint != null) {

                //
                if (currentPoint.y < topY) {
                    topY = currentPoint.y;
                    topPointIndex = i;
                } else if (currentPoint.y > bottomY) {
                    bottomY = currentPoint.y;
                    bottomPointIndex = i;
                }

                //
                if (precPrecPoint != null) {

                    if (((precPrecPoint.y > precPoint.y) && (currentPoint.y > precPoint.y)) || ((precPrecPoint.y < precPoint.y) && (currentPoint.y < precPoint.y))) {
                        countAngle++;

                        if (countAngle > 2) {
                            statusAddCause("not-monotone/bad-angle");
                            return false;
                        }
                    }
                }

            } else {
                topY = currentPoint.y;
                bottomY = currentPoint.y;
                topPointIndex = i;
                bottomPointIndex = i;
            }

            precPrecPoint = precPoint;
            precPoint = currentPoint;
        }

        //
        topPoint = points.get(topPointIndex);
        bottomPoint = points.get(bottomPointIndex);

        return true;
    }

    class SideTuple {
        Vector<Point> side = new Vector<Point>();
        double minX, maxX;
        Point pMin, pMax;

        public SideTuple(Point pInit) {
            side.add(pInit);
            maxX = minX = pInit.x;
            pMin = pMax = pInit;
        }

        public void add(Point p) {
            side.add(p);
            if (p.x > maxX) {
                maxX = p.x;
                pMax = p;
            } else if (p.x < minX) {
                minX = p.x;
                pMin = p;
            }
        }

    };

    /**
     * Algorithme qui fusionne les cotés des points
     */
    SideTuple makeSide(boolean clockwise, boolean withUpDown) {

        final int size = points.size();

        int start = Math.floorMod(topPointIndex + (withUpDown ? (clockwise ? 0 : -1) : (clockwise ? 1 : -1)), size);
        int end = Math.floorMod(bottomPointIndex + (withUpDown ? (clockwise ? 0 : -1) : (clockwise ? 0 : 0)), size);
        int add = clockwise ? 1 : -1;

        // No point in this side
        if (start == end)
            return null;

        // Add first
        Point p = points.get(start);
        final SideTuple st = new SideTuple(p);
        start = Math.floorMod(start + add, size);

        //
        for (int i = start; i != end; i = Math.floorMod(i + add, size)) {
            p = points.get(i);
            st.add(p);
        }

        return st;
    }

    /**
     * Algorithme qui fusionne les cotés des points
     */
    private boolean buildFusion() {
        if (!searchUpDonwSide())
            return false;
        //
        fusionPoints.clear();

        SideTuple sideClockwise = makeSide(true, false);
        SideTuple sideUnclockwise = makeSide(false, false);

        //
        if ((sideClockwise == null) && (sideUnclockwise == null)) {
            statusAddCause("No side-point detected");
            return false;
        }

        //
        Iterator<Point> leftPoint_it = null, rightPoint_it = null;

        //
        if ((sideClockwise != null) && (sideUnclockwise != null)) {

            final boolean normalSens = Calc.resumeProduitVectorielZ(topPoint, sideClockwise.pMin, bottomPoint) < //
            Calc.resumeProduitVectorielZ(topPoint, sideUnclockwise.pMax, bottomPoint);

            final Iterable<Point> sideRight = normalSens ? sideClockwise.side : sideUnclockwise.side;
            final Iterable<Point> sideLeft = normalSens ? sideUnclockwise.side : sideClockwise.side;

            rightPoint_it = sideRight.iterator();
            leftPoint_it = sideLeft.iterator();

        } else {

            final SideTuple oneSide = (sideClockwise != null) ? sideClockwise : sideUnclockwise;

            final boolean isRight = Calc.resumeProduitVectorielZ(topPoint, oneSide.pMin, bottomPoint) < 0;

            if (isRight) {
                rightPoint_it = oneSide.side.iterator();
                leftPoint_it = Collections.<Point>emptyIterator();
            } else {
                leftPoint_it = oneSide.side.iterator();
                rightPoint_it = Collections.<Point>emptyIterator();
            }

        }

        // Add Top
        fusionPoints.add(new PointTipped(topPoint, Tip.Up));

        //
        Point leftPoint = null, rightPoint = null;

        //
        if (leftPoint_it.hasNext())
            leftPoint = leftPoint_it.next();
        if (rightPoint_it.hasNext())
            rightPoint = rightPoint_it.next();

        //
        while (true) {

            //
            statusAddCounter();

            //
            if ((leftPoint != null) && (rightPoint != null)) {

                if (leftPoint.y <= rightPoint.y) {
                    fusionPoints.add(new PointTipped(leftPoint, Tip.Left));
                    leftPoint = (leftPoint_it.hasNext()) ? leftPoint_it.next() : null;
                } else {
                    fusionPoints.add(new PointTipped(rightPoint, Tip.Right));
                    rightPoint = (rightPoint_it.hasNext()) ? rightPoint_it.next() : null;
                }

            } else if (rightPoint_it.hasNext() || (rightPoint != null)) {
                if (rightPoint == null)
                    rightPoint = rightPoint_it.next();
                fusionPoints.add(new PointTipped(rightPoint, Tip.Right));
                rightPoint = null;
            } else if (leftPoint_it.hasNext() || (leftPoint != null)) {
                if (leftPoint == null)
                    leftPoint = leftPoint_it.next();
                fusionPoints.add(new PointTipped(leftPoint, Tip.Left));
                leftPoint = null;
            } else
                break;
        }

        // Add Bottom
        fusionPoints.add(new PointTipped(bottomPoint, Tip.Down));

        return true;
    }

    static boolean inP(Point pA, PointTipped pOrigine, Point pB) {
        double produit = Calc.resumeProduitVectorielZ(pA, pOrigine, pB);
        return (((produit > 0) && (pOrigine.tip == Tip.Right)) || //
                ((produit < 0) && (pOrigine.tip == Tip.Left)) || //
                (pOrigine.tip == Tip.Up) || (pOrigine.tip == Tip.Down));
    }


    /**
     * Algorithme qui Créer les segments de la triangulation
     * 
     * @see Computational Geometry - Algorithms and Applications -
     *      TRIANGULATEMONOTONEPOLYGON
     */
    private boolean buildTriangulation() {

        //
        if (intersectionTester != null) {
            if (intersectionTester.haveIntersections()) {
                statusAddCause("Polygon have intersection");
                return false;
            }
        }

        //
        if (!buildFusion())
            return false;

        //
        // drawVertexInformType();

        //
        triangulationFusion.clear();

        //
        ArrayDeque<PointTipped> stack = new ArrayDeque<PointTipped>();
        final int fusionPointsSize = fusionPoints.size();

        //
        stack.push(fusionPoints.get(0));
        stack.push(fusionPoints.get(1));

        //
        for (int i = 2; i < fusionPointsSize - 1; i++) {

            //
            statusAddCounter();

            //
            PointTipped currentPoint = fusionPoints.get(i);
            PointTipped stackFirst = stack.getFirst();

            // same chain
            if ((currentPoint.tip.code & stackFirst.tip.code) != 0) {

                PointTipped stackPop = stack.pop();

                //
                while (!stack.isEmpty()) {
                    stackFirst = stack.getFirst();

                    if (inP(currentPoint, stackPop, stackFirst)) {
                        stackPop = stack.pop();
                        triangulationFusion.add(new Segment(currentPoint, stackPop));

                    } else
                        break;
                }

                //
                stack.push(stackPop);
                stack.push(currentPoint);

            } else { // opposite chain

                //
                Iterator<PointTipped> stack_it = stack.descendingIterator();
                if (stack_it.hasNext()) {
                    stack_it.next(); // except the last one (in
                                     // descendingIterator its the first).
                    while (stack_it.hasNext()) {
                        PointTipped stackPoint = stack_it.next();
                        triangulationFusion.add(new Segment(currentPoint, stackPoint));
                    }
                }
                stack.clear();

                //
                stack.push(fusionPoints.get(i - 1));
                stack.push(currentPoint);
            }
        }

        //
        if (!stack.isEmpty()) {
            
            //
            Iterator<PointTipped> stack_it = stack.iterator();
            stack_it.next(); // except the first

            //
            PointTipped lastPoint = fusionPoints.lastElement();

            //
            if (stack_it.hasNext()) {
                do {
                    PointTipped pt = stack_it.next();

                    if (stack_it.hasNext())
                        triangulationFusion.add(new Segment(lastPoint, pt));
                    else
                        break; // and except the lasted one

                } while (true);

            }
        }

        return true;
    }

    /*
     * ****************************** DEBUG ONLY ******************************
     */

    public void drawTextTipPosDecal(String txt, Point p, int pos) {
        if (mutableVisitorForDebugging == null)
            return;
        final Point pToOrigin = new Point();
        pToOrigin.set(p);
        pToOrigin.y += 20 + pos * 15;
        poly.convertToStandard(pToOrigin);
        mutableVisitorForDebugging.drawTip(txt, pToOrigin);
    }

    public void drawSegmentByPoint(Point a, Point b) {
        if (mutableVisitorForDebugging == null)
            return;

        mutableVisitorForDebugging.getGraphicsContext().save();
        mutableVisitorForDebugging.getGraphicsContext().setLineWidth(5);
        mutableVisitorForDebugging.getGraphicsContext().setStroke(Color.YELLOW);

        final Segment sToOrigin = new Segment();
        sToOrigin.a.set(a);
        sToOrigin.b.set(b);
        poly.convertToStandard(sToOrigin.a);
        poly.convertToStandard(sToOrigin.b);

        mutableVisitorForDebugging.visit_unit(sToOrigin);
        mutableVisitorForDebugging.getGraphicsContext().restore();

    }

    public void drawVertexInformType() {
        if (mutableVisitorForDebugging == null)
            return;

        int counter = 0;

        for (PointTipped pt : fusionPoints) {

            final Point pToOrigin = new Point();
            pToOrigin.set(pt);
            poly.convertToStandard(pToOrigin);

            String type = "☺";

            switch (pt.tip) {
            case Down:
                type = "↓";
                break;
            case Left:
                type = "←";
                break;
            case None:
                break;
            case Right:
                type = "→";
                break;
            case Up:
                type = "↑";
                break;
            }

            pToOrigin.y += 20;
            mutableVisitorForDebugging.drawTip(type, pToOrigin);

            // pToOrigin.y += 10;
            // mutableVisitorForDebugging.drawTip(pt.toString(), pToOrigin);

            // pToOrigin.y += 10;
            // mutableVisitorForDebugging.drawTip(String.valueOf(counter++),
            // pToOrigin);

            /*
             * drawTextTipPosDecal( //
             * 
             * Integer.toHexString(System.identityHashCode(vi.back)) + "->" + //
             * Integer.toHexString(System.identityHashCode(vi)) + "->" + //
             * Integer.toHexString(System.identityHashCode(vi.next)) , vi, 4);
             */

        }
    }

};

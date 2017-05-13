package at.u4a.geometric_algorithms.algorithm;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import at.u4a.geometric_algorithms.algorithm.InterfaceAlgorithmBuilder;
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
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

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
            return (l.getShape() instanceof Polygon) || (l.getAlgorithm() instanceof ConvexEnvelope);
        }

        static int TriangulationCount = 1;

        @Override
        public AbstractLayer builder(AbstractLayer l) {

            //
            Polygon poly = null;
            if (l.getAlgorithm() instanceof ConvexEnvelope) {
                ConvexEnvelope ce = (ConvexEnvelope) l.getAlgorithm();
                poly = ce.getPolygon();
            } else if (l.getShape() instanceof Polygon) {
                poly = (Polygon) l.getShape();
            } else {
                throw new RuntimeException("Triangulation need a Polygon or ConvexEnvelope !");
            }

            //
            AbstractLayer al = new AlgorithmLayer<Triangulation>(new Triangulation(poly.perimeter, poly), Algorithm.Triangulation, l);
            al.setLayerName("t" + String.valueOf(TriangulationCount));
            TriangulationCount++;
            return al;
        }

    };

    private final AbstractList<Point> points;
    private final AbstractShape as;

    private final MonotonePolygon mp;

    public Triangulation(Vector<Point> points, AbstractShape as) {
        this.points = points;
        this.as = as;

        this.mp = new MonotonePolygon(as.origin, points);
    }

    /* ************** */

    @Override
    public void accept(Vector<AbstractLayer> v, InterfaceGraphicVisitor visitor) {

        makeTriangulation();

        //
        if (actualType == Polygon.Type.Monotone) {

            //
            final Segment sToOrigin = new Segment();
            for (Segment tf : triangulationFusion) {
                sToOrigin.set(tf);
                as.convertToStandard(sToOrigin.a);
                as.convertToStandard(sToOrigin.b);
                visitor.visit_unit(sToOrigin);
            }

        }

        //
        // visitor.drawEdgeTipFromList(poly, poly.perimeter);

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

    private int mutablePreviousPolyHash = 0;

    protected void makeTriangulation() {

        int currentPolyHash = as.hashCode();
        if (currentPolyHash != mutablePreviousPolyHash) {

            //
            actualType = Polygon.Type.Simple;
            if (buildTriangulation())
                actualType = Polygon.Type.Monotone;
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

        if (size < 3)
            return false;

        double topY = 0, bottomY = 0;

        Point precPoint = null, precPrecPoint = null, currentPoint;
        int countAngle = 0;

        for (int i = 0; i < size + 2; i++) {
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

                        if (countAngle > 2)
                            return false;
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

        topPoint = points.get(topPointIndex);
        bottomPoint = points.get(bottomPointIndex);

        return true;
    }

    class SideTuple {
        Vector<Point> side = new Vector<Point>();
        double minX, maxX;
    };

    /**
     * Algorithme qui fusionne les cotés des points
     */
    SideTuple makeSide(boolean clockwise, boolean withUpDown) {

        final int size = points.size();

        SideTuple st = new SideTuple();

        int start = Math.floorMod(topPointIndex + (withUpDown ? (clockwise ? 0 : -1) : (clockwise ? 1 : -1)), size);
        int end = Math.floorMod(bottomPointIndex + (withUpDown ? (clockwise ? 0 : -1) : (clockwise ? 0 : 0)), size);
        int add = clockwise ? 1 : -1;

        // Add first
        Point p = points.get(start);
        st.side.addElement(p);
        st.maxX = st.minX = p.x;
        start = Math.floorMod(start + add, size);

        //
        for (int i = start; i != end; i = Math.floorMod(i + add, size)) {

            p = points.get(i);
            st.side.addElement(p);

            if (p.x > st.maxX)
                st.maxX = p.x;
            else if (p.x < st.minX)
                st.minX = p.x;
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

        SideTuple sideClockwise = makeSide(true, true);
        SideTuple sideUnclockwise = makeSide(false, true);

        boolean normalSens = sideUnclockwise.minX < sideClockwise.maxX;

        Vector<Point> sideRight = normalSens ? sideClockwise.side : sideUnclockwise.side;
        Vector<Point> sideLeft = normalSens ? sideUnclockwise.side : sideClockwise.side;

        // Add Top
        fusionPoints.add(new PointTipped(topPoint, Tip.Up));

        //
        Point leftPoint = null, rightPoint = null;
        Iterator<Point> leftPoint_it = sideLeft.iterator();
        Iterator<Point> rightPoint_it = sideRight.iterator();
        //
        while (true) {
            if (leftPoint_it.hasNext() && rightPoint_it.hasNext()) {

                if (leftPoint == null)
                    leftPoint = leftPoint_it.next();
                if (rightPoint == null)
                    rightPoint = rightPoint_it.next();

                if (leftPoint.y <= rightPoint.y) {
                    fusionPoints.add(new PointTipped(leftPoint, Tip.Left));
                    leftPoint = null;
                } else {
                    fusionPoints.add(new PointTipped(rightPoint, Tip.Right));
                    rightPoint = null;
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
        // fusionPoints.add(new PointTipped(bottomPoint, Tip.Down));

        return true;
    }

    static boolean inP(Point pA, PointTipped pOrigine, Point pB) {
        double produit = Calc.resumeProduitVectorielZ(pA, pOrigine, pB);
        return (((produit > 0) && (pOrigine.tip == Tip.Right)) || ((produit < 0) && (pOrigine.tip == Tip.Left)) || (pOrigine.tip == Tip.Up) || (pOrigine.tip == Tip.Down));
    }

    /**
     * Algorithme qui Créer les segments de la triangulation
     * 
     * @see Computational Geometry - Algorithms and Applications -
     *      TRIANGULATEMONOTONEPOLYGON
     */
    private boolean buildTriangulation() {
        if (!buildFusion())
            return false;

        //
        triangulationFusion.clear();

        //
        ArrayDeque<PointTipped> stack = new ArrayDeque<PointTipped>();
        int fusionPointsSize = fusionPoints.size();

        //
        stack.add(fusionPoints.get(0));
        stack.add(fusionPoints.get(1));

        //
        for (int i = 2; i < fusionPointsSize - 1; i++) {

            PointTipped currentPoint = fusionPoints.get(i);
            PointTipped stackFirst = stack.getFirst();

            // same chain
            if ((currentPoint.tip.code & stackFirst.tip.code) != 0) {

                PointTipped stackPop = stack.pop();

                //
                while (!stack.isEmpty()) {
                    stackFirst = stack.peekFirst();

                    if (inP(currentPoint, stackPop, stackFirst)) {
                        stackPop = stack.pop();
                        triangulationFusion.add(new Segment(currentPoint, stackPop));
                    } else
                        break;
                }

                //
                stack.push(stackPop);
                stack.push(currentPoint);

            } else { // oposite chain

                //
                Iterator<PointTipped> stack_it = stack.descendingIterator();
                if (stack_it.hasNext()) {
                    PointTipped endStackPoint = stack_it.next();
                    while (stack_it.hasNext()) {
                        PointTipped stackPoint = stack_it.next();

                        // block vertex that can traverse edge of opposite side
                        if (endStackPoint != null)
                            if (inP(endStackPoint, stackPoint, currentPoint))
                                return false;

                        triangulationFusion.add(new Segment(currentPoint, stackPoint));
                        endStackPoint = null;
                    }
                }

                //
                stack.clear();

                //
                stack.push(fusionPoints.get(i - 1));
                stack.push(currentPoint);

            }
        }

        // except the first diagonals
        triangulationFusion.remove(0);

        return true;
    }
};
